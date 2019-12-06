package expression;

import expression.exceptions.NegativeSqrtException;

public class Sqrt extends AbstractUnaryOperation {
    public Sqrt(TripleExpression a) {
        super(a);
    }

    public int calc(int x) throws NegativeSqrtException {
        Checkers.checkSqrt(x);
        int l = 0, r = 46400;
        while (r - l > 1) {
            int m = l + (r - l) / 2;
            if (m * m > x) {
                r = m;
            } else {
                l = m;
            }
        }
        return l;
    }
}
