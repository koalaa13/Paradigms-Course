package expression.exceptions;

public class ParsingException extends Exception {
    public ParsingException(String reason, String expression, int pos) {
        super(reason);
        System.out.printf("%s at index %d:\n", reason, pos + 1);
        int l = Integer.max(0, pos - 5), r = Integer.min(expression.length(), pos + 5);
        for (int i = l; i < r; ++i) {
            System.out.printf("%c", expression.charAt(i));
        }
        System.out.print("\n");
        for (int i = l; i < r; ++i) {
            if (i == pos) {
                System.out.print("^");
            } else {
                System.out.print("~");
            }
        }
        if (pos >= expression.length()) {
            System.out.print("^");
        }
        System.out.print("\n");
    }

    public ParsingException(String reason) {
        super(reason);
    }
}
