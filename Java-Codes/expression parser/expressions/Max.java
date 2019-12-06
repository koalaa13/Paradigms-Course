package expression;

public class Max extends AbstractBinaryOperation {
    public Max(TripleExpression a, TripleExpression b) {
        super(a, b);
    }

    public int calc(int x, int y) {
        if (x > y) {
            return x;
        } else {
            return y;
        }
    }
}
