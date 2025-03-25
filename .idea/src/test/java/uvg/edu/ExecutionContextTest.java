package uvg.edu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExecutionContextTest {
    @Test
    public void testSetAndGetVariable() {
        ExecutionContext context = new ExecutionContext();
        context.set("x", 42);
        assertEquals(42, context.get("x"));
    }

    @Test
    void testVariableNotFound() {
        ExecutionContext context = new ExecutionContext();
        assertNull(context.get("nonExistentVar"));
    }
}