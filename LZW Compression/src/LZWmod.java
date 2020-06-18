
/*************************************************************************
 * Compilation: javac LZW.java 
 * Execution: java LZW - < input.txt (compress)
 * Execution: java LZW + < input.txt (expand) Dependencies: BinaryIn.java
 * BinaryOut.java
 *
 * Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

import java.io.*;

public class LZWmod {
    private static InputStream in = new BufferedInputStream(System.in);

    private static final int R = 256; // number of input chars
    private static int W = 9; // codeword width (start 16)
    private static int L = 512; // number of codewords = 2^W

    private static String flag;

    public static void compress() throws IOException {
        StringBuilder input = new StringBuilder();
        TrieSTNew<Integer> st = new TrieSTNew<Integer>();

        for (int i = 0; i < R; i++) {
            input.append((char) i);
            st.put(input, i);
            input.setLength(0);
        }
        int code = R + 1; // R is codeword for EOF

        if (flag.equals("n")) {  BinaryStdOut.write('n'); }
        else if(flag.equals("r")) { BinaryStdOut.write('r'); }
        else { throw new IllegalArgumentException(); }

        int i; // current byte
        char temp; // holds last byte
        while ((i = in.read()) != -1) {
            input.append((char) i);
            if (st.get(input) == null) {
                temp = input.charAt(input.length() - 1);
                input.setLength(input.length() - 1);
                BinaryStdOut.write(st.get(input), W);
                if (code == L && W < 16) { incrementParams(); } // expand up to 16-bit
                if (flag.equals("r") && code == L && W == 16) { // reset dictionary
                    st = new TrieSTNew<>();
                    StringBuilder re = new StringBuilder();
                    for (int k = 0; k < R; k++) {
                        re.append((char) k);
                        st.put(re, k);
                        re.setLength(0);
                    }
                    resetParams(); // 9-bit : 512 codewords;
                    code = R + 1; // reset codeword "index" after ASCII and EOF
                }
                if (code < L) {
                    input.append(temp);
                    st.put(input, code++); // add to dict
                }
                input.setLength(0);
                input.append(temp);
            }
        }
        /*
        input.append((char) in.read());
        while (true) {
            // input not in dict
            if (st.get(input) == null) {
                temp = input.charAt(input.length() - 1);
                input.setLength(input.length() - 1);
                BinaryStdOut.write(st.get(input), W);
                if (code == L && W < 16) { incrementParams(); } // expand up to 16-bit
                if (flag.equals("r") && code == L && W == 16) { // reset dictionary
                    st = new TrieSTNew<Integer>();
                    StringBuilder init = new StringBuilder();
                    for (int k = 0; k < R; k++) {
                        init.append((char) k);
                        st.put(init, k);
                        init.setLength(0);
                    }
                    resetParams(); // 9-bit : 512 codewords;
                    code = R + 1; // reset codeword "index" after ASCII and EOF
                }
                if (code < L) {
                    input.append(temp);
                    st.put(input, code++);
                }
                input.setLength(0);
                input.append(temp);
            // input is prefix
            } else {
                i = in.read();
                if (i == -1) { break; } // EOF
                else { input.append((char) i); } // add next char
            }
        }
        */
        BinaryStdOut.write(input.toString(), W);
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    }
    
    public static void expand() {
        String[] st = new String[65536]; 
        int i; 
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = ""; // (unused) lookahead for EOF

        char type = BinaryStdIn.readChar(); // compression type (r/n)
        int codeword = BinaryStdIn.readInt(W);
        String val = st[codeword];
        while (true) {
            BinaryStdOut.write(val);
            if (i == L && W < 16) { incrementParams(); }
            if (type == 'r' && i == L && W == 16) {
                st = new String[65536];
                for (i = 0; i < R; i++)
                    st[i] = "" + (char) i;
                st[i++] = ""; // (unused) lookahead for EOF
                resetParams();
            }
            codeword = BinaryStdIn.readInt(W);
            if (codeword == R) { break; } // EOF
            String s = st[codeword];
            if (i == codeword) { s = val + val.charAt(0); }
            if (i < L) { st[i++] = val + s.charAt(0); } // for 'n' case
            val = s;
        }
        BinaryStdOut.close();
    }

    public static void incrementParams() {
        W++;
        L *= 2;
    }

    public static void resetParams() {
        W = 9;
        L = 512;
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) flag = args[1];
        if (args[0].equals("-"))        compress();
        else if (args[0].equals("+"))   expand();
        else    throw new RuntimeException("Illegal command line argument");
    }

}