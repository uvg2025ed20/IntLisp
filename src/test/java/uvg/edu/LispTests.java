package uvg.edu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import uvg.edu.interpreter.*;

public class LispTests {
    private ExecutionContext context;
    private LispTokenizer tokenizer;
    private LispParser parser;
    private ArithmeticInterpreter arithmetic;
    private FunctionInterpreter functionInterpreter;
    private VariableInterpreter variableInterpreter;
    private PredicateInterpreter predicateInterpreter;

    @BeforeEach
    public void setUp() {
        context = new ExecutionContext();
        arithmetic = new ArithmeticInterpreter();
        functionInterpreter = new FunctionInterpreter(context);
        variableInterpreter = new VariableInterpreter(context);
        predicateInterpreter = new PredicateInterpreter();
    }

    @Test
    public void testExecutionContextVariables() {
        context.set("X", 10);
        assertEquals(10, context.get("X"));
    }

    @Test
    public void testExecutionContextFunctions() {
        LispFunction func = new LispFunction("INCREMENT", Arrays.asList("N"), Arrays.asList(Arrays.asList("+", "N", 1)));
        context.setFunction("INCREMENT", func);
        assertNotNull(context.getFunction("INCREMENT"));
    }

    @Test
    public void testLispTokenizer() {
        tokenizer = new LispTokenizer("(+ 1 2)");
        List<Token> tokens = tokenizer.tokenize();
        assertEquals(6, tokens.size());
        assertEquals("+", tokens.get(1).getValue());
    }

    @Test
    public void testArithmeticInterpreter() {
        assertEquals(5, arithmetic.interpret("+", Arrays.asList(2, 3)));
        assertEquals(1, arithmetic.interpret("-", Arrays.asList(5, 4)));
        assertEquals(6, arithmetic.interpret("*", Arrays.asList(2, 3)));
        assertEquals(2, arithmetic.interpret("/", Arrays.asList(6, 3)));
    }

    @Test
    public void testPredicateInterpreter() {
        assertTrue((Boolean) predicateInterpreter.interpret("<", Arrays.asList(2, 3)));
        assertFalse((Boolean) predicateInterpreter.interpret(">", Arrays.asList(2, 3)));
        assertTrue((Boolean) predicateInterpreter.interpret("EQUAL", Arrays.asList(3, 3)));
    }

    @Test
    public void testVariableInterpreter() {
        variableInterpreter.interpret(Arrays.asList("A", 5));
        assertEquals(5, context.get("A"));
    }

    @Test
    public void testFunctionInterpreter() {
        List<Object> defunArgs = Arrays.asList("SQUARE", Arrays.asList("X"), Arrays.asList("*", "X", "X"));
        functionInterpreter.interpret(defunArgs);
        assertNotNull(context.getFunction("SQUARE"));
    }

    @Test
    public void testLispTokenizerWithComplexExpression() {
        tokenizer = new LispTokenizer("(defun factorial (n) (if (<= n 1) 1 (* n (factorial (- n 1)))))");
        List<Token> tokens = tokenizer.tokenize();
        assertFalse(tokens.isEmpty());
        assertEquals("DEFUN", tokens.get(1).getValue().toUpperCase());
    }

    @Test
    public void testArithmeticWithNegativeNumbers() {
        assertEquals(-1, arithmetic.interpret("+", Arrays.asList(-3, 2)));
        assertEquals(-5, arithmetic.interpret("-", Arrays.asList(-2, 3)));
        assertEquals(6, arithmetic.interpret("*", Arrays.asList(-2, -3)));
        assertEquals(-2, arithmetic.interpret("/", Arrays.asList(-6, 3)));
    }

    @Test
    public void testPredicateWithEdgeCases() {
        assertFalse((Boolean) predicateInterpreter.interpret("<", Arrays.asList(3, 3)));
        assertTrue((Boolean) predicateInterpreter.interpret("<=", Arrays.asList(3, 3)));
        assertFalse((Boolean) predicateInterpreter.interpret(">", Arrays.asList(1, 3)));
    }

    @Test
    public void testExecutionContextFunctionLookup() {
        assertNull(context.getFunction("NON_EXISTENT"));
    }

    @Test
    public void testFunctionWithNestedCalls() {
        List<Object> defunArgs = Arrays.asList("DOUBLE", Arrays.asList("X"), Arrays.asList("*", "X", 2));
        functionInterpreter.interpret(defunArgs);
        LispFunction func = context.getFunction("DOUBLE");
        assertNotNull(func);
        assertEquals("DOUBLE", func.getName());
    }

    @Test
    public void testSetVariableAndRetrieve() {
        variableInterpreter.interpret(Arrays.asList("B", 20));
        assertEquals(20, context.get("B"));
    }
}