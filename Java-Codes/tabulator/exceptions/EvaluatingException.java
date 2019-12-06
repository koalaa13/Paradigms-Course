package expression.exceptions;

public class EvaluatingException extends Exception {
    EvaluatingException(String reason) {
        super(reason);
    }
}
