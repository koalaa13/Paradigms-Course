package expression;

import expression.exceptions.OverflowException;

public class CheckedAdd extends AbstractBinaryOperation {
    public CheckedAdd(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    public int calc(int x, int y) throws OverflowException {
        Checkers.checkAdd(x, y);
        return x + y;
    }
}
