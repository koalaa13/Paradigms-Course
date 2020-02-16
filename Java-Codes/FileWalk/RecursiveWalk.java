package ru.ifmo.rain.maksimov.walk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveWalk {
    public static void main(String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.err.println("Invalid arguments. Use RecursiveWalk <input file> <output file>");
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
                    MyVisitor visitor = new MyVisitor(writer);
                    Files.walkFileTree(Paths.get(filename), visitor);
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
