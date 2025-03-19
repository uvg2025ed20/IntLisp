package uvg.edu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionContext {
    private Map<String, Object> variables = new HashMap<>();
    private Map<String, LispFunction> functions = new HashMap<>();
    private ExecutionContext parentContext;  // Added parent context reference

    public ExecutionContext() {
        this(null);
    }

    public ExecutionContext(ExecutionContext parent) {
        this.parentContext = parent;
    }

    public void set(String key, Object value) {
        variables.put(key, value);
    }

    public Object get(String key) {
        Object value = variables.get(key);
        if (value == null && parentContext != null) {
            return parentContext.get(key);
        }
        return value;
    }

    public void setFunction(String key, LispFunction function) {
        functions.put(key, function);
    }

    public LispFunction getFunction(String key) {
        LispFunction func = functions.get(key);
        if (func == null && parentContext != null) {
            return parentContext.getFunction(key);
        }
        return func;
    }

    public List<String> getAllFunctionNames() {
        List<String> names = new ArrayList<>(functions.keySet());
        if (parentContext != null) {
            names.addAll(parentContext.getAllFunctionNames());
        }
        return names;
    }
}
