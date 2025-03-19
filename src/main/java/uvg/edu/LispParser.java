package uvg.edu;

import java.util.ArrayList;
import java.util.List;

public class LispParser {
    private final List<Token> tokens;
    private int position;
    private final ExecutionContext context;

    public LispParser(List<Token> tokens, ExecutionContext context) {
        this.tokens = tokens;
        this.position = 0;
        this.context = context;
    }

    public Object parse() {
        if (position >= tokens.size()) return null;
        Token token = tokens.get(position++);
        
        if (token.getType() == TokenType.QUOTE) {
            Object quoted = parse();
            return quoted;
        }
        
        if (token.getType() == TokenType.LPAREN) {
            List<Object> expression = new ArrayList<>();
            while (position < tokens.size() && tokens.get(position).getType() != TokenType.RPAREN) {
                expression.add(parse());
            }
            position++;
            
            if (!expression.isEmpty() && expression.get(0) instanceof String && 
                ((String)expression.get(0)).toUpperCase().equals("DEFUN")) {
                return defun(expression.subList(1, expression.size()));
            }
            
            return evaluate(expression);
        } else if (token.getType() == TokenType.SYMBOL) {
            Object value = context.get(token.getValue());
            return (value != null) ? value : token.getValue();
        } else if (token.getType() == TokenType.NUMBER) {
            return Integer.parseInt(token.getValue());
        } else if (token.getType() == TokenType.STRING) { 
            return token.getValue();
        }
        return null;
    }
    

    private Object evaluate(List<Object> expression) {
        if (expression.isEmpty()) return null;

        Object operator = expression.get(0);

        if (operator instanceof String) {
            String op = (String) operator;
            List<Object> args = expression.subList(1, expression.size());

            switch (op.toUpperCase()) {
                case "+": return add(args);
                case "-": return subtract(args);
                case "*": return multiply(args);
                case "/": return divide(args);
                case "QUOTE": return args.get(0);
                case "SETQ": return setq(args);
                case "PRINT": return print(args);
                case "ATOM": return atom(args);
                case "LIST": return list(args);
                case "EQUAL": return equal(args);
                case "<": return lessThan(args);
                case ">": return greaterThan(args);
                case "COND": return cond(args);
                default:
                    Object varValue = context.get(op);
                    if (varValue != null) {
                        return varValue;
                    }
                    
                    LispFunction func = context.getFunction(op);
                    if (func != null) {
                        return applyFunction(func, args);
                    }
                    
                    throw new RuntimeException("Operador desconocido: " + op);
            }
        }
        return expression;
    }

    private int add(List<Object> operands) {
        return operands.stream().mapToInt(o -> (Integer) o).sum();
    }

    private int subtract(List<Object> operands) {
        int result = (Integer) operands.get(0);
        for (int i = 1; i < operands.size(); i++) result -= (Integer) operands.get(i);
        return result;
    }

    private int multiply(List<Object> operands) {
        return operands.stream().mapToInt(o -> (Integer) o).reduce(1, (a, b) -> a * b);
    }

    private int divide(List<Object> operands) {
        int result = (Integer) operands.get(0);
        for (int i = 1; i < operands.size(); i++) result /= (Integer) operands.get(i);
        return result;
    }

    private Object setq(List<Object> args) {
        String varName = (String) args.get(0);
        Object value = args.get(1);
        context.set(varName, value);
        return value;
    }

    private Object defun(List<Object> args) {
        if (args.size() < 3) {
            throw new RuntimeException("DEFUN requiere al menos nombre, parámetros y cuerpo");
        }
        
        String funcName = (String) args.get(0);
        List<String> params;
        
        if (args.get(1) instanceof List) {
            params = new ArrayList<>();
            for (Object param : (List<Object>) args.get(1)) {
                if (param instanceof String) {
                    params.add((String) param);
                } else {
                    throw new RuntimeException("Los parámetros deben ser símbolos");
                }
            }
        } else {
            throw new RuntimeException("La lista de parámetros debe ser una lista");
        }
        List<Object> body = new ArrayList<>(args.subList(2, args.size()));
        LispFunction function = new LispFunction(params, body);
        context.setFunction(funcName, function);
        return funcName;
    }

    private Object applyFunction(LispFunction func, List<Object> args) {
        if (func.getParameters().size() != args.size()) {
            throw new RuntimeException("Número incorrecto de argumentos");
        }
    
        ExecutionContext functionContext = new ExecutionContext(context);
        
        for (int i = 0; i < func.getParameters().size(); i++) {
            Object arg = args.get(i);
            if (arg instanceof List) {
                LispParser argParser = new LispParser(tokensFromList((List<Object>) arg), context);
                functionContext.set(func.getParameters().get(i), argParser.parse());
            } else {
                functionContext.set(func.getParameters().get(i), arg);
            }
        }
    
        Object result = null;
        for (Object expr : func.getBody()) {
            LispParser bodyParser = new LispParser(
                tokensFromList(expr instanceof List ? (List<Object>) expr : List.of(expr)),functionContext);
        result = bodyParser.parse();
    }
    return result;
}

    private Object print(List<Object> args) {
        Object value = args.get(0);
        System.out.println(value);
        return value;
    }

    private boolean atom(List<Object> args) {
        Object arg = args.get(0);
        return !(arg instanceof List);
    }

    private boolean list(List<Object> args) {
        Object arg = args.get(0);
        return arg instanceof List;
    }

    private boolean equal(List<Object> args) {
        return args.get(0).equals(args.get(1));
    }

    private boolean lessThan(List<Object> args) {
        return (Integer) args.get(0) < (Integer) args.get(1);
    }

    private boolean greaterThan(List<Object> args) {
        return (Integer) args.get(0) > (Integer) args.get(1);
    }

    private Object cond(List<Object> args) {
        for (Object clause : args) {
            if (!(clause instanceof List)) {
                throw new RuntimeException("Debe ser lista...");
            }
    
            List<Object> condPair = (List<Object>) clause;
            if (condPair.size() < 2) {
                throw new RuntimeException("se necesita una condición y el resultado");
            }
    
            Object condition = condPair.get(0);
    

            if (condition instanceof String && ((String) condition).equalsIgnoreCase("T")) {
                Object action = condPair.get(1);
                return action;
            }
    
            Object evalResult;
            if (condition instanceof Boolean) {
                evalResult = condition;
            } else if (condition instanceof List) {
                LispParser conditionParser = new LispParser(tokensFromList((List<Object>) condition), context);
                evalResult = conditionParser.parse();
            } else {
                Object value = context.get(condition.toString());
                evalResult = (value != null) ? value : false;
            }
    
            if (evalResult instanceof Boolean && (Boolean) evalResult) {
                Object action = condPair.get(1);
                if (action instanceof String) {
                    return action;
                }

                if (action instanceof List) {
                    LispParser actionParser = new LispParser(tokensFromList((List<Object>) action), context);
                    return actionParser.parse();
                }
                return action;
            }
        }
        return null;
    }

    private List<Token> tokensFromList(List<Object> list) {
        List<Token> tokens = new ArrayList<>();
        for (Object obj : list) {
            if (obj instanceof Integer) {
                tokens.add(new Token(TokenType.NUMBER, obj.toString()));
            } else if (obj instanceof String) {
                tokens.add(new Token(TokenType.STRING, (String) obj)); 
            } else if (obj instanceof List) {
                tokens.add(new Token(TokenType.LPAREN, "("));
                tokens.addAll(tokensFromList((List<Object>) obj));
                tokens.add(new Token(TokenType.RPAREN, ")"));
            }
        }
        return tokens;
    }
}