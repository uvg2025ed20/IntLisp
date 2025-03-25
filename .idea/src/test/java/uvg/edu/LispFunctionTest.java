package uvg.edu;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class LispFunctionTest {
    @Test
    public void testFunctionDefinition() {
        ExecutionContext context = new ExecutionContext();
        LispFunction function = new LispFunction(List.of("x"), List.of("(+ x 1)"));
        context.setFunction("increment", function);
        assertNotNull(context.getFunction("increment"));
    }
}