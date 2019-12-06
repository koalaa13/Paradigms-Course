package expression;

import expression.exceptions.EvaluatingException;
import expression.exceptions.ParsingException;

public abstract class AbstractUnaryOperation implements TripleExpression {
    private TripleExpression first;

    AbstractUnaryOperation(TripleExpression a) {
        first = a;
    }

    protected abstract int calc(int x) throws ParsingException, EvaluatingException;

    public int evaluate(int x, int y, int z) throws ParsingException, EvaluatingException {
        return calc(first.evaluate(x, y, z));
    }

}
