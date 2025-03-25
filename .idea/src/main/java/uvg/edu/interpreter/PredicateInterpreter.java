package uvg.edu.interpreter;

import java.util.List;

public class PredicateInterpreter {
    public Object interpret(String operator, List<Object> args) {
        switch (operator.toUpperCase()) {
            case "ATOM":
                if (args.size() != 1) throw new RuntimeException("ATOM requires 1 argument");
                return !(args.get(0) instanceof List);
            case "LIST":
                if (args.size() != 1) throw new RuntimeException("LIST requires 1 argument");
                return args.get(0) instanceof List;
            case "EQUAL":
                if (args.size() != 2) throw new RuntimeException("EQUAL requires 2 arguments");
                return args.get(0).equals(args.get(1));
            case "<":
                return compare(args, (a, b) -> a < b);
            case ">":
                return compare(args, (a, b) -> a > b);
            case "<=":
                return compare(args, (a, b) -> a <= b);
            case ">=":
                return compare(args, (a, b) -> a >= b);
            default:
                throw new RuntimeException("Unknown predicate: " + operator);
        }
    }

    private Boolean compare(List<Object> args, java.util.function.BiPredicate<Integer, Integer> pred) {
        if (args.size() != 2) throw new RuntimeException("Comparison requires 2 arguments");
        if (!(args.get(0) instanceof Integer) || !(args.get(1) instanceof Integer)) {
            throw new RuntimeException("Non-numeric arguments in comparison: " + args);
        }
        return pred.test((Integer) args.get(0), (Integer) args.get(1));
    }
}