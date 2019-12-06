package expression.exceptions;

public class MissingOperandException extends ParsingException {
    public MissingOperandException(String expression, int pos) {
        super("Missing operand in expression", expression, pos);
    }
}
