package uvg.edu;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class ExecutionContext {
    private Map<String, Object> variables = new HashMap<>();
    private Map<String, LispFunction> functions = new HashMap<>();
    private ExecutionContext parentContext;

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
        ExecutionContext current = this;
        while (current != null) {
            if (current.variables.containsKey(key)) {
                return current.variables.get(key);
            }
            current = current.parentContext;
        }
        return null;
    }

    public void setFunction(String key, LispFunction function) {
        functions.put(key, function);
    }

    public LispFunction getFunction(String key) {
        ExecutionContext current = this;
        while (current != null) {
            if (current.functions.containsKey(key)) {
                return current.functions.get(key);
            }
            current = current.parentContext;
        }
        return null;
    }

    public List<String> getAllFunctionNames() {
        List<String> names = new ArrayList<>(functions.keySet());
        if (parentContext != null) {
            names.addAll(parentContext.getAllFunctionNames());
        }
        return names;
    }
}