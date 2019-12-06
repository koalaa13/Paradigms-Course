"use strict";

const Expression = (function () {
    const supportedVariables = {
        "x": 0,
        "y": 1,
        "z": 2
    };

    const op = {
        "+": Add,
        "-": Sub,
        "*": Mul,
        "/": Div,
        "negate": Negate,
        "sumsq": Sumsq,
        "length": Length,
        "sum": Sum,
        "avg": Avg
    };

    const cntArgs = {
        "+": 2,
        "-": 2,
        "/": 2,
        "*": 2,
        "negate": 1,
        "sumsq": 0,
        "length": 0,
        "sum": 0,
        "avg": 0
    };

    function Const(value) {
        this.getValue = () => value;
        this.evaluate = (...args) => value;
        this.toString = () => value.toString();
        this.diff = variable => ZERO;
        this.prefix = () => value.toString();
        this.postfix = () => value.toString();
    }

    const ZERO = new Const(0);
    const ONE = new Const(1);
    const TWO = new Const(2);

    function Variable(name) {
        this.evaluate = (...args) => args[supportedVariables[name]];
        this.toString = () => name;
        this.diff = variable => name === variable ? ONE : ZERO;
        this.prefix = () => name;
        this.postfix = () => name;
    }

    function AbstractOperations(...operands) {
        this.operands = operands;
    }

    AbstractOperations.prototype.evaluate = function (...args) {
        return this.calc(this.operands.map(elem => elem.evaluate(...args)));
    };
    AbstractOperations.prototype.toString = function () {
        let res = "";
        this.operands.forEach(elem => res += elem.toString() + " ");
        return res + this.operationSymbol;
    };
    AbstractOperations.prototype.postfix = function () {
        let res = "(";
        if (this.operands.length === 0) {
            res += ' ';
        }
        this.operands.forEach(elem => res += elem.postfix() + " ");
        return res + this.operationSymbol + ")";
    };
    AbstractOperations.prototype.prefix = function () {
        let res = "(" + this.operationSymbol;
        this.operands.forEach(elem => res += " " + elem.prefix());
        if (this.operands.length === 0) {
            res += ' ';
        }
        return res + ")";
    };

    function Add(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => operands[0] + operands[1];
        this.operationSymbol = "+";
        this.diff = variable => new Add(op1.diff(variable), op2.diff(variable));
    }

    Add.prototype = Object.create(AbstractOperations.prototype);

    function Negate(op1) {
        AbstractOperations.call(this, op1);
        this.calc = operands => -operands[0];
        this.operationSymbol = "negate";
        this.diff = variable => new Negate(op1.diff(variable));
    }

    Negate.prototype = Object.create(AbstractOperations.prototype);

    function Sub(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => operands[0] - operands[1];
        this.operationSymbol = "-";
        this.diff = variable => new Sub(op1.diff(variable), op2.diff(variable));
    }

    Sub.prototype = Object.create(AbstractOperations.prototype);

    function Mul(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => operands[0] * operands[1];
        this.operationSymbol = "*";
        this.diff = variable => new Add(new Mul(op1, op2.diff(variable)), new Mul(op1.diff(variable), op2));
    }

    Mul.prototype = Object.create(AbstractOperations.prototype);

    function Div(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => operands[0] / operands[1];
        this.operationSymbol = "/";
        this.diff = variable => new Div(new Sub(new Mul(op1.diff(variable), op2), new Mul(op1, op2.diff(variable))), new Mul(op2, op2));
    }

    Div.prototype = Object.create(AbstractOperations.prototype);

    let sqr = (...args) => args.reduce((sum, cur) => (sum + cur * cur), 0);

    function Sumsq(...args) {
        AbstractOperations.call(this, ...args);
        this.calc = operands => sqr(...operands);
        this.operationSymbol = "sumsq";
        this.diff = variable => new Mul(TWO, args.reduce((res, cur) => new Add(res, new Mul(cur, cur.diff(variable))), ZERO));
    }

    Sumsq.prototype = Object.create(AbstractOperations.prototype);

    function Length(...args) {
        AbstractOperations.call(this, ...args);
        this.calc = operands => Math.sqrt(sqr(...operands));
        this.operationSymbol = "length";
        this.diff = variable => {
            if (args.length === 0) {
                return ZERO;
            }
            return new Div(new Sumsq(...args).diff(variable), new Mul(TWO, new Length(...args)));
        }
    }

    Length.prototype = Object.create(AbstractOperations.prototype);

    let sum = (...args) => args.reduce((sum, cur) => sum + cur, 0);

    function Sum(...args) {
        AbstractOperations.call(this, ...args);
        this.calc = operands => sum(...operands);
        this.operationSymbol = "sum";
        this.diff = variable => args.reduce((res, cur) => new Add(res, cur.diff(variable)), ZERO);
    }

    Sum.prototype = Object.create(AbstractOperations.prototype);

    function Avg(...args) {
        AbstractOperations.call(this, ...args);
        this.calc = operands => sum(...operands) / operands.length;
        this.operationSymbol = "avg";
        this.diff = variable => new Div(new Mul(new Sum(...args).diff(variable), new Const(args.length)), new Const(args.length * args.length));
    }

    Avg.prototype = Object.create(AbstractOperations.prototype);

    function isPartOfIdentifier(c) {
        return /[a-z]/.test(c) || /[0-9]/.test(c);
    }

    function isPartOfNumber(c) {
        return /[0-9]/.test(c) || c === '-' || c === '.';
    }

    function getSomething(expression, pos, isPart) {
        let res = "";
        while (pos < expression.length && isPart(expression[pos])) {
            res += expression[pos++];
        }
        return res;
    }

    function getNumber(expression, pos) {
        return getSomething(expression, pos, isPartOfNumber);
    }

    function getIdentifier(expression, pos) {
        return getSomething(expression, pos, isPartOfIdentifier);
    }

    function isWhitespace(c) {
        return /\s/.test(c);
    }

    function skipWhitespaces(expression, pos) {
        return getSomething(expression, pos, isWhitespace).length;
    }

    function getCurToken(expression, pos) {
        let c = expression[pos];
        if (/[+*\/()]/.test(c)) {
            return c;
        }
        if (c === '-') {
            if (pos + 1 < expression.length && isPartOfNumber(expression[pos + 1])) {
                return getNumber(expression, pos);
            } else {
                return c;
            }
        }
        if (isPartOfNumber(c)) {
            return getNumber(expression, pos);
        }
        if (isPartOfIdentifier(c)) {
            return getIdentifier(expression, pos);
        }
        throw new UnknownTokenException(c, pos + 1);
    }

    function parse(expression) {
        let stackOperands = [], stackOperations = [], indsOfBrackets = [], countOperands = [];
        for (let i = 0; i < expression.length; ++i) {
            i += skipWhitespaces(expression, i);
            if (i >= expression.length) {
                break;
            }
            let token = getCurToken(expression, i);
            if (token in supportedVariables) {
                stackOperands.push(new Variable(token));
                countOperands[countOperands.length - 1]++;
            } else {
                if (token in op) {
                    stackOperations.push(new Pair(token, i));
                } else {
                    if (token === '(') {
                        indsOfBrackets.push(i);
                        countOperands.push(0);
                    } else {
                        if (token === ')') {
                            if (indsOfBrackets.length === 0) {
                                throw new UnpairedBracketException(')', i + 1);
                            } else {
                                indsOfBrackets.pop();
                            }
                            if (stackOperations.length === 0) {
                                throw new MissingOperationException(i + 1);
                            }
                            let curOperation = stackOperations.pop(), curCntArgs = cntArgs[curOperation.first()];
                            if (cntArgs[curOperation.first()] === 0) {
                                curCntArgs = countOperands[countOperands.length - 1];
                            }
                            if (stackOperands.length < curCntArgs) {
                                throw new MissingOperandException(curOperation.first(), curOperation.second() + 1, curCntArgs, stackOperands.length);
                            }
                            let args = [];
                            for (let j = 0; j < curCntArgs; ++j) {
                                args.push(stackOperands.pop());
                            }
                            args.reverse();
                            stackOperands.push(new op[curOperation.first()](...args));
                            countOperands.pop();
                            countOperands[countOperands.length - 1]++;
                        } else {
                            let value = parseFloat(token);
                            if (!isNaN(value)) {
                                stackOperands.push(new Const(value));
                                countOperands[countOperands.length - 1]++;
                            } else {
                                throw new UnknownTokenException(token, i + 1)
                            }
                        }
                    }
                }
            }
            i += token.length - 1;
        }
        if (indsOfBrackets.length !== 0) {
            throw new UnpairedBracketException('(', indsOfBrackets[0] + 1);
        }
        if (stackOperations.length !== 0) {
            let curOperation = stackOperations.pop();
            throw new MissingOperandException(curOperation.first(), curOperation.second() + 1, cntArgs[curOperation.first()], stackOperands.length);
        }
        if (stackOperands.length !== 1) {
            throw new MissingOperationException(i + 1);
        }
        return stackOperands.pop();

    }

    return {
        Const: Const,
        Variable: Variable,
        Add: Add,
        Negate: Negate,
        Sub: Sub,
        Div: Div,
        Mul: Mul,
        parse: parse,
        Sumsq: Sumsq,
        Length: Length,
        Sum: Sum,
        Avg: Avg
    }
})();

