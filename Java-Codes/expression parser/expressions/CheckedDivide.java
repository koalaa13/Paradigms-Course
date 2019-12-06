package expression;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.OverflowException;

public class CheckedDivide extends AbstractBinaryOperation {
    public CheckedDivide(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    public int calc(int x, int y) throws OverflowException, DivisionByZeroException {
        Checkers.checkDivide(x, y);
        return x / y;
    }
}
