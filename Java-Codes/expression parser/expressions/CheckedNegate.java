package expression;

import expression.exceptions.OverflowException;

public class CheckedNegate extends AbstractUnaryOperation {
    public CheckedNegate(TripleExpression a) {
        super(a);
    }

    public int calc(int x) throws OverflowException {
        Checkers.checkNegate(x);
        return -x;
    }
}
