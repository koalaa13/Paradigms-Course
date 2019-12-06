package expression.exceptions;

public class ModulingByZeroException extends EvaluatingException {
    public ModulingByZeroException() {
        super("Taking modulo by zero");
    }

    public ModulingByZeroException(String reason) {
        super(reason);
    }
}
