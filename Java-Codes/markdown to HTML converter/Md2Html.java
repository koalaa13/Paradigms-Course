package md2html;

import java.io.*;

public class Md2Html {
    public static void main(String[] args) throws MarkdownException {
        try (Writer writer = new BufferedWriter(new FileWriter(args[1]));
             FileMarkdownSource file = new FileMarkdownSource(args[0])) {
            MarkdownConverter converter = new MarkdownConverter(file);
            writer.write(converter.convert());
        } catch(Exception e) {
            System.out.printf("%s", e.getMessage());
        }
    }
}
