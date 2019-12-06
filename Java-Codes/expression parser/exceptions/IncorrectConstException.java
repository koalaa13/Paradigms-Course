package expression.exceptions;

public class IncorrectConstException extends ParsingException {
    public IncorrectConstException(String s, String expression, int pos) {
        super(s, expression, pos);
    }
}
