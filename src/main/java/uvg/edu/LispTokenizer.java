package uvg.edu;

import java.util.ArrayList;
import java.util.List;

public class LispTokenizer {
    private final String input;
    private int position;

    public LispTokenizer(String input) {
        this.input = input;
        this.position = 0;
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (position < input.length()) {
            char current = input.charAt(position);
            if (Character.isWhitespace(current)) {
                position++;
            } else if (current == '(') {
                tokens.add(new Token(TokenType.LPAREN, "("));
                position++;
            } else if (current == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")"));
                position++;
            } else if (current == '\'') {
                tokens.add(new Token(TokenType.QUOTE, "'"));
                position++;
            } else if (Character.isDigit(current) || (current == '-' && peekNextIsDigit())) {
                tokens.add(readNumber());
            } else if (Character.isLetter(current) || "+-*/".indexOf(current) != -1) {
                tokens.add(readSymbol());
            } else {
                throw new RuntimeException("Carácter inesperado: " + current);
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private boolean peekNextIsDigit() {
        return position + 1 < input.length() && Character.isDigit(input.charAt(position + 1));
    }

    private Token readNumber() {
        int start = position;
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        return new Token(TokenType.NUMBER, input.substring(start, position));
    }

    private Token readSymbol() {
        int start = position;
        while (position < input.length() &&
                (Character.isLetterOrDigit(input.charAt(position)) || "+-*/".indexOf(input.charAt(position)) != -1)) {
            position++;
        }
        return new Token(TokenType.SYMBOL, input.substring(start, position));
    }
}
