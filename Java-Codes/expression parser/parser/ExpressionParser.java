package expression.parser;

import expression.*;
import expression.exceptions.ParsingException;
import expression.exceptions.UnpairedBracketsException;

public class ExpressionParser implements Parser {
    private Tokenizer myTokenizer;

    private TripleExpression unaryOperations() throws ParsingException {
        TripleExpression res;
        switch (myTokenizer.getNextToken()) {
            case NUMBER:
                res = new Const(myTokenizer.getValue());
                myTokenizer.getNextToken();
                break;
            case VARIABLE:
                res = new Variable(myTokenizer.getVarName());
                myTokenizer.getNextToken();
                break;
            case SUB:
                res = new CheckedNegate(unaryOperations());
                break;
            case ABS:
                res = new Abs(unaryOperations());
                break;
            case SQRT:
                res = new Sqrt(unaryOperations());
                break;
            case OPEN_BRACKET:
                res = minAndMax();
                if (myTokenizer.getCurToken() != Token.CLOSE_BRACKET) {
                    throw new UnpairedBracketsException("There is unpaired open bracket in a expression", myTokenizer.getExpression(), myTokenizer.getInd());
                }
                myTokenizer.getNextToken();
                break;
            case HIGH:
                res = new High(unaryOperations());
                break;
            case LOW:
                res = new Low(unaryOperations());
                break;
            default:
                throw new ParsingException("Incorrect expression", myTokenizer.getExpression(), myTokenizer.getInd());
        }
        return res;
    }

    private TripleExpression mulAndDiv() throws ParsingException {
        TripleExpression res = unaryOperations();
        for (; ; ) {
            switch (myTokenizer.getCurToken()) {
                case MUL:
                    res = new CheckedMultiply(res, unaryOperations());
                    break;
                case DIV:
                    res = new CheckedDivide(res, unaryOperations());
                    break;
                default:
                    return res;
            }
        }
    }

    private TripleExpression addAndSub() throws ParsingException {
        TripleExpression res = mulAndDiv();
        for (; ; ) {
            switch (myTokenizer.getCurToken()) {
                case ADD:
                    res = new CheckedAdd(res, mulAndDiv());
                    break;
                case SUB:
                    res = new CheckedSubtract(res, mulAndDiv());
                    break;
                default:
                    return res;
            }
        }
    }

    private TripleExpression minAndMax() throws ParsingException {
        TripleExpression res = addAndSub();
        for (; ; ) {
            switch (myTokenizer.getCurToken()) {
                case MAX:
                    res = new Max(res, addAndSub());
                    break;
                case MIN:
                    res = new Min(res, addAndSub());
                    break;
                default:
                    return res;
            }
        }
    }

    public TripleExpression parse(String expression) throws ParsingException {
        myTokenizer = new Tokenizer(expression);
        return minAndMax();
    }
}