let Const = Expression.Const;
let Variable = Expression.Variable;
let Add = Expression.Add;
let Negate = Expression.Negate;
let Multiply = Expression.Mul;
let Subtract = Expression.Sub;
let Divide = Expression.Div;
let parsePrefix = Expression.parse;
let parsePostfix = Expression.parse;
let Sumsq = Expression.Sumsq;
let Length = Expression.Length;
let Avg = Expression.Avg;
let Sum = Expression.Sum;

function Pair(first, second) {
    this.first = () => first;
    this.second = () => second;
}

const Exceptions = (function () {
    function Exception(message) {
        this.message = message;
    }

    Exception.prototype = Object.create(Error.prototype);

    function MissingOperandException(operation, pos, countExpected, countHave) {
        let message = "Expected " + countExpected.toString() + " operands for operation " + operation + " at pos " + pos.toString() + " have only " + countHave.toString();
        Exception.call(this, message);
        this.name = "MissingOperandException";
    }

    MissingOperandException.prototype = Object.create(Exception.prototype);

    function UnpairedBracketException(bracket, pos) {
        let message = "There is unpaired bracket \'" + bracket + "\' in expression at pos " + pos.toString();
        Exception.call(this, message);
        this.name = "UnpairedBracketException";
    }

    UnpairedBracketException.prototype = Object.create(Exception.prototype);

    function UnknownTokenException(token, pos) {
        let message = "There is unknown token \'" + token + "\' in expression at pos " + pos.toString();
        Exception.call(this, message);
        this.name = "UnknownTokenException";
    }

    UnknownTokenException.prototype = Object.create(Exception.prototype);

    function MissingOperationException(pos) {
        let message = "Missing operation in expression at pos " + pos.toString();
        Exception.call(this, message);
        this.name = "MissingOperationException";
    }

    MissingOperationException.prototype = Object.create(Exception.prototype);

    return {
        MissingOperandException: MissingOperandException,
        UnpairedBracketException: UnpairedBracketException,
        UnknownTokenException: UnknownTokenException,
        MissingOperationException: MissingOperationException
    }
})();

let MissingOperandException = Exceptions.MissingOperandException;
let UnpairedBracketException = Exceptions.UnpairedBracketException;
let UnknownTokenException = Exceptions.UnknownTokenException;
let MissingOperationException = Exceptions.MissingOperationException;
