// Christopher Ku
// Section: AG with Jiamae Wang
// Assessment 4: DNAStrand
//
// This DNAStrand clas represents a DNA strand that contains different
// Nucleotides that all combine together to represent a specific DNA
// strand. There are four different kinds of Nucleotides being A, C
// , G and T. There are specific instances within the DNA strand where
// sequences of Nucleotides will repeat itself in given patterns.
public class DNAStrand {
    private Nucleotide front; // This field represents the beginning of the DNA Strand instance

    /**
     * pre: Takes in a String parameter that contains individual Nucleotides
     *      that makes up the actual DNA being passed in.
     *
     * post: Constructs a DNAStrand instance that represents a DNA strand
     *       made up of Nucleotides.
     *
     * @param data   A String that represents a DNA.
     */
    public DNAStrand(String data) {
        this.front = new Nucleotide(data.charAt(0));
        Nucleotide current = this.front;
        for (int i = 1; i < data.length(); i++) {
            current.next = new Nucleotide(data.charAt(i));
            current = current.next;
        }
    }

    /**
     * pre: The passed in DNAStrand parameter named substrand must not
     *      be null or contain nothing. Or else this method will throw
     *      an IllegalArguementException if this precondition isn't met.
     *
     * post: Takes a DNAStrand parameter that represents a DNA substrand
     *       and counts the total consecutive repitions of the gvien
     *       substrand within the entire DNA Strand in this DNAStrand
     *       instance. Afterwards records the highest number of consecutive
     *       repitions of the substrand and returns an integer representing
     *       that number.
     *
     * @param substrand   A non-null DNAStrand representing a substrand of a DNA sequence
     *
     * @return maxRepeats   An integer representing the highest number of consecutive
     *                      repitions of the parsed in substrand within the current
     *                      DNAStrand instance.
     */
    public int maxConsecutiveRepeats(DNAStrand substrand) {
        if (substrand == null) {
            throw new IllegalArgumentException();
        }
        int maxRepeats = 0;
        Nucleotide current = this.front;
        Nucleotide originalNode = this.front;
        while (current != null) {
            originalNode = current;
            int countedNucleos = countSameNucleos(substrand, current);
            if (maxRepeats < countedNucleos) {
                maxRepeats = countedNucleos;
            }
            current = current.next;
        }
        return maxRepeats;
    }

    /**
     * pre: Accepts a non-null DNAStrand substrand as a prameter and a
     *      Nucleotide data type parameter representing the current nucleotide
     *      of examination or interest of the current DNAStrand instance.
     *
     * post: Counts the total number of occurences where the specific
     *       nucleotide of the parsed in substrand is the same as the current
     *       nucleotide of the current DNAStrand instance. Then returns an
     *       integer value representing the total counts of when a substrand
     *       nucleotide matches the nucleotide of the current DNAStrand instance.
     *
     * @param substrand   A non-null DNAStrand representing a substrand of a DNA sequence.
     *
     * @param current   A Nucleotide representing the current DNAStrand instance's current
     *                  nucleotide of interest or examination.
     *
     * @return count   An integer value represent the total total counts of when a substrand
     *                 nucleotide matches the nucleotide of the current DNAStrand instance.
     */
    private int countSameNucleos(DNAStrand substrand, Nucleotide current) {
        Nucleotide currentNucleotide = substrand.front;
        int count = 0;
        while (current != null && currentNucleotide.data == current.data) {
            currentNucleotide = currentNucleotide.next;
            current = current.next;
            if (currentNucleotide == null) {
                currentNucleotide = substrand.front;
                count++;
            }
        }
        return count;
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Nucleotide current = front; current != null; current = current.next) {
            result.append(current.data);
        }
        return result.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof DNAStrand)) {
            return false;
        }
        return this.toString().equals(o.toString());
    }

    public int hashCode() {
        return toString().hashCode();
    }

    private static class Nucleotide {
        public char data;
        public Nucleotide next;

        public Nucleotide(char data) {
            this.data = data;
            this.next = null;
        }
    }
    public static void main(String[] args) {
        DNAStrand dna = new DNAStrand("AATAAATAAT");
        DNAStrand str = new DNAStrand("AAT");
        System.out.println(dna.maxConsecutiveRepeats(str));
    }
}


