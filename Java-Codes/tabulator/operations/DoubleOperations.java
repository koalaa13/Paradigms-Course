package expression.operations;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IncorrectConstException;

public class DoubleOperations implements Operations<Double> {

    public Double parseNumber(final String s) throws IncorrectConstException {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            throw new IncorrectConstException();
        }
    }

    public Double abs(final Double x) {
        return Math.abs(x);
    }

    public Double sqr(final Double x) {
        return x * x;
    }

    public Double mod(final Double x, final Double y) {
        return x % y;
    }

    public Double add(final Double x, final Double y) {
        return x + y;
    }

    public Double subtract(final Double x, final Double y) {
        return x - y;
    }

    public Double multiply(final Double x, final Double y) {
        return x * y;
    }

    public Double negate(final Double x) {
        return -x;
    }

    public Double divide(final Double x, final Double y) {
        return x / y;
    }
}
