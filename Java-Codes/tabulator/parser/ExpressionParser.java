package expression.parser;

import expression.*;
import expression.exceptions.ParsingException;
import expression.exceptions.UnpairedBracketsException;
import expression.operations.Operations;

public class ExpressionParser<T> implements Parser<T> {
    private Tokenizer<T> myTokenizer;
    private Operations<T> operations;

    public ExpressionParser(Operations<T> op) {
        operations = op;
    }

    private TripleExpression<T> unaryOperations() throws ParsingException {
        TripleExpression<T> res;
        switch (myTokenizer.getNextToken()) {
            case NUMBER:
                res = new Const<>(myTokenizer.getValue());
                myTokenizer.getNextToken();
                break;
            case VARIABLE:
                res = new Variable<>(myTokenizer.getVarName());
                myTokenizer.getNextToken();
                break;
            case SUB:
                res = new Negate<>(unaryOperations(), operations);
                break;
            case SQR:
                res = new Sqr<>(unaryOperations(), operations);
                break;
            case ABS:
                res = new Abs<>(unaryOperations(), operations);
                break;
            case OPEN_BRACKET:
                res = addAndSub();
                if (myTokenizer.getCurToken() != Token.CLOSE_BRACKET) {
                    throw new UnpairedBracketsException("There is unpaired open bracket in a expression", myTokenizer.getExpression(), myTokenizer.getInd());
                }
                myTokenizer.getNextToken();
                break;
            default:
                throw new ParsingException("Incorrect expression", myTokenizer.getExpression(), myTokenizer.getInd());
        }
        return res;
    }

    private TripleExpression<T> mulAndDiv() throws ParsingException {
        TripleExpression<T> res = unaryOperations();
        for (; ; ) {
            switch (myTokenizer.getCurToken()) {
                case MUL:
                    res = new Multiply<>(res, unaryOperations(), operations);
                    break;
                case DIV:
                    res = new Divide<>(res, unaryOperations(), operations);
                    break;
                case MOD:
                    res = new Mod<>(res, unaryOperations(), operations);
                    break;
                default:
                    return res;
            }
        }
    }

    private TripleExpression<T> addAndSub() throws ParsingException {
        TripleExpression<T> res = mulAndDiv();
        for (; ; ) {
            switch (myTokenizer.getCurToken()) {
                case ADD:
                    res = new Add<>(res, mulAndDiv(), operations);
                    break;
                case SUB:
                    res = new Subtract<>(res, mulAndDiv(), operations);
                    break;
                default:
                    return res;
            }
        }
    }

    public TripleExpression<T> parse(final String expression) throws ParsingException {
        myTokenizer = new Tokenizer<>(expression, operations);
        return addAndSub();
    }
}
