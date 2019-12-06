package expression;

import expression.exceptions.OverflowException;

public class Abs extends AbstractUnaryOperation {
    public Abs(TripleExpression a) {
        super(a);
    }

    public int calc(int x) throws OverflowException {
        Checkers.checkAbs(x);
        if (x < 0) {
            x = -x;
        }
        return x;
    }
}
