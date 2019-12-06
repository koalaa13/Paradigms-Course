package expression.operations;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IncorrectConstException;
import expression.exceptions.ModulingByZeroException;
import expression.exceptions.OverflowException;

public class ByteOperations implements Operations<Byte> {
    
    public Byte parseNumber(final String s) throws IncorrectConstException {
        return (byte) Integer.parseInt(s);
    }

    public Byte add(final Byte x, final Byte y) throws OverflowException {
        return (byte) (x + y);
    }

    public Byte subtract(Byte x, Byte y) throws OverflowException {
        return (byte) (x - y);
    }

    public Byte divide(Byte x, Byte y) throws OverflowException, DivisionByZeroException {
        return (byte) (x / y);
    }

    public Byte multiply(Byte x, Byte y) throws OverflowException {
        return (byte) (x * y);
    }

    public Byte negate(Byte x) throws OverflowException {
        return (byte) (-x);
    }

    public Byte abs(Byte x) throws OverflowException {
        return (byte) Math.abs(x);
    }

    public Byte sqr(Byte x) throws OverflowException {
        return (byte) (x * x);
    }

    public Byte mod(Byte x, Byte y) throws DivisionByZeroException, ModulingByZeroException {
        return (byte) (x % y);
    }
}
