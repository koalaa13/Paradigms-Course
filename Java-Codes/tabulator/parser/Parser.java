package expression.parser;

import expression.TripleExpression;

public interface Parser<T> {
    TripleExpression<T> parse(String expression) throws Exception;
}
