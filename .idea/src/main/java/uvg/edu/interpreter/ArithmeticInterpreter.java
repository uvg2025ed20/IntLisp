package uvg.edu.interpreter;

import java.util.List;

public class ArithmeticInterpreter {
    public Object interpret(String operator, List<Object> args) {
        switch (operator.toUpperCase()) {
            case "+":
                return add(args);
            case "-":
                return subtract(args);
            case "*":
                return multiply(args);
            case "/":
                return divide(args);
            default:
                throw new RuntimeException("Unknown arithmetic operator: " + operator);
        }
    }

    private Integer add(List<Object> args) {
        int sum = 0;
        for (Object arg : args) {
            if (!(arg instanceof Integer)) {
                throw new RuntimeException("Non-numeric argument in +: " + arg);
            }
            sum += (Integer) arg;
        }
        return sum;
    }

    private Integer subtract(List<Object> args) {
        if (args.isEmpty()) throw new RuntimeException("- requires arguments");
        if (!(args.get(0) instanceof Integer)) {
            throw new RuntimeException("Non-numeric argument in -: " + args.get(0));
        }
        int result = (Integer) args.get(0);
        for (int i = 1; i < args.size(); i++) {
            if (!(args.get(i) instanceof Integer)) {
                throw new RuntimeException("Non-numeric argument in -: " + args.get(i));
            }
            result -= (Integer) args.get(i);
        }
        return result;
    }

    private Integer multiply(List<Object> args) {
        int result = 1;
        for (Object arg : args) {
            if (!(arg instanceof Integer)) {
                throw new RuntimeException("Non-numeric argument in *: " + arg);
            }
            result *= (Integer) arg;
        }
        return result;
    }

    private Integer divide(List<Object> args) {
        if (args.isEmpty()) throw new RuntimeException("/ requires arguments");
        if (!(args.get(0) instanceof Integer)) {
            throw new RuntimeException("Non-numeric argument in /: " + args.get(0));
        }
        int result = (Integer) args.get(0);
        for (int i = 1; i < args.size(); i++) {
            if (!(args.get(i) instanceof Integer)) {
                throw new RuntimeException("Non-numeric argument in /: " + args.get(i));
            }
            int divisor = (Integer) args.get(i);
            if (divisor == 0) throw new RuntimeException("Division by zero");
            result /= divisor;
        }
        return result;
    }
}