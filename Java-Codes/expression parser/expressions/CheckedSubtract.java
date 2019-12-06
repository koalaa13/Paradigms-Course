package expression;

import expression.exceptions.OverflowException;

public class CheckedSubtract extends AbstractBinaryOperation {
    public CheckedSubtract(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    public int calc(int x, int y) throws OverflowException {
        Checkers.checkSubtract(x, y);
        return x - y;
    }
}
