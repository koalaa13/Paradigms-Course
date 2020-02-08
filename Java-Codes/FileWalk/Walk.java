package ru.ifmo.rain.maksimov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Walk {
    private final static int BUFFER_SIZE = 8192;

    private static void error(String mess) {
        System.err.println(mess);
        System.exit(0);
    }

    private static String hash(String filename) {
        final int PRIME_KEY = 0x01000193;
        int res = 0x811c9dc5;
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            byte[] buf = new byte[BUFFER_SIZE];
            for (int readed = fileInputStream.read(buf, 0, BUFFER_SIZE); readed > 0; readed = fileInputStream.read(buf, 0, BUFFER_SIZE)) {
                for (int i = 0; i < readed; ++i) {
                    res *= PRIME_KEY;
                    res ^= buf[i] & 0xff;
                }
            }
        } catch (IOException e) {
            return "00000000";
        }
        return String.format("%08x", res);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            error("Invalid count of arguments");
        }
        String inputFile = args[0];
        String outputFile = args[1];
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            String filename = reader.readLine();
            if (Paths.get(outputFile).getParent() != null) {
                Files.createDirectories(Paths.get(outputFile).getParent());
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));
            while (filename != null) {
                writer.write(hash(filename) + ' ' + filename + '\n');
                filename = reader.readLine();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            error("File with filenames to hash not found");
        } catch (IOException e) {
            error("An error occurred. " + e.getMessage());
        }
    }
}
