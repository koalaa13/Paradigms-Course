package expression.generic;

import expression.TripleExpression;
import expression.exceptions.EvaluatingException;
import expression.exceptions.ParsingException;
import expression.operations.*;
import expression.parser.ExpressionParser;

import java.util.HashMap;
import java.util.Map;

public class GenericTabulator implements Tabulator {
    private static Map<String, Operations<?>> types = new HashMap<>();

    static {
        types.put("i", new IntegerOperations(true));
        types.put("bi", new BigIntegerOperations(true));
        types.put("d", new DoubleOperations());
        types.put("u", new IntegerOperations(false));
        types.put("f", new FloatOperations());
        types.put("b", new ByteOperations());
    }

    public Object[][][] tabulate(final String mode, final String expression, final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) throws ParsingException, EvaluatingException {
        return calcTable(types.get(mode), expression, x1, x2, y1, y2, z1, z2);
    }

    private <T> Object[][][] calcTable(final Operations<T> operations, final String expression, final int x1, final int x2, final int y1, final int y2, final int z1, final int z2) throws ParsingException, EvaluatingException {
        int n = x2 - x1 + 1, m = y2 - y1 + 1, p = z2 - z1 + 1;
        Object[][][] res = new Object[n][m][p];
        ExpressionParser<T> parser = new ExpressionParser<>(operations);
        TripleExpression<T> exp = parser.parse(expression);
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                for (int k = 0; k < p; ++k) {
                    String x = Integer.toString(x1 + i);
                    String y = Integer.toString(y1 + j);
                    String z = Integer.toString(z1 + k);
                    try {
                        res[i][j][k] = exp.evaluate(operations.parseNumber(x), operations.parseNumber(y), operations.parseNumber(z));
                    } catch (Exception e) {
                        res[i][j][k] = null;
                    }
                }
            }
        }
        return res;
    }
}
