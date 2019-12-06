package expression.operations;

import expression.exceptions.DivisionByZeroException;
import expression.exceptions.IncorrectConstException;
import expression.exceptions.ModulingByZeroException;
import expression.exceptions.OverflowException;

public class IntegerOperations implements Operations<Integer> {

    private final boolean flag;

    public IntegerOperations(final boolean toCheck) {
        flag = toCheck;
    }

    public Integer parseNumber(final String s) throws IncorrectConstException {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IncorrectConstException();
        }
    }

    private void checkAdd(final Integer x, final Integer y) throws OverflowException {
        if (y < 0) {
            if (x < Integer.MIN_VALUE - y) {
                throw new OverflowException("Overflow when adding");
            }
        } else {
            if (x > Integer.MAX_VALUE - y) {
                throw new OverflowException("Overflow when adding");
            }
        }
    }

    public Integer add(final Integer x, final Integer y) throws OverflowException {
        if (flag) {
            checkAdd(x, y);
        }
        return x + y;
    }

    private void checkSubtract(final Integer x, final Integer y) throws OverflowException {
        if (y < 0) {
            if (x > Integer.MAX_VALUE + y) {
                throw new OverflowException("Overflow when subtracting");
            }
        } else {
            if (x < Integer.MIN_VALUE + y) {
                throw new OverflowException("Overflow when subtracting");
            }
        }
    }

    public Integer subtract(final Integer x, final Integer y) throws OverflowException {
        if (flag) {
            checkSubtract(x, y);
        }
        return x - y;
    }

    private void checkNegate(final Integer x) throws OverflowException {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException("Overflow when negating");
        }
    }

    public Integer negate(final Integer x) throws OverflowException {
        if (flag) {
            checkNegate(x);
        }
        return -x;
    }

    private void checkAbs(final Integer x) throws OverflowException {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException("Overflow while calculating absolute value");
        }
    }

    public Integer abs(final Integer x) throws OverflowException {
        if (flag) {
            checkAbs(x);
        }
        return Math.abs(x);
    }

    private void checkSqr(final Integer x) throws OverflowException {
        try {
            checkMultiply(x, x);
        } catch (OverflowException e) {
            throw new OverflowException("Overflow while calculating square");
        }
    }

    public Integer sqr(final Integer x) throws OverflowException {
        if (flag) {
            checkSqr(x);
        }
        return x * x;
    }

    private void checkMod(final Integer x, final Integer y) throws ModulingByZeroException {
        if (y == 0) {
            throw new ModulingByZeroException();
        }
    }

    public Integer mod(final Integer x, final Integer y) throws ModulingByZeroException {
        if (flag) {
            checkMod(x, y);
        }
        return x % y;
    }

    private void checkMultiply(final Integer x, final Integer y) throws OverflowException {
        if (x < 0 && y < 0 && x < Integer.MAX_VALUE / y) {
            throw new OverflowException("Overflow when multiplying");
        }
        if (x < 0 && y > 0 && x < Integer.MIN_VALUE / y) {
            throw new OverflowException("Overflow when multiplying");
        }
        if (x > 0 && y < 0 && y < Integer.MIN_VALUE / x) {
            throw new OverflowException("Overflow when multiplying");
        }
        if (x > 0 && y > 0 && x > Integer.MAX_VALUE / y) {
            throw new OverflowException("Overflow when multiplying");
        }
    }

    public Integer multiply(final Integer x, final Integer y) throws OverflowException {
        if (flag) {
            checkMultiply(x, y);
        }
        return x * y;
    }

    private void checkDivide(final Integer x, final Integer y) throws DivisionByZeroException, OverflowException {
        if (y == 0) {
            throw new DivisionByZeroException();
        }
        if (x == Integer.MIN_VALUE && y == -1) {
            throw new OverflowException("Overflow when dividing");
        }
    }

    public Integer divide(final Integer x, final Integer y) throws DivisionByZeroException, OverflowException {
        if (flag) {
            checkDivide(x, y);
        }
        return x / y;
    }
}
