package ru.ifmo.rain.maksimov.walk;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveWalk {
    public static void main(String[] args) {
        if (args.length != 2) {
            Helper.error("Invalid count of arguments");
        }
        String inputFile = args[0];
        String outputFile = args[1];
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8))) {
            String line = reader.readLine();
            if (Paths.get(outputFile).getParent() != null) {
                Files.createDirectories(Paths.get(outputFile).getParent());
            }
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));
            while (line != null) {
                try {
                    Files.walkFileTree(Paths.get(line), new MyVisitor(writer));
                } catch (IOException ignored) {} // It's a check for nonexistent folder
                line = reader.readLine();
            }
            writer.close();
        } catch (FileNotFoundException e) {
            Helper.error("File with filenames to hash not found");
        } catch (IOException e) {
            Helper.error("An error occurred. \nError message: " + e.getMessage());
        }
    }

    private static class MyVisitor extends SimpleFileVisitor<Path> {
        private final Writer writer;

        private MyVisitor(Writer writer) {
            this.writer = writer;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            writer.write(Walk.hash(file.toString()));
            return FileVisitResult.CONTINUE;
        }
    }
}
