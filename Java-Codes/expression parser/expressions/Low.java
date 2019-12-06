package expression;

public class Low extends AbstractUnaryOperation {
    public Low(TripleExpression a) {
        super(a);
    }

    public int calc(int x) {
        return Integer.lowestOneBit(x);
    }
}
