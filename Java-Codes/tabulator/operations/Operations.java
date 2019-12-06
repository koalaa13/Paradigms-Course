package expression.operations;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IncorrectConstException;
import expression.exceptions.ModulingByZeroException;
import expression.exceptions.OverflowException;

public interface Operations<T> {
    T parseNumber(final String s) throws IncorrectConstException;

    T add(final T x, final T y) throws OverflowException;

    T subtract(final T x, final T y) throws OverflowException;

    T divide(final T x, final T y) throws OverflowException, DivisionByZeroException;

    T multiply(final T x, final T y) throws OverflowException;

    T negate(final T x) throws OverflowException;

    T abs(final T x) throws OverflowException;

    T sqr(final T x) throws OverflowException;

    T mod(final T x, final T y) throws DivisionByZeroException, ModulingByZeroException;
}
