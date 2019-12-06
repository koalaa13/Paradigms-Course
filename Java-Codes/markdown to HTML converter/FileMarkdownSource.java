package md2html;

import java.io.*;

public class FileMarkdownSource extends MarkdownSource {
    private final Reader reader;

    FileMarkdownSource(final String fileName) throws MarkdownException {
        try {
            reader = new BufferedReader(new FileReader(fileName));
        } catch (IOException e) {
            throw error("Error opening file '%s': %s", fileName, e.getMessage());
        }
    }

    public void close() throws IOException {
        reader.close();
    }

    @Override
    protected char readChar() throws Exception {
        final int c = reader.read();
        if (c == -1) {
            return END;
        } else {
            return (char) c;
        }
    }
}
