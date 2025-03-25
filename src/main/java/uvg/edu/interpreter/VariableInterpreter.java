package uvg.edu.interpreter;

import java.util.List;

import uvg.edu.ExecutionContext;

public class VariableInterpreter {
    private final ExecutionContext context;

    public VariableInterpreter(ExecutionContext context) {
        this.context = context;
    }

    public Object interpret(List<Object> args) {
        if (args.size() != 2) {
            throw new RuntimeException("SETQ requires 2 arguments");
        }
        if (!(args.get(0) instanceof String)) {
            throw new RuntimeException("SETQ first argument must be a symbol");
        }
        String varName = (String) args.get(0);
        Object value = args.get(1);
        context.set(varName, value);
        return value;
    }
}