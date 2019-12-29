public class MyLZW {
    private static final int MIN_BIT = 9;    // Initial bit width
    private static final int MAX_BIT = 16;   // Maximum bit width
    private static final double RATIO = 1.1;  // Monitor mode compression ratio threshold
    private static final int R = 256;               // number of input chars
    private static int W = MIN_BIT;           // codeword width
    private static int L = (int) Math.pow(2, W);    // number of codewords = 2^W
    private static char mode;   // Char to hold compression mode value

    public static void compress() { 
        double oldCompRatio = 0.0;  // For monitor mode ratio comparison
        double newCompRatio = 0.0;  // For monitor mode ratio comparison
        double monitorRatio = 0.0;  // ratio compared to 1.1
        int compSize = 0;   // Size of input data
        int uncompSize = 0; // Size of compressed data

        String input = BinaryStdIn.readString();    // Reads in entire file data
        TST<Integer> st = new TST<Integer>();       // Creates new TST as our ST

        for (int i = 0; i < R; i++)     // Initializes TST symbol table
            st.put("" + (char) i, i);

        int code = R + 1;  // R is codeword for EOF

        BinaryStdOut.write((byte) mode);    // Writes mode char to beginning of compressed file for expansion

        while (input.length() > 0) {    // Loop until file is fully read
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            int t = s.length();

            uncompSize += t * 16;   // Add char data * Size of char
            compSize += W;          // Add size of code word (W)

            if (t < input.length() && code < L){   // Add s to symbol table.
                oldCompRatio = (double) uncompSize / compSize;  // Set old compression ratio
                st.put(input.substring(0, t + 1), code++);
            }
            else if (t < input.length() && code >= L) { // Full codebook for specific bit width
                if (W < MAX_BIT) { // Codebook is full with W < 16, so expand bit width by 1
                    L = (int) Math.pow(2, ++W);     // Increment bit width
                    st.put(input.substring(0, t + 1), code++);
                }
                else if (W == MAX_BIT) {    // Codebook is full and we can't expand bit width anymore
                    if (mode == 'r') { // Do reset mode
                        W = MIN_BIT;    // Reset bit width
                        L = (int) Math.pow(2, W);
                        st = new TST<Integer>();
                        for (int i = 0; i < R; i++)     // Overwrite old ST with new ST
                            st.put("" + (char) i, i);
                        code = R + 1; // R is codeword for EOF
                        st.put(input.substring(0, t + 1), code++);  // Add new codeword
                    }
                    else if (mode == 'm') { // Do monitor mode
                        newCompRatio = (double) (uncompSize / compSize);
                        monitorRatio = oldCompRatio / newCompRatio;

                        if (monitorRatio > RATIO) { // Reset codebook if threshold is surpassed
                            st = new TST<Integer>();
                            for (int i = 0; i < R; i++)     // Overwrite old ST with new ST
                                st.put("" + (char) i, i);
                            code = R + 1; // R is codeword for EOF
                        }
                        st.put(input.substring(0, t + 1), code++);  // Add new codeword
                    }
                    // If mode == 'n', do nothing
                }
            }
            input = input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 


    public static void expand() {
        double oldCompRatio = 0.0;  // For monitor mode ratio comparison
        double newCompRatio = 0.0;  // For monitor mode ratio comparison
        double monitorRatio = 0.0;  // ratio compared to 1.1
        int compSize = 0;   // Size of input data
        int uncompSize = 0; // Size of compressed data

        String[] st = new String[(int) Math.pow(2, MAX_BIT)];   // Creates array ST with the max amount of codewords possible

        // initialize symbol table with all 1-character strings
        int i;
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";                        // (unused) lookahead for EOF
        i = 0;  // Reset iterator

        int j = R + 1;  // next available codeword value
        mode = BinaryStdIn.readChar();  // First char in compressed file is mode
        int codeword = BinaryStdIn.readInt(W);
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {  // Loop until whole file is fully read
            uncompSize += (val.length() * 16);   // Add char data * Size of char
            compSize += W;          // Add size of code word (W)

            BinaryStdOut.write(val);
            if (j >= L) {   // Out of room to add codewords
                if (W < MAX_BIT) {  // We can expand the codebook bit width and continue adding
                    oldCompRatio = (double) (uncompSize / compSize); // Set old compression ratio
                    L = (int) Math.pow(2, ++W);     // Increment bit width by 1
                }
                else if (W == MAX_BIT) {    // No more room in the codebook
                    if (mode == 'r') { // Do reset mode
                        for (i = 0; i < R; i++)     // Overwrite old ST array with new one
                            st[i] = "" + (char) i;
                        st[i++] = "";                        // (unused) lookahead for EOF
                        i = 0;  // Reset iterator
                        j = R + 1;
                        W = MIN_BIT; // Reset bit width
                        L = (int) Math.pow(2, W);
                    }
                    else if (mode == 'm') { // Do monitor mode
                        newCompRatio = (double) (uncompSize / compSize);    // Set new compression ratio
                        monitorRatio = oldCompRatio / newCompRatio;
                        if (monitorRatio > RATIO) {    // Reset codebook if threshold is surpassed
                            for (i = 0; i < R; i++)     // Overwrite old ST array with new one
                                st[i] = "" + (char) i;
                            st[i++] = "";                        // (unused) lookahead for EOF
                            i = 0;  // Reset iterator
                            j = R + 1;
                            W = MIN_BIT; // Reset bit width
                            L = (int) Math.pow(2, W);
                            oldCompRatio = 0.0;
                        }
                    }
                    // If mode == 'n', do nothing
                }
            }


            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (j == codeword) s = val + val.charAt(0);   // special case hack
            if (j < L) {
                oldCompRatio = (double) (uncompSize / compSize);    // Set old compression ratio
                st[j++] = val + s.charAt(0);
            }
            val = s;
        }
        BinaryStdOut.close();
    }



    public static void main(String[] args) {
        if (args.length >= 2) { // If +/- and mode are present, store all and compress
            if (args[1].length() == 1 && (args[1].equals("n") || args[1].equals("r") || args[1].equals("m"))) {
                mode = args[1].charAt(0);   // Set mode to first command line arg (gets first char of one char string)
            }
        }
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}