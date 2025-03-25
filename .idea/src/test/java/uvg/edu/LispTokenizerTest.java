package uvg.edu;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LispTokenizerTest {
    @Test
    public void testTokenizeSimpleExpression() {
        LispTokenizer tokenizer = new LispTokenizer("(+ 1 2)");
        List<Token> tokens = tokenizer.tokenize();
        assertEquals(6, tokens.size());
        assertEquals(TokenType.LPAREN, tokens.get(0).getType());
        assertEquals(TokenType.SYMBOL, tokens.get(1).getType());
        assertEquals(TokenType.NUMBER, tokens.get(2).getType());
        assertEquals(TokenType.NUMBER, tokens.get(3).getType());
        assertEquals(TokenType.RPAREN, tokens.get(4).getType());
        assertEquals(TokenType.EOF, tokens.get(5).getType());
    }

    @Test
    void testTokenizeNestedExpression() {
        LispTokenizer tokenizer = new LispTokenizer("(+ 1 (* 2 3))");
        List<Token> tokens = tokenizer.tokenize();
        assertEquals(10, tokens.size());
        assertEquals(TokenType.LPAREN, tokens.get(0).getType());
        assertEquals(TokenType.SYMBOL, tokens.get(1).getType());
        assertEquals("+", tokens.get(1).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(2).getType());
        assertEquals("1", tokens.get(2).getValue());
        assertEquals(TokenType.LPAREN, tokens.get(3).getType());
        assertEquals(TokenType.SYMBOL, tokens.get(4).getType());
        assertEquals("*", tokens.get(4).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(5).getType());
        assertEquals("2", tokens.get(5).getValue());
        assertEquals(TokenType.NUMBER, tokens.get(6).getType());
        assertEquals("3", tokens.get(6).getValue());
        assertEquals(TokenType.RPAREN, tokens.get(7).getType());
        assertEquals(TokenType.RPAREN, tokens.get(8).getType());
    }
}