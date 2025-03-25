package uvg.edu;

import java.util.ArrayList;
import java.util.List;
import uvg.edu.interpreter.*;

public class LispParser {
    private final List<Token> tokens;
    private int position;
    private final ExecutionContext context;
    private final ArithmeticInterpreter arithmetic = new ArithmeticInterpreter();
    private final PredicateInterpreter predicates = new PredicateInterpreter();
    private final FunctionInterpreter functions;
    private final VariableInterpreter variables;
    private final ConditionalInterpreter conditionals;
    private final QuoteInterpreter quote = new QuoteInterpreter();
    private final PrintInterpreter print = new PrintInterpreter();

    public LispParser(List<Token> tokens, ExecutionContext context) {
        this.tokens = tokens;
        this.context = context;
        this.functions = new FunctionInterpreter(context);
        this.variables = new VariableInterpreter(context);
        this.conditionals = new ConditionalInterpreter();
        this.position = 0;
    }

    public Object parse() {
        if (position >= tokens.size()) {
            return null;
        }
        Token token = tokens.get(position++);

        if (token.getType() == TokenType.LPAREN) {
            List<Object> expression = new ArrayList<>();
            while (position < tokens.size() && tokens.get(position).getType() != TokenType.RPAREN) {
                expression.add(parse());
            }
            if (position >= tokens.size()) {
                throw new RuntimeException("Missing closing parenthesis");
            }
            position++; // Consume RPAREN
            return expression;
        } else if (token.getType() == TokenType.QUOTE) {
            List<Object> expression = new ArrayList<>();
            expression.add("QUOTE");
            Object quotedExpr = parse();
            if (quotedExpr == null) {
                throw new RuntimeException("Nothing to quote after '");
            }
            expression.add(quotedExpr);
            return expression;
        } else if (token.getType() == TokenType.NUMBER) {
            return Integer.parseInt(token.getValue());
        } else if (token.getType() == TokenType.STRING) {
            return token.getValue();
        } else if (token.getType() == TokenType.SYMBOL) {
            return token.getValue();
        }
        return null;
    }

    public Object evaluate(List<Object> expr, ExecutionContext currentContext) {
        if (expr.isEmpty()) return null;
        if (expr.size() == 1 && !(expr.get(0) instanceof List)) return expr.get(0);

        Object operator = expr.get(0);
        if (operator instanceof List) {
            operator = evaluate((List<Object>) operator, currentContext);
        }

        List<Object> args = new ArrayList<>();
        for (int i = 1; i < expr.size(); i++) {
            Object arg = expr.get(i);
            if (operator instanceof String && (
                "ATOM".equalsIgnoreCase((String)operator) || 
                "LIST".equalsIgnoreCase((String)operator) || 
                "QUOTE".equalsIgnoreCase((String)operator) || 
                "DEFUN".equalsIgnoreCase((String)operator) || 
                "COND".equalsIgnoreCase((String)operator))) {
                args.add(arg);
            } else if (arg instanceof List) {
                args.add(evaluate((List<Object>) arg, currentContext));
            } else if (arg instanceof String) {
                Object value = currentContext.get((String) arg);
                args.add(value != null ? value : arg);
            } else {
                args.add(arg);
            }
        }

        if (!(operator instanceof String)) {
            return operator;
        }

        String op = ((String) operator).toUpperCase();
        LispFunction func = currentContext.getFunction(op);
        if (func != null) {
            return applyFunction(func, args, currentContext);
        }

        switch (op) {
            case "QUOTE":
                return quote.interpret(args);
            case "SETQ":
                return variables.interpret(args);
            case "DEFUN":
                return functions.interpret(args);
            case "COND":
                return conditionals.interpret(args, this, currentContext);
            case "PRINT":
                return print.interpret(args);
            case "+": case "-": case "*": case "/":
                return arithmetic.interpret(op, args);
            case "ATOM": case "LIST": case "EQUAL": case "<": case ">": case "<=": case ">=":
                return predicates.interpret(op, args);
            default:
                Object value = currentContext.get(op);
                if (value != null) return value;
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    private Object applyFunction(LispFunction func, List<Object> args, ExecutionContext parentContext) {
        if (func.getParameters().size() != args.size()) {
            throw new RuntimeException("Incorrect number of arguments for " + func.getName());
        }

        ExecutionContext funcContext = new ExecutionContext(parentContext);
        for (int i = 0; i < func.getParameters().size(); i++) {
            funcContext.set(func.getParameters().get(i), args.get(i));
        }

        Object result = null;
        for (Object expr : func.getBody()) {
            if (expr instanceof List) {
                result = evaluate((List<Object>) expr, funcContext);
            } else {
                result = expr;
            }
        }
        return result;
    }
}
