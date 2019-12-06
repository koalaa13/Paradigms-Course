package md2html;

import java.io.IOException;

public abstract class MarkdownSource implements AutoCloseable {
    static final char END = '\0';

    int pos;
    private int line = 1;
    private int posInLine;
    private char c;

    protected abstract char readChar() throws Exception;

    public abstract void close() throws IOException;

    char getChar() {
        return c;
    }

    private String getLine() throws MarkdownException {
        StringBuilder sb = new StringBuilder();
        for (; ; ) {
            nextChar();
            if (c == '\n' || c == END) {
                break;
            }
            sb.append(c);
        }
        sb.append('\n');
        return sb.toString();
    }

    String getParagraph() throws MarkdownException {
        StringBuilder sb = new StringBuilder();
        String curLine;
        for (; ; ) {
            curLine = getLine();
            if (curLine.equals("\n") || curLine.equals(String.valueOf(END))) {
                break;
            }
            sb.append(curLine);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    private void skip() throws Exception {
        while (c == '\r') {
            c = readChar();
        }
    }

    private void nextChar() throws MarkdownException {
        try {
            if (c == '\n') {
                posInLine = 0;
                line++;
            }
            c = readChar();
            skip();
            pos++;
            posInLine++;
        } catch (final Exception e) {
            throw error("Source read error", e.getMessage());
        }
    }

    MarkdownException error(final String format, final Object... args) {
        return new MarkdownException(String.format("%d:%d %s", line, posInLine, String.format(format, args)));
    }

}
