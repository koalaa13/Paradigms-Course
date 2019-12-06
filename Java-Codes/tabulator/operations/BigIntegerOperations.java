package expression.operations;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IncorrectConstException;
import expression.exceptions.ModulingByZeroException;
import expression.exceptions.OverflowException;

import java.math.BigInteger;

public class BigIntegerOperations implements Operations<BigInteger> {

    private final boolean flag;

    public BigIntegerOperations(final boolean toCheck) {
        flag = toCheck;
    }

    public BigInteger parseNumber(final String s) throws IncorrectConstException {
        try {
            return new BigInteger(s);
        } catch (NumberFormatException e) {
            throw new IncorrectConstException();
        }
    }

    public BigInteger add(final BigInteger x, final BigInteger y) {
        return x.add(y);
    }

    public BigInteger subtract(final BigInteger x, final BigInteger y) {
        return x.subtract(y);
    }

    public BigInteger multiply(final BigInteger x, final BigInteger y) {
        return x.multiply(y);
    }

    private void checkDivide(final BigInteger y) throws DivisionByZeroException {
        if (y.equals(BigInteger.ZERO)) {
            throw new DivisionByZeroException();
        }
    }

    public BigInteger divide(final BigInteger x, final BigInteger y) throws DivisionByZeroException {
        if (flag) {
            checkDivide(y);
        }
        return x.divide(y);
    }

    public BigInteger negate(final BigInteger x) {
        return x.negate();
    }

    public BigInteger abs(final BigInteger x) throws OverflowException {
        return x.abs();
    }

    public BigInteger sqr(final BigInteger x) throws OverflowException {
        return x.multiply(x);
    }

    private void checkMod(final BigInteger y) throws ModulingByZeroException {
        if (y.equals(BigInteger.ZERO)) {
            throw new ModulingByZeroException();
        }
    }

    public BigInteger mod(final BigInteger x, final BigInteger y) throws DivisionByZeroException, ModulingByZeroException {
        if (flag) {
            checkMod(y);
        }
        return x.mod(y);
    }
}
