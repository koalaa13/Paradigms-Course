package expression.exceptions;

public class MissingOperationException extends ParsingException {
    public MissingOperationException(String expression, int pos) {
        super("Missing operation in expression", expression, pos);
    }
}
