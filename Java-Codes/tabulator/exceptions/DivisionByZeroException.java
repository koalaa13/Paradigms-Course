package expression.exceptions;

public class DivisionByZeroException extends EvaluatingException {
    public DivisionByZeroException() {
        super("Division by zero");
    }

    public DivisionByZeroException(String reason) {
        super(reason);
    }

}
