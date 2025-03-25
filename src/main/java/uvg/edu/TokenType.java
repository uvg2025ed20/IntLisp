package uvg.edu;

/**
 * The `TokenType` enum represents the different types of tokens that can be encountered in the Lisp interpreter.
 */
public enum TokenType {
    LPAREN, // Represents the left parenthesis token
    RPAREN, // Represents the right parenthesis token
    SYMBOL, // Represents a symbol token
    NUMBER, // Represents a number token
    QUOTE,  // Represents a quote token
    STRING, // Represents a string token
    EOF     // Represents the end-of-file token
}