package expression;

public class High extends AbstractUnaryOperation {
    public High(TripleExpression a) {
        super(a);
    }

    public int calc(int x) {
        return Integer.highestOneBit(x);
    }
}
