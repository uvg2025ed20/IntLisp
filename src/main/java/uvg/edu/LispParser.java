package uvg.edu;

import java.util.ArrayList;
import java.util.List;

public class LispParser {
    private final List<Token> tokens;
    private int position;

    public LispParser(List<Token> tokens) {
        this.tokens = tokens;
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
            position++;
            return evaluate(expression);
        } else if (token.getType() == TokenType.NUMBER) {
            return Integer.parseInt(token.getValue());
        } else if (token.getType() == TokenType.SYMBOL) {
            return token.getValue();
        } else {
            return null;
        }
    }

    private Object evaluate(List<Object> expression) {
        if (expression.isEmpty()) return null;

        String operator = (String) expression.get(0);  // El primer elemento es el operador

        switch (operator) {
            case "+":
                return add(expression.subList(1, expression.size()));  // Operación de suma
            case "-":
                return subtract(expression.subList(1, expression.size()));  // Operación de resta
            
            default:
                throw new RuntimeException("Operador desconocido: " + operator);
        }
    }

    private int add(List<Object> operands) {
        int result = 0;
        for (Object operand : operands) {
            result += (Integer) operand;
        }
        return result;
    }

    private int subtract(List<Object> operands) {
        int result = (Integer) operands.get(0);
        for (int i = 1; i < operands.size(); i++) {
            result -= (Integer) operands.get(i);
        }
        return result;
    }
}
