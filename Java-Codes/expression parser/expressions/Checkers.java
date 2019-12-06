package expression;

import expression.exceptions.*;

class Checkers {

    static void checkMultiply(int x, int y) throws OverflowException {
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

    static void checkAdd(int x, int y) throws OverflowException {
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

    static void checkDivide(int x, int y) throws OverflowException, DivisionByZeroException {
        if (y == 0) {
            throw new DivisionByZeroException();
        }
        if (x == Integer.MIN_VALUE && y == -1) {
            throw new OverflowException("Overflow when dividing");
        }
    }

    static void checkNegate(int x) throws OverflowException {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException("Overflow when negating");
        }
    }

    static void checkSubtract(int x, int y) throws OverflowException {
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

    static void checkAbs(int x) throws OverflowException {
        if (x == Integer.MIN_VALUE) {
            throw new OverflowException("Absolute value overflow");
        }
    }

    static void checkSqrt(int x) throws NegativeSqrtException {
        if (x < 0) {
            throw new NegativeSqrtException("Sqrt from negative number");
        }
    }
}
