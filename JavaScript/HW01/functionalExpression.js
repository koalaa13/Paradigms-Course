"use strict";

const supportedCnsts = {
    "pi": Math.PI,
    "e": Math.E,
    "one": 1,
    "two": 2
};

const supportedVariables = {
    "x": 0,
    "y": 1,
    "z": 2
};

const cnst = value => () => value;
const pi = cnst(Math.PI);
const e = cnst(Math.E);
const one = cnst(1);
const two = cnst(2);

const variable = name => function () {
    return arguments[supportedVariables[name]];
};

const operations = action => function () {
    const operands = [].slice.call(arguments);
    return (...args) => action(...operands.map(elem => elem(...args)));
};

const cmp = (a, b) => {
    if (a < b) {
        return -1;
    }
    if (a === b) {
        return 0;
    }
    return 1;
};

const sum = (...args) => args.reduce((elem, zero) => elem + zero);
const avg = (...args) => sum(...args) / args.length;
const med = (...args) => {
    args.sort(cmp);
    return args[Math.floor(args.length / 2)];
};

const add = operations((a, b) => a + b);
const subtract = operations((a, b) => a - b);
const divide = operations((a, b) => a / b);
const multiply = operations((a, b) => a * b);
const negate = operations(a => -a);
const avg5 = operations((...args) => avg(...args));
const med3 = operations((...args) => med(...args));
const abs = operations(a => Math.abs(a));
const iff = operations((a, b, c) => a < 0 ? c : b);

const parse = function (expression) {
    const tokens = expression.split(" ").filter(word => word.length > 0);
    const op = {
        "+": add,
        "-": subtract,
        "*": multiply,
        "/": divide,
        "negate": negate,
        "avg5": avg5,
        "med3": med3,
        "abs": abs,
        "iff": iff
    };
    const cntArgs = {
        "+": 2,
        "-": 2,
        "*": 2,
        "/": 2,
        "negate": 1,
        "avg5": 5,
        "med3": 3,
        "abs": 1,
        "iff": 3
    };
    let stack = [];
    tokens.forEach(token => {
        if (token in supportedCnsts) {
            stack.push(cnst(supportedCnsts[token]));
            return;
        }
        if (token in supportedVariables) {
            stack.push(variable(token));
            return;
        }
        if (token in op) {
            let args = [];
            for (let j = 0; j < cntArgs[token]; ++j) {
                args.push(stack.pop());
            }
            args.reverse();
            stack.push(op[token](...args));
            return;
        }
        stack.push(cnst(parseInt(token)));
    });
    return stack.pop();
};