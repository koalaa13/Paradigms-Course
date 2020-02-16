package ru.ifmo.rain.maksimov.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Walk {
    private final static int BUFFER_SIZE = 8192;

    public static String hash(String filename) {
        final int PRIME_KEY = 0x01000193;
        int res = 0x811c9dc5;
        try (InputStream fileInputStream = Files.newInputStream(Paths.get(filename))) {
            byte[] buf = new byte[BUFFER_SIZE];
            for (int readed = fileInputStream.read(buf, 0, BUFFER_SIZE); readed > 0; readed = fileInputStream.read(buf, 0, BUFFER_SIZE)) {
                for (int i = 0; i < readed; ++i) {
                    res *= PRIME_KEY;
                    res ^= buf[i] & 0xff;
                }
            }
        } catch (IOException e) {
            Helper.log("IOException while hashing file " + filename, e);
            // e.printStackTrace();
            return Helper.getOutputFormat(filename, 0);
        }
        return Helper.getOutputFormat(filename, res);
    }

    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Invalid arguments. Use Walk <input file> <output file>");
            return;
        }
        Path in, out;
        try {
            in = Paths.get(args[0]);
        } catch (InvalidPathException e) {
            Helper.log("Invalid input file: " + args[0], e);
            // e.printStackTrace();
            return;
        }

        try {
            out = Paths.get(args[1]);
        } catch (InvalidPathException e) {
            Helper.log("Invalid output file: " + args[1], e);
            // e.printStackTrace();
            return;
        }

        if (out.getParent() != null) {
            try {
                Files.createDirectories(out.getParent());
            } catch (IOException e) {
                Helper.log("Can not create output file", e);
                // e.printStackTrace();
                return;
            }
        }

        try (BufferedReader reader = Files.newBufferedReader(in);
             BufferedWriter writer = Files.newBufferedWriter(out)) {
            String filename = reader.readLine();
            while (filename != null) {
                try {
                    writer.write(hash(filename));
                } catch (FileNotFoundException e) {
                    Helper.log("No such file: " + filename, e);
                    writer.write(Helper.getOutputFormat(filename, 0));
                } catch (InvalidPathException e) {
                    Helper.log("Invalid path:" + filename, e);
                    writer.write(Helper.getOutputFormat(filename, 0));
                } catch (IOException e) {
                    Helper.log("Read or write error occurred", e);
                    writer.write(Helper.getOutputFormat(filename, 0));
                }
                filename = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            Helper.log("No such input or output file", e);
            // e.printStackTrace();
        } catch (IOException e) {
            Helper.log("Read or write error occurred", e);
            // e.printStackTrace();
        }
    }
}
