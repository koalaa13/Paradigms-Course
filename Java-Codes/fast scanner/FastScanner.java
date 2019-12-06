import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;

import javafx.util.Pair;

public class FastScanner {
    final private int BUFFER_SIZE = (1 << 16);
    private DataInputStream in;
    private byte[] buffer;
    private int bufferPointer, bytesRead;

    public FastScanner() {
        in = new DataInputStream(System.in);
        buffer = new byte[BUFFER_SIZE];
        bufferPointer = 0;
        bytesRead = 0;
    }

    public FastScanner(String fileName) {
        try {
            in = new DataInputStream(new FileInputStream(fileName));
            buffer = new byte[BUFFER_SIZE];
            bufferPointer = 0;
            bytesRead = 0;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void fillBuffer() {
        try {
            bufferPointer = 0;
            do {
                bytesRead = in.read(buffer, 0, BUFFER_SIZE);
            } while (bytesRead == 0);
            if (bytesRead == -1) {
                buffer[0] = -1;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private byte arrayBoolsToByte(boolean b[]) {
        byte res = 0;
        for (boolean c : b) {
            res <<= 1;
            if (c) {
                res |= 1;
            }
        }
        return res;
    }

    private boolean[] byteToArrayBools(byte b) {
        boolean[] res = new boolean[8];
        byte b1 = b;
        for (int i = 7; i >= 0; --i) {
            if ((b1 & 1) == 1) {
                res[i] = true;
            } else {
                res[i] = false;
            }
            b1 >>= 1;
        }
        return res;
    }

    private byte read() {
        if (bufferPointer == bytesRead) {
            fillBuffer();
        }
        return buffer[bufferPointer++];
    }

    private Pair<Boolean, Character> readChar() {
        byte firstByte = read();
        int needBytes = 1, cnt = 0;
        for (int i = 7; i >= 0 && (firstByte & (1 << i)) != 0; --i) {
            ++cnt;
        }
        if (cnt > 0) {
            needBytes += cnt - 1;
        }
        byte[] bytesArray = new byte[needBytes];
        Boolean flag = true;
        bytesArray[0] = firstByte;
        for (int i = 1; i < needBytes; ++i) {
            byte nextByte = read();
            if (nextByte == -1) {
                flag = false;
            }
            bytesArray[i] = nextByte;
        }
        Character c = new String(bytesArray, StandardCharsets.UTF_8).toCharArray()[0];
        Pair<Boolean, Character> res = new Pair<>(flag, c);
        return res;
    }

    public boolean hasNext() {
        if (bufferPointer == bytesRead) {
            fillBuffer();
        }
        return bytesRead != -1;
    }

    public String nextLine() {
        StringBuffer res = new StringBuffer();
        Pair<Boolean, Character> c = readChar();
        while (c.getKey() != false) {
            if (c.getValue() == '\n') {
                break;
            }
            res.append(c.getValue());
            c = readChar();
        }
        return res.toString();
    }

    public int nextInt() {
        int res = 0;
        byte c = read();
        boolean neg = false;
        while (!((c >= '0' && c <= '9') || c == '-')) {
            c = read();
        }
        if (c == '-') {
            neg = true;
            c = read();
        }
        do {
            res = res * 10 + (c - '0');
            c = read();
        } while (c >= '0' && c <= '9');
        if (neg) {
            res = -res;
        }
        return res;
    }

    public long nextLong() {
        long res = 0;
        byte c = read();
        boolean neg = false;
        while (!((c >= '0' && c <= '9') || c == '-')) {
            c = read();
        }
        if (c == '-') {
            neg = true;
            c = read();
        }
        do {
            res = res * 10 + (c - '0');
            c = read();
        } while (c >= '0' && c <= '9');
        if (neg) {
            res = -res;
        }
        return res;
    }

    public double nextDouble() {
        double res = 0, p = 1;
        byte c = read();
        boolean neg = false;
        while (!((c >= '0' && c <= '9') || c == '-')) {
            c = read();
        }
        if (c == '-') {
            neg = true;
            c = read();
        }
        do {
            res = res * 10 + (c - '0');
            c = read();
        } while (c >= '0' && c <= '9');
        if (c == '.') {
            c = read();
            while (c >= '0' && c <= '9') {
                res += (c - '0') / (p *= 10);
            }
        }
        if (neg) {
            res = -res;
        }
        return res;
    }

    public static boolean isIntNum(char c) {
        return (c >= '0' && c <= '9') || c == '-';
    }

    public static boolean isDoubleNum(char c) {
        return isIntNum(c) || c == '.';
    }

    public static boolean isWord(char c) {
        return Character.isLetter(c) || c == '\'' || Character.getType(c) == Character.DASH_PUNCTUATION;
    }

    public static ArrayList<Integer> integersFromString(String s) {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
            if (isIntNum(s.charAt(i))) {
                int j = i;
                while (j < s.length() && isIntNum(s.charAt(j))) {
                    ++j;
                }
                String num = s.substring(i, j);
                res.add(new Integer(num));
                i = j - 1;
            }
        }
        return res;
    }

    public static ArrayList<String> wordsFromString(String s) {
        ArrayList<String> res = new ArrayList<>();
        for (int i = 0; i < s.length(); ++i) {
            if (isWord(s.charAt(i))) {
                int j = i;
                while (j < s.length() && isWord(s.charAt(j))) {
                    ++j;
                }
                String newWord = s.substring(i, j);
                res.add(newWord);
                i = j - 1;
            }
        }
        return res;
    }

    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
