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
            return expression;
        } else if (token.getType() == TokenType.NUMBER) {
            return Integer.parseInt(token.getValue());
        } else if (token.getType() == TokenType.SYMBOL) {
            return token.getValue();
        } else {
            return null;
        }
    }
}
