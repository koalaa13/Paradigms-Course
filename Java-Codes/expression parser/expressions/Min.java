package expression;

public class Min extends AbstractBinaryOperation {
    public Min(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    public int calc(int x, int y) {
        if (x < y) {
            return x;
        } else {
            return y;
        }
    }
}
