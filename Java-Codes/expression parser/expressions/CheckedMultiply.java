package expression;

import expression.exceptions.OverflowException;

public class CheckedMultiply extends AbstractBinaryOperation {
    public CheckedMultiply(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    public int calc(int x, int y) throws OverflowException {
        Checkers.checkMultiply(x, y);
        return x * y;
    }
}
