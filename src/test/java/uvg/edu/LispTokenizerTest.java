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
}