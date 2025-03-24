package uvg.edu;

import java.util.ArrayList;
import java.util.List;

public class LispParser {
    private final List<Token> tokens;
    private int position;
    private final ExecutionContext context;
    private final ExecutionContext globalContext;

    public LispParser(List<Token> tokens, ExecutionContext context) {
        this.tokens = tokens;
        this.context = context;
        this.globalContext = context;
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
            return evaluate(expression, context);
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

    private Object evaluate(List<Object> expr, ExecutionContext currentContext) {
        if (expr.isEmpty()) {
            return null;
        }
        
        System.out.println("Evaluating expression: " + expr);
        
        if (expr.size() == 1 && !(expr.get(0) instanceof List)) {
            Object value = expr.get(0);
            if (value instanceof String) {
                Object resolved = currentContext.get((String) value);
                return resolved != null ? resolved : value;
            }
            return value;
        }
        
        Object operator = expr.get(0);
        System.out.println("Evaluando expresión con operador: " + operator);
        
        List<Object> evaluatedArgs = new ArrayList<>();
        for (int i = 1; i < expr.size(); i++) {
            Object arg = expr.get(i);
            if (arg instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> argList = (List<Object>) arg;
                Object evalArg = evaluate(argList, currentContext);
                evaluatedArgs.add(evalArg);
            } else if (arg instanceof String) {
                String symbol = (String) arg;
                Object value = currentContext.get(symbol);
                if (value != null) {
                    evaluatedArgs.add(value);
                } else {
                    try {
                        evaluatedArgs.add(Integer.parseInt(symbol));
                    } catch (NumberFormatException e) {
                        evaluatedArgs.add(symbol);
                    }
                }
                System.out.println("Resolviendo argumento: " + symbol + ", valor: " + value);
            } else {
                evaluatedArgs.add(arg);
            }
        }
        
        System.out.println("Argumentos evaluados para " + operator + ": " + evaluatedArgs);
        
        if (operator instanceof String) {
            String opStr = (String) operator;
            LispFunction func = currentContext.getFunction(opStr);
            if (func != null) {
                return applyFunction(func, evaluatedArgs);
            }
            
            switch (opStr.toUpperCase()) {
                case "QUOTE":
                    return expr.get(1);
                case "SETQ":
                    currentContext.set((String) expr.get(1), evaluatedArgs.get(1));
                    return evaluatedArgs.get(1);
                case "COND":
                    return cond(expr.subList(1, expr.size()), currentContext);
                case "+":
                    return add(evaluatedArgs);
                case "-":
                    return subtract(evaluatedArgs);
                case "*":
                    return multiply(evaluatedArgs);
                case "/":
                    return divide(evaluatedArgs);
                case "<":
                    return lessThan(evaluatedArgs);
                case ">":
                    return greaterThan(evaluatedArgs);
                case "EQUAL":
                    return equal(evaluatedArgs);
                default:
                    throw new RuntimeException("Operador desconocido: " + operator);
            }
        }
        
        throw new RuntimeException("Operador no válido: " + operator);
    }

    // Nuevo método auxiliar para evaluar expresiones anidadas
    private Object evaluateExpression(Object expr, ExecutionContext currentContext) {
        if (expr instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> exprList = (List<Object>) expr;
            // Si la lista tiene un operador como "<", evaluar normalmente
            if (!exprList.isEmpty() && exprList.get(0) instanceof String) {
                return evaluate(exprList, currentContext);
            }
            // Si no, evaluar cada elemento (aunque no debería ocurrir en COND)
            List<Object> result = new ArrayList<>();
            for (Object subExpr : exprList) {
                result.add(evaluateExpression(subExpr, currentContext));
            }
            return result;
        } else if (expr instanceof String) {
            String symbol = (String) expr;
            Object value = currentContext.get(symbol);
            return value != null ? value : symbol;
        }
        return expr; // Números o valores literales
    }

    private Object cond(List<Object> args, ExecutionContext currentContext) {
        System.out.println("Evaluando COND con cláusulas: " + args);
        for (Object clause : args) {
            if (!(clause instanceof List)) {
                throw new RuntimeException("Cada cláusula de COND debe ser una lista");
            }
            @SuppressWarnings("unchecked")
            List<Object> condPair = (List<Object>) clause;
            if (condPair.size() < 2) {
                throw new RuntimeException("Cada cláusula de COND debe tener condición y acción");
            }
            
            Object condition = condPair.get(0);
            Object evalResult = evaluateExpression(condition, currentContext);
            
            System.out.println("Resultado de la condición: " + evalResult);
            if (evalResult instanceof Boolean && (Boolean) evalResult) {
                Object action = condPair.get(1);
                return evaluateExpression(action, currentContext);
            }
        }
        return null;
    }
    private Object add(List<Object> args) {
        int sum = 0;
        for (Object arg : args) {
            if (arg instanceof Integer) {
                sum += (Integer) arg;
            } else {
                throw new RuntimeException("Argumento no numérico en +: " + arg);
            }
        }
        return sum;
    }
    
    private Object subtract(List<Object> args) {
        if (args.isEmpty()) throw new RuntimeException("Se necesitan argumentos para -");
        int result = (Integer) args.get(0);
        for (int i = 1; i < args.size(); i++) {
            if (args.get(i) instanceof Integer) {
                result -= (Integer) args.get(i);
            } else {
                throw new RuntimeException("Argumento no numérico en -: " + args.get(i));
            }
        }
        return result;
    }
    
    private boolean lessThan(List<Object> args) {
        if (args.size() != 2) throw new RuntimeException("Se necesitan exactamente 2 argumentos para <");
        if (!(args.get(0) instanceof Integer) || !(args.get(1) instanceof Integer)) {
            throw new RuntimeException("Argumentos no numéricos en <: " + args);
        }
        return (Integer) args.get(0) < (Integer) args.get(1);
    }
    
    private Object equal(List<Object> args) {
        if (args.size() != 2) throw new RuntimeException("Se necesitan exactamente 2 argumentos para EQUAL");
        return args.get(0).equals(args.get(1));
    }

    private int multiply(List<Object> args) {
        int result = 1;
        for (Object arg : args) {
            if (arg instanceof Integer) {
                result *= (Integer) arg;
            } else {
                throw new RuntimeException("Argumento no numérico en *: " + arg);
            }
        }
        return result;
    }

    private int divide(List<Object> args) {
        if (args.isEmpty()) throw new RuntimeException("Se necesitan argumentos para /");
        int result = (Integer) args.get(0);
        for (int i = 1; i < args.size(); i++) {
            if (args.get(i) instanceof Integer) {
                int divisor = (Integer) args.get(i);
                if (divisor == 0) throw new RuntimeException("División por cero no permitida");
                result /= divisor;
            } else {
                throw new RuntimeException("Argumento no numérico en /: " + args.get(i));
            }
        }
        return result;
    }

    private boolean greaterThan(List<Object> args) {
        if (args.size() != 2) throw new RuntimeException("Se necesitan exactamente 2 argumentos para >");
        if (!(args.get(0) instanceof Integer) || !(args.get(1) instanceof Integer)) {
            throw new RuntimeException("Argumentos no numéricos en >: " + args);
        }
        return (Integer) args.get(0) > (Integer) args.get(1);
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
        System.out.println("Llamando a función: " + func);
        System.out.println("Con argumentos: " + args);
        
        if (func.getParameters().size() != args.size()) {
            throw new RuntimeException("Número incorrecto de argumentos");
        }
        
        // Crear un nuevo contexto con el contexto global como padre
        ExecutionContext functionContext = new ExecutionContext(globalContext);
        for (int i = 0; i < func.getParameters().size(); i++) {
            functionContext.set(func.getParameters().get(i), args.get(i));
        }
        
        Object result = null;
        // Evaluar cada expresión en el cuerpo
        for (Object expr : func.getBody()) {
            System.out.println("Evaluando expresión en cuerpo de función: " + expr);
            result = evaluateExpression(expr, functionContext); // Usar evaluateExpression
        }
        
        System.out.println("Resultado de la función: " + result);
        return result;
    }
    private boolean atom(List<Object> args) {
        Object arg = args.get(0);
        return !(arg instanceof List);
    }

    private boolean list(List<Object> args) {
        Object arg = args.get(0);
        return arg instanceof List;
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