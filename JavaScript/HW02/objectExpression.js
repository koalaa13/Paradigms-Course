"use strict";

let Expression = (function () {
    const supportedVariables = {
        "x": 0,
        "y": 1,
        "z": 2
    };

    function Const(value) {
        this.getValue = () => value;
        this.evaluate = (...args) => value;
        this.toString = () => value.toString();
        this.simplify = () => new Const(value);
        this.diff = variable => ZERO;
    }

    const ZERO = new Const(0);
    const ONE = new Const(1);
    const TWO = new Const(2);

    function isValue(exp, value) {
        return (exp instanceof Const) && exp.getValue() === value;
    }

    function Variable(name) {
        this.evaluate = (...args) => args[supportedVariables[name]];
        this.toString = () => name;
        this.simplify = () => new Variable(name);
        this.diff = variable => name === variable ? ONE : ZERO;
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

    function Add(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => operands[0] + operands[1];
        this.operationSymbol = "+";
        this.diff = variable => new Add(op1.diff(variable), op2.diff(variable));
        this.simplify = () => {
            op1 = op1.simplify();
            op2 = op2.simplify();
            if (op1 instanceof Const && op2 instanceof Const) {
                return new Const(op1.getValue() + op2.getValue());
            }
            if (isValue(op1, 0)) {
                return op2;
            }
            if (isValue(op2, 0)) {
                return op1;
            }
            return new Add(op1, op2);
        };
    }

    Add.prototype = Object.create(AbstractOperations.prototype);

    function Negate(op1) {
        AbstractOperations.call(this, op1);
        this.calc = operands => -operands[0];
        this.operationSymbol = "negate";
        this.diff = variable => new Negate(op1.diff(variable));
        this.simplify = () => {
            if (op1.operationSymbol === "negate") {
                return op1.operands[0].simplify();
            }
            if (op1 instanceof Const) {
                return new Const(-op1.getValue());
            }
            return new Negate(op1.simplify());
        }
    }

    Negate.prototype = Object.create(AbstractOperations.prototype);

    function Sub(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => operands[0] - operands[1];
        this.operationSymbol = "-";
        this.diff = variable => new Sub(op1.diff(variable), op2.diff(variable));
        this.simplify = () => {
            op1 = op1.simplify();
            op2 = op2.simplify();
            if (op1 instanceof Const && op2 instanceof Const) {
                return new Const(op1.getValue() - op2.getValue());
            }
            if (isValue(op1, 0)) {
                return new Negate(op2);
            }
            if (isValue(op2, 0)) {
                return op1;
            }
            return new Sub(op1, op2);
        }
    }

    Sub.prototype = Object.create(AbstractOperations.prototype);

    function Mul(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => operands[0] * operands[1];
        this.operationSymbol = "*";
        this.diff = variable => new Add(new Mul(op1, op2.diff(variable)), new Mul(op1.diff(variable), op2));
        this.simplify = () => {
            op1 = op1.simplify();
            op2 = op2.simplify();
            if (op1 instanceof Const && op2 instanceof Const) {
                return new Const(op1.getValue() * op2.getValue());
            }
            if (isValue(op1, 1)) {
                return op2;
            }
            if (isValue(op2, 1)) {
                return op1;
            }
            if (isValue(op1, 0) || isValue(op2, 0)) {
                return ZERO;
            }
            return new Mul(op1, op2);
        }
    }

    Mul.prototype = Object.create(AbstractOperations.prototype);

    function Div(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => operands[0] / operands[1];
        this.operationSymbol = "/";
        this.diff = variable => new Div(new Sub(new Mul(op1.diff(variable), op2), new Mul(op1, op2.diff(variable))), new Mul(op2, op2));
        this.simplify = () => {
            op1 = op1.simplify();
            op2 = op2.simplify();
            if (op1 instanceof Const && op2 instanceof Const) {
                return new Const(op1.getValue() / op2.getValue());
            }
            if (isValue(op2, 1) || isValue(op1, 0)) {
                return op1;
            }
            return new Div(op1, op2);
        }
    }

    Div.prototype = Object.create(AbstractOperations.prototype);

    function ArcTan(op1) {
        AbstractOperations.call(this, op1);
        this.calc = operands => Math.atan(operands[0]);
        this.operationSymbol = "atan";
        this.diff = (variable) => new Div(op1.diff(variable), new Add(new Mul(op1, op1), ONE));
        this.simplify = () => {
            op1 = op1.simplify();
            if (op1 instanceof Const) {
                return new Const(Math.atan(op1.getValue()));
            }
            return new ArcTan(op1);
        }
    }

    ArcTan.prototype = Object.create(AbstractOperations.prototype);

    function ArcTan2(op1, op2) {
        AbstractOperations.call(this, op1, op2);
        this.calc = operands => Math.atan2(operands[0], operands[1]);
        this.operationSymbol = "atan2";
        this.diff = variable => new Div(new Sub(new Mul(op1.diff(variable), op2), new Mul(op1, op2.diff(variable))), new Add(new Mul(op1, op1), new Mul(op2, op2)));
        this.simplify = () => {
            op1 = op1.simplify();
            op2 = op2.simplify();
            if (op1 instanceof Const && op2 instanceof Const) {
                return new Const(Math.atan2(op1.getValue(), op2.getValue()));
            }
            return new ArcTan2(op1, op2);
        }
    }

    ArcTan2.prototype = Object.create(AbstractOperations.prototype);

    function Sinh(op1) {
        AbstractOperations.call(this, op1);
        this.calc = operands => Math.sinh(operands[0]);
        this.operationSymbol = "sinh";
        this.diff = variable => new Mul(new Cosh(op1), op1.diff(variable));
        this.simplify = () => {
            op1.simplify();
            if (op1 instanceof Const) {
                return new Const(Math.sinh(op1.getValue()));
            }
            return new Sinh(op1);
        }
    }

    Sinh.prototype = Object.create(AbstractOperations.prototype);

    function Cosh(op1) {
        AbstractOperations.call(this, op1);
        this.calc = operands => Math.cosh(operands[0]);
        this.operationSymbol = "cosh";
        this.diff = variable => new Mul(new Sinh(op1), op1.diff(variable));
        this.simplify = () => {
            op1.simplify();
            if (op1 instanceof Const) {
                return new Const(Math.cosh(op1.getValue()));
            }
            return new Cosh(op1);
        }
    }

    Cosh.prototype = Object.create(AbstractOperations.prototype);

    function parse(expression) {
        let tokens = expression.split(" ").filter(word => word.length > 0);
        let stack = [];
        let op = {
            "+": Add,
            "-": Sub,
            "*": Mul,
            "/": Div,
            "negate": Negate,
            "atan": ArcTan,
            "atan2": ArcTan2,
            "cosh": Cosh,
            "sinh": Sinh
        };
        let cntArgs = {
            "+": 2,
            "-": 2,
            "/": 2,
            "*": 2,
            "negate": 1,
            "atan": 1,
            "atan2": 2,
            "cosh": 1,
            "sinh": 1
        };
        tokens.forEach(token => {
            if (token in supportedVariables) {
                stack.push(new Variable(token));
                return;
            }
            if (token in op) {
                let args = [];
                for (let j = 0; j < cntArgs[token]; ++j) {
                    args.push(stack.pop());
                }
                args.reverse();
                stack.push(new op[token](...args));
                return;
            }
            stack.push(new Const(parseInt(token)));
        });
        return stack.pop();
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
        ArcTan2: ArcTan2,
        ArcTan: ArcTan,
        Sinh: Sinh,
        Cosh: Cosh
    }
})();

let Const = Expression.Const;
let Variable = Expression.Variable;
let Add = Expression.Add;
let Negate = Expression.Negate;
let Multiply = Expression.Mul;
let Subtract = Expression.Sub;
let Divide = Expression.Div;
let parse = Expression.parse;
let ArcTan = Expression.ArcTan;
let ArcTan2 = Expression.ArcTan2;
let Cosh = Expression.Cosh;
let Sinh = Expression.Sinh;
