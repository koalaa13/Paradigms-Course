package ru.itmo.rain.maksimov.walk;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Helper {
    public static void log(String mess, Exception e) {
        String out = mess + System.lineSeparator();
        if (e != null) {
            out += "Error message: " + e.getMessage() + System.lineSeparator();
        }
        System.err.println(out);
    }

    public static String getOutputFormat(String filename, int hash) {
        return String.format("%08x", hash) + ' ' + filename + System.lineSeparator();
    }
}
