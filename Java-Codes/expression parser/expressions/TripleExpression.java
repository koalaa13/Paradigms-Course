package expression;

import expression.exceptions.EvaluatingException;
import expression.exceptions.ParsingException;

public interface TripleExpression {
    int evaluate(int x, int y, int z) throws EvaluatingException, ParsingException;
}
