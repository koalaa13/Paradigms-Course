package expression.parser;

import expression.exceptions.*;
import expression.operations.Operations;

import java.util.HashMap;
import java.util.HashSet;

public class Tokenizer<T> {
    private String expression;
    private int ind, balance;
    private T value;
    private Token curToken;
    private char varName;
    private Operations<T> operations;
    private static HashSet<Token> unaryOperations = new HashSet<>();
    private static HashSet<Token> binaryOperations = new HashSet<>();
    private static HashMap<String, Token> identifiers = new HashMap<>();

    Tokenizer(final String newExpression, final Operations<T> op) {
        expression = newExpression;
        ind = balance = 0;
        curToken = Token.BEGIN;
        operations = op;
    }

    static {
        identifiers.put("x", Token.VARIABLE);
        identifiers.put("y", Token.VARIABLE);
        identifiers.put("z", Token.VARIABLE);
        identifiers.put("abs", Token.ABS);
        identifiers.put("mod", Token.MOD);
        identifiers.put("square", Token.SQR);

        unaryOperations.add(Token.ABS);
        unaryOperations.add(Token.SQR);

        binaryOperations.add(Token.MOD);
        binaryOperations.add(Token.ADD);
        binaryOperations.add(Token.SUB);
        binaryOperations.add(Token.MUL);
        binaryOperations.add(Token.DIV);
    }

    public String getExpression() {
        return expression;
    }

    int getInd() {
        return ind;
    }

    T getValue() {
        return value;
    }

    Token getCurToken() {
        return curToken;
    }

    Token getNextToken() throws ParsingException {
        nextToken();
        return curToken;
    }

    char getVarName() {
        return varName;
    }

    private void skipWhiteSpaces() {
        while (ind < expression.length() && Character.isWhitespace(expression.charAt(ind))) {
            ++ind;
        }
    }

    private boolean isPartOfNumber(final char c) {
        return Character.isDigit(c) || c == '.' || c == 'e';
    }

    private String getNumber() {
        int l = ind;
        while (ind < expression.length() && isPartOfNumber(expression.charAt(ind))) {
            ++ind;
        }
        return expression.substring(l, ind--);
    }

    private boolean isPartOfIdentifier(final char c) {
        return Character.isLetterOrDigit(c);
    }

    private String getIdentifier() {
        int l = ind;
        while (ind < expression.length() && isPartOfIdentifier(expression.charAt(ind))) {
            ++ind;
        }
        return expression.substring(l, ind--);
    }

    private void checkForOperand() throws MissingOperandException {
        if (curToken == Token.BEGIN || curToken == Token.OPEN_BRACKET || binaryOperations.contains(curToken) || unaryOperations.contains(curToken)) {
            throw new MissingOperandException(expression, ind);
        }
    }

    private void checkForOperation() throws MissingOperationException {
        if (curToken == Token.CLOSE_BRACKET || curToken == Token.VARIABLE || curToken == Token.NUMBER) {
            throw new MissingOperationException(expression, ind);
        }
    }

    private void nextToken() throws ParsingException {
        skipWhiteSpaces();
        if (ind >= expression.length()) {
            checkForOperand();
            curToken = Token.END;
            return;
        }
        char c = expression.charAt(ind);
        switch (c) {
            case '+':
                checkForOperand();
                curToken = Token.ADD;
                break;
            case '*':
                checkForOperand();
                curToken = Token.MUL;
                break;
            case '/':
                checkForOperand();
                curToken = Token.DIV;
                break;
            case '-':
                if (curToken == Token.NUMBER || curToken == Token.VARIABLE || curToken == Token.CLOSE_BRACKET) {
                    curToken = Token.SUB;
                } else {
                    if (ind + 1 >= expression.length()) {
                        throw new MissingOperandException(expression, ind);
                    } else {
                        if (isPartOfNumber(expression.charAt(ind + 1))) {
                            ind++;
                            value = operations.parseNumber("-" + getNumber());
                            curToken = Token.NUMBER;
                        } else {
                            curToken = Token.SUB;
                        }
                    }
                }
                break;
            case '(':
                if (curToken == Token.CLOSE_BRACKET || curToken == Token.NUMBER || curToken == Token.VARIABLE) {
                    throw new MissingOperationException(expression, ind);
                }
                balance++;
                curToken = Token.OPEN_BRACKET;
                break;
            case ')':
                if (binaryOperations.contains(curToken) || unaryOperations.contains(curToken) || curToken == Token.OPEN_BRACKET) {
                    throw new MissingOperandException(expression, ind);
                }
                if (balance == 0) {
                    throw new UnpairedBracketsException("There is unpaired close bracket in a expression", expression, ind);
                }
                balance--;
                curToken = Token.CLOSE_BRACKET;
                break;
            default:
                if (Character.isDigit(c)) {
                    checkForOperation();
                    value = operations.parseNumber(getNumber());
                    curToken = Token.NUMBER;
                } else {
                    String cur = getIdentifier();
                    if (!identifiers.containsKey(cur)) {
                        throw new UnknownOperationException("Unknown function in expression \"" + cur + "\"", expression, ind - cur.length() + 1);
                    }
                    if (binaryOperations.contains(identifiers.get(cur))) {
                        checkForOperand();
                    } else {
                        checkForOperation();
                    }
                    curToken = identifiers.get(cur);
                    if (curToken == Token.VARIABLE) {
                        varName = cur.charAt(0);
                    }
                }
        }
        ++ind;
    }

}
