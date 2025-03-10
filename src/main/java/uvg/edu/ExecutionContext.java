package uvg.edu;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {
    private Map<String, Object> variables = new HashMap<>();
    private Map<String, LispFunction> functions = new HashMap<>();

    public void set(String key, Object value) {
        variables.put(key, value);
    }

    public Object get(String key) {
        return variables.get(key);
    }

    public void setFunction(String key, LispFunction function) {
        functions.put(key, function);
    }

    public LispFunction getFunction(String key) {
        return functions.get(key);
    }
}
