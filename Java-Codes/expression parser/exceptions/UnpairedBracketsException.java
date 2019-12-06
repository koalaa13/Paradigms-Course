package expression.exceptions;

public class UnpairedBracketsException extends ParsingException {
    public UnpairedBracketsException(String s, String expression, int pos) {
        super(s, expression, pos);
    }
}
