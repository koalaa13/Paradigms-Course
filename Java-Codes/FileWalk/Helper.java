package ru.ifmo.rain.maksimov.walk;

public class Helper {
    public static void log(String mess, Exception e) {
        System.err.println(mess + "\nError message: " + e.getMessage() + '\n');
    }

    static String getOutputFormat(String filename, int hash) {
        return String.format("%08x", hash) + ' ' + filename + '\n';
    }
}
