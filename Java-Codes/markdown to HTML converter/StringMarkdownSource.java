package md2html;

public class StringMarkdownSource extends MarkdownSource {
    private final String data;

    public StringMarkdownSource(String data) {
        this.data = data + END;
    }

    public void close() {}

    @Override
    protected char readChar() throws Exception {
        return data.charAt(pos);
    }
}
