package expression;

import expression.exceptions.EvaluatingException;
import expression.exceptions.ParsingException;

public abstract class AbstractBinaryOperation implements TripleExpression {
    private TripleExpression first, second;

    AbstractBinaryOperation(TripleExpression a, TripleExpression b) {
        first = a;
        second = b;
    }

    protected abstract int calc(int x, int y) throws EvaluatingException, ParsingException;

    public int evaluate(int x, int y, int z) throws EvaluatingException, ParsingException {
        return calc(first.evaluate(x, y, z), second.evaluate(x, y, z));
    }
}
