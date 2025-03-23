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
        return parse(false); // Por defecto, no estamos dentro de DEFUN
    }

    private Object parse(boolean insideDefun) {
        if (position >= tokens.size()) {
            System.out.println("Reached end of tokens at position: " + position);
            return null;
        }
        Token token = tokens.get(position++);
        System.out.println("Parsing token at position " + (position - 1) + ": " + token);
        
        if (token.getType() == TokenType.QUOTE) {
            Object quoted = parse(insideDefun);
            System.out.println("Parsed QUOTE: " + quoted);
            return quoted;
        }
        
        if (token.getType() == TokenType.LPAREN) {
            List<Object> expression = new ArrayList<>();
            boolean isDefun = false;
            
            // Parsear el primer elemento para determinar si es DEFUN
            if (position < tokens.size()) {
                Token nextToken = tokens.get(position);
                if (nextToken.getType() == TokenType.SYMBOL && nextToken.getValue().toUpperCase().equals("DEFUN")) {
                    position++;
                    expression.add(nextToken.getValue());
                    isDefun = true;
                    System.out.println("Detected DEFUN at position " + (position - 1));
                }
            }

            // Si no es DEFUN, parsear el primer elemento normalmente
            if (!isDefun && position < tokens.size() && tokens.get(position).getType() != TokenType.RPAREN) {
                Object firstElement = parse(insideDefun);
                expression.add(firstElement);
                System.out.println("Parsed first element: " + firstElement);
            }

            // Parsear el resto de la lista
            while (position < tokens.size() && tokens.get(position).getType() != TokenType.RPAREN) {
                Object subExpr = parse(isDefun || insideDefun); // Pasar el estado de DEFUN a las subexpresiones
                expression.add(subExpr);
                System.out.println("Parsed subexpression: " + subExpr);
            }
            
            if (position >= tokens.size()) {
                throw new RuntimeException("Missing closing parenthesis");
            }
            position++; // Consumir el RPAREN
            System.out.println("Finished parsing list: " + expression);
            
            if (isDefun) {
                System.out.println("Calling defun with args: " + expression.subList(1, expression.size()));
                return defun(expression.subList(1, expression.size()));
            }
            
            if (insideDefun) {
                // Si estamos dentro de DEFUN, no evaluar la expresión, solo devolverla
                return expression;
            }
            
            System.out.println("Evaluating expression: " + expression);
            return evaluate(expression);
        } else if (token.getType() == TokenType.SYMBOL) {
            String symbol = token.getValue();
            if (insideDefun) {
                // Si estamos dentro de DEFUN, no resolver el símbolo, solo devolverlo
                System.out.println("Inside DEFUN, not resolving symbol: " + symbol);
                return symbol;
            }
            Object value = context.get(symbol);
            System.out.println("Resolving symbol: " + symbol + ", value: " + value);
            if (value != null) {
                return value; // Devolver el valor si es una variable en el contexto
            }
            // Si no es una variable, podría ser un operador o función
            return symbol; // Esto permite que operadores como "+" lleguen a evaluate
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
        System.out.println("Evaluando expresión con operador: " + operator);
    
        if (operator instanceof String) {
            String op = (String) operator;
            List<Object> args = expression.subList(1, expression.size());
    
            // Evaluar argumentos completamente
            List<Object> evaluatedArgs = new ArrayList<>();
            for (Object arg : args) {
                if (arg instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> argList = (List<Object>) arg;
                    LispParser argParser = new LispParser(tokensFromList(argList), context);
                    Object parsedArg = argParser.parse();
                    if (parsedArg instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Object> parsedList = (List<Object>) parsedArg;
                        evaluatedArgs.add(evaluate(parsedList));
                    } else {
                        evaluatedArgs.add(parsedArg);
                    }
                } else if (arg instanceof String) {
                    String argStr = (String) arg;
                    Object value = context.get(argStr);
                    System.out.println("Resolviendo argumento: " + argStr + ", valor: " + value);
                    evaluatedArgs.add(value != null ? value : argStr);
                } else {
                    evaluatedArgs.add(arg);
                }
            }
    
            System.out.println("Argumentos evaluados para " + op + ": " + evaluatedArgs);
    
            switch (op.toUpperCase()) {
                case "+": return add(evaluatedArgs);
                case "-": return subtract(evaluatedArgs);
                case "*": return multiply(evaluatedArgs);
                case "/": return divide(evaluatedArgs);
                case "QUOTE": return evaluatedArgs.get(0);
                case "SETQ": return setq(evaluatedArgs);
                case "PRINT": return print(evaluatedArgs);
                case "ATOM": return atom(evaluatedArgs);
                case "LIST": return list(evaluatedArgs);
                case "EQUAL": return equal(evaluatedArgs);
                case "<": return lessThan(evaluatedArgs);
                case ">": return greaterThan(evaluatedArgs);
                case "COND": return cond(args); // Usar args sin evaluar para COND
                default:
                    LispFunction func = context.getFunction(op);
                    if (func != null) {
                        return applyFunction(func, evaluatedArgs);
                    }
                    throw new RuntimeException("Operador desconocido: " + op);
            }
        }
        return expression;
    }
    
    private int add(List<Object> operands) {
        System.out.println("Adding: " + operands);
        return operands.stream().mapToInt(o -> (Integer) o).sum();
    }

    private int subtract(List<Object> operands) {
        System.out.println("Subtracting: " + operands);
        int result = (Integer) operands.get(0);
        for (int i = 1; i < operands.size(); i++) result -= (Integer) operands.get(i);
        return result;
    }

    private int multiply(List<Object> operands) {
        System.out.println("Multiplying: " + operands);
        return operands.stream().mapToInt(o -> (Integer) o).reduce(1, (a, b) -> a * b);
    }

    private int divide(List<Object> operands) {
        System.out.println("Dividing: " + operands);
        int result = (Integer) operands.get(0); 
        for (int i = 1; i < operands.size(); i++) {
            int divisor = (Integer) operands.get(i); 
            if (divisor == 0) {
                throw new RuntimeException("División por cero no permitida");
            }
            result /= divisor; 
        }
        return result;
    }

    private Object setq(List<Object> args) {
        if (!(args.get(0) instanceof String)) {
            throw new RuntimeException("SETQ requiere un símbolo como primer argumento");
        }
        String varName = (String) args.get(0);
        Object value = args.get(1);
        context.set(varName, value);
        return value;
    }

    private Object defun(List<Object> args) {
        if (args.size() < 3) {
            throw new RuntimeException("DEFUN requiere al menos nombre, parámetros y cuerpo");
        }
        
        if (!(args.get(0) instanceof String)) {
            throw new RuntimeException("El nombre de la función debe ser un símbolo");
        }
        String funcName = (String) args.get(0);
        List<String> params;
        
        if (!(args.get(1) instanceof List)) {
            throw new RuntimeException("La lista de parámetros debe ser una lista");
        }
        @SuppressWarnings("unchecked")
        List<Object> paramList = (List<Object>) args.get(1);
        params = new ArrayList<>();
        for (Object param : paramList) {
            if (!(param instanceof String)) {
                throw new RuntimeException("Los parámetros deben ser símbolos");
            }
            params.add((String) param);
        }
        
        List<Object> body = new ArrayList<>(args.subList(2, args.size()));
        System.out.println("Defining function " + funcName + " with params " + params + " and body " + body);
        LispFunction function = new LispFunction(params, body);
        context.setFunction(funcName, function);
        return funcName;
    }

    private Object applyFunction(LispFunction func, List<Object> args) {
        // Verificar que el número de argumentos sea correcto
        if (func.getParameters().size() != args.size()) {
            throw new RuntimeException("Número incorrecto de argumentos");
        }
    
        // Crear un nuevo contexto para la ejecución de la función
        ExecutionContext functionContext = new ExecutionContext(context);
        for (int i = 0; i < func.getParameters().size(); i++) {
            Object arg = args.get(i);
            functionContext.set(func.getParameters().get(i), arg); // Asignar argumentos a parámetros
        }
    
        List<Object> body = func.getBody(); // Obtener el cuerpo de la función
        Object result = null;
    
        // Evaluar cada expresión en el cuerpo
        for (Object expr : body) {
            if (expr instanceof List) { // Si es una lista (como (COND ...) o (FACTORIAL ...))
                List<Object> exprList = (List<Object>) expr;
                // Convertir la lista en una expresión LISP completa con paréntesis
                List<Token> tokens = tokensFromList(exprList);
                tokens.add(0, new Token(TokenType.LPAREN, "("));
                tokens.add(new Token(TokenType.RPAREN, ")"));
    
                // Parsear y evaluar la subexpresión recursivamente
                LispParser bodyParser = new LispParser(tokens, functionContext);
                result = bodyParser.parse(false); // Evaluar completamente
            } else if (expr instanceof String) { // Si es un símbolo
                String symbol = (String) expr;
                result = functionContext.get(symbol); // Resolver variable en el contexto
                if (result == null) result = symbol; // Si no está definida, devolver el símbolo
            } else { // Si es un valor literal (como un número)
                result = expr;
            }
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
        System.out.println("Evaluando COND con cláusulas: " + args);
        for (Object clause : args) {
            if (!(clause instanceof List)) {
                throw new RuntimeException("Debe ser lista...");
            }
    
            @SuppressWarnings("unchecked")
            List<Object> condPair = (List<Object>) clause;
            if (condPair.size() < 2) {
                throw new RuntimeException("Se necesita una condición y el resultado");
            }
    
            Object condition = condPair.get(0);
            System.out.println("Evaluando condición: " + condition);
    
            Object evalResult;
            if (condition instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> conditionList = (List<Object>) condition;
                LispParser conditionParser = new LispParser(tokensFromList(conditionList), context);
                Object parsedCondition = conditionParser.parse();
                if (parsedCondition instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> parsedConditionList = (List<Object>) parsedCondition;
                    evalResult = evaluate(parsedConditionList);
                } else {
                    evalResult = parsedCondition;
                }
            } else if (condition instanceof String && ((String) condition).equalsIgnoreCase("T")) {
                evalResult = true;
            } else {
                evalResult = condition; // Si no es lista ni "T", tratar como valor directo
            }
    
            System.out.println("Resultado de la condición: " + evalResult);
            if (evalResult instanceof Boolean && (Boolean) evalResult) {
                Object action = condPair.get(1);
                if (action instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> actionList = (List<Object>) action;
                    System.out.println("La condición es verdadera, evaluando acción: " + actionList);
                    LispParser actionParser = new LispParser(tokensFromList(actionList), context);
                    Object parsedAction = actionParser.parse();
                    if (parsedAction instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Object> parsedActionList = (List<Object>) parsedAction;
                        return evaluate(parsedActionList);
                    }
                    return parsedAction;
                }
                System.out.println("La condición es verdadera, devolviendo acción: " + action);
                return action; // Devolver valores directos como 1
            }
        }
        return null; // Si no se cumple ninguna condición
    }
    private List<Token> tokensFromList(List<Object> list) {
        List<Token> tokens = new ArrayList<>();
        for (Object obj : list) {
            if (obj instanceof Integer) {
                tokens.add(new Token(TokenType.NUMBER, obj.toString()));
            } else if (obj instanceof String) {
                tokens.add(new Token(TokenType.SYMBOL, (String) obj));
            } else if (obj instanceof List) {
                tokens.add(new Token(TokenType.LPAREN, "("));
                @SuppressWarnings("unchecked")
                List<Object> nestedList = (List<Object>) obj;
                tokens.addAll(tokensFromList(nestedList));
                tokens.add(new Token(TokenType.RPAREN, ")"));
            }
        }
        return tokens;
    }
}