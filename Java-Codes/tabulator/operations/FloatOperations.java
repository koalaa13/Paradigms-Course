package expression.operations;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IncorrectConstException;
import expression.exceptions.ModulingByZeroException;
import expression.exceptions.OverflowException;

public class FloatOperations implements Operations<Float> {

    public Float parseNumber(final String s) throws IncorrectConstException {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            throw new IncorrectConstException();
        }
    }

    public Float add(final Float x, final Float y) throws OverflowException {
        return x + y;
    }

    public Float subtract(Float x, Float y) throws OverflowException {
        return x - y;
    }

    public Float divide(Float x, Float y) throws OverflowException, DivisionByZeroException {
        return x / y;
    }

    public Float multiply(Float x, Float y) throws OverflowException {
        return x * y;
    }

    public Float negate(Float x) throws OverflowException {
        return -x;
    }

    public Float abs(Float x) throws OverflowException {
        return Math.abs(x);
    }

    public Float sqr(Float x) throws OverflowException {
        return x * x;
    }

    public Float mod(Float x, Float y) throws DivisionByZeroException, ModulingByZeroException {
        return x % y;
    }
}
