package uvg.edu;

import java.util.ArrayList;
import java.util.List;

/**
 * The `LispTokenizer` class is responsible for tokenizing a given Lisp input string.
 */
public class LispTokenizer {
    private final String input; // The input string to tokenize
    private int position; // The current position in the input string

    /**
     * Constructs a `LispTokenizer` with the specified input string.
     *
     * @param input The input string to tokenize.
     */
    public LispTokenizer(String input) {
        this.input = input;
        this.position = 0;
    }

    /**
     * Tokenizes the input string and returns a list of tokens.
     *
     * @return A list of tokens.
     * @throws RuntimeException If an unexpected character is encountered.
     */
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
            } else if (current == '"') {
                tokens.add(readString());
            } else if (Character.isDigit(current) || (current == '-' && peekNextIsDigit())) {
                tokens.add(readNumber());
            }
            // Handles <= and >= operators before reading normal symbols
            else if ((current == '<' || current == '>') && position + 1 < input.length() && input.charAt(position + 1) == '=') {
                tokens.add(new Token(TokenType.SYMBOL, Character.toString(current) + "="));
                position += 2; // Skip two positions
            }
            // Handles normal symbols
            else if (Character.isLetter(current) || "+-*/<>".indexOf(current) != -1) {
                tokens.add(readSymbol());
            } else {
                throw new RuntimeException("Unexpected character: " + current);
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    /**
     * Checks if the next character is a digit.
     *
     * @return `true` if the next character is a digit, `false` otherwise.
     */
    private boolean peekNextIsDigit() {
        return position + 1 < input.length() && Character.isDigit(input.charAt(position + 1));
    }

    /**
     * Reads a number token from the input string.
     *
     * @return A number token.
     */
    private Token readNumber() {
        int start = position;
        while (position < input.length() && Character.isDigit(input.charAt(position))) {
            position++;
        }
        return new Token(TokenType.NUMBER, input.substring(start, position));
    }

    /**
     * Reads a symbol token from the input string.
     *
     * @return A symbol token.
     */
    private Token readSymbol() {
        int start = position;
        while (position < input.length() &&
                (Character.isLetterOrDigit(input.charAt(position)) ||
                 "+-*/<>".indexOf(input.charAt(position)) != -1)) {
            position++;
        }
        return new Token(TokenType.SYMBOL, input.substring(start, position));
    }

    /**
     * Reads a string token from the input string.
     *
     * @return A string token.
     * @throws RuntimeException If the string is not closed.
     */
    private Token readString() {
        position++;
        int start = position;

        while (position < input.length() && input.charAt(position) != '"') {
            position++;
        }

        if (position >= input.length()) {
            throw new RuntimeException("Unclosed string literal");
        }

        String value = input.substring(start, position);

        position++;
        return new Token(TokenType.STRING, value);
    }
}