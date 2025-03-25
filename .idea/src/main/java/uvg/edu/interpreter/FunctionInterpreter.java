package uvg.edu.interpreter;

import java.util.ArrayList;
import java.util.List;

import uvg.edu.ExecutionContext;
import uvg.edu.LispFunction;

public class FunctionInterpreter {
    private final ExecutionContext context;

    public FunctionInterpreter(ExecutionContext context) {
        this.context = context;
    }

    public Object interpret(List<Object> args) {
        if (args.size() < 3) {
            throw new RuntimeException("DEFUN requires name, parameters, and body");
        }
        if (!(args.get(0) instanceof String)) {
            throw new RuntimeException("Function name must be a symbol");
        }
        String funcName = (String) args.get(0);

        if (!(args.get(1) instanceof List)) {
            throw new RuntimeException("Parameter list must be a list");
        }
        @SuppressWarnings("unchecked")
        List<Object> paramList = (List<Object>) args.get(1);
        List<String> params = new ArrayList<>();
        for (Object param : paramList) {
            if (!(param instanceof String)) {
                throw new RuntimeException("Parameters must be symbols");
            }
            params.add((String) param);
        }

        List<Object> body = new ArrayList<>(args.subList(2, args.size()));
        LispFunction function = new LispFunction(funcName, params, body);
        context.setFunction(funcName, function);
        return funcName;
    }
}