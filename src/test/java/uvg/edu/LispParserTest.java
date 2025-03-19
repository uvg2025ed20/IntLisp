package uvg.edu;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LispParserTest {
    @Test
    public void testParseSimpleExpression() {
        LispTokenizer tokenizer = new LispTokenizer("(+ 1 2)");
        List<Token> tokens = tokenizer.tokenize();
        ExecutionContext context = new ExecutionContext();
        LispParser parser = new LispParser(tokens, context);
        Object result = parser.parse();
        assertEquals(3, result);
    }
}