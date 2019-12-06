package expression.exceptions;

public class UnknownOperationException extends ParsingException {
    public UnknownOperationException(String s, String expession, int pos) {
        super(s, expession, pos);
    }
}
