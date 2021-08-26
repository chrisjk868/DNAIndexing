import java.util.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import com.sun.net.httpserver.*;

public class Server {
    // Port number used to connect to this server
    private static final int PORT = Integer.parseInt(System.getenv().getOrDefault("PORT", "8000"));
    // JSON endpoint structure
    private static final String QUERY_TEMPLATE = "{\"items\":[%s]}";

    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length != 1) {
            throw new IllegalArgumentException("java Server [csv file]");
        }
        String filename = args[0];
        Map<String, Map<DNAStrand, Integer>> fingerprints = new LinkedHashMap<>();
        try (Scanner input = new Scanner(new File(filename))) {
            String header = input.nextLine();
            DNAStrand[] strs = Arrays.stream(header.split(","))
                                     .skip(1) // Skip the "name" header
                                     .map(DNAStrand::new)
                                     .toArray(DNAStrand[]::new);
            while (input.hasNextLine()) {
                String[] fields = input.nextLine().split(",");
                String name = fields[0];
                fingerprints.put(name, new LinkedHashMap<>());
                for (int i = 1; i < fields.length; i += 1) {
                    int expected = Integer.parseInt(fields[i]);
                    fingerprints.get(name).put(strs[i - 1], expected);
                }
            }
        }
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("index.html"));
            send(t, "text/html; charset=utf-8", html);
        });
        server.createContext("/sequences.json", (HttpExchange t) -> {
            String html = Files.readString(Paths.get("sequences.json"));
            send(t, "application/json", html);
        });
        server.createContext("/query", (HttpExchange t) -> {
            String s = parse(t.getRequestBody());
            if (s.equals("")) {
                send(t, "application/json", String.format(QUERY_TEMPLATE, ""));
                return;
            }
            DNAStrand dna = new DNAStrand(s);
            Map<String, Boolean> matches = new LinkedHashMap<>();
            for (String name : fingerprints.keySet()) {
                Map<DNAStrand, Integer> fingerprint = fingerprints.get(name);
                for (DNAStrand str : fingerprint.keySet()) {
                    int expected = fingerprint.get(str);
                    int actual = dna.maxConsecutiveRepeats(str);
                    matches.merge(name, expected == actual, Boolean::logicalAnd);
                }
            }
            send(t, "application/json", String.format(QUERY_TEMPLATE, json(matches)));
        });
        server.setExecutor(null);
        server.start();
    }

    private static String parse(InputStream postData) {
        try (Scanner input = new Scanner(postData)) {
            input.nextLine(); // Skip form boundary
            input.nextLine(); // Skip Content-Disposition
            input.nextLine(); // Skip blank line
            return input.nextLine();
        }
    }

    private static void send(HttpExchange t, String contentType, String data)
            throws IOException, UnsupportedEncodingException {
        t.getResponseHeaders().set("Content-Type", contentType);
        byte[] response = data.getBytes("UTF-8");
        t.sendResponseHeaders(200, response.length);
        try (OutputStream os = t.getResponseBody()) {
            os.write(response);
        }
    }

    private static String json(Map<String, Boolean> matches) {
        StringBuilder results = new StringBuilder();
        for (String name : matches.keySet()) {
            boolean match = matches.get(name);
            if (results.length() > 0) {
                results.append(',');
            }
            results.append('{')
                   .append("\"name\":")
                   .append('"').append(name.replace("\"", "\\\"")).append('"')
                   .append(',')
                   .append("\"match\":")
                   .append(match)
                   .append('}');
        }
        return results.toString();
    }
}
