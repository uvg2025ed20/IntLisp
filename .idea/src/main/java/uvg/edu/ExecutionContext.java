package uvg.edu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutionContext {
    private Map<String, Object> variables = new HashMap<>();
    private Map<String, LispFunction> functions = new HashMap<>();
    private ExecutionContext parentContext;  // Referencia al contexto padre

    public ExecutionContext() {
        this(null);
    }

    public ExecutionContext(ExecutionContext parent) {
        this.parentContext = parent;
    }

    public void set(String key, Object value) {
        System.out.println("Estableciendo variable en contexto: " + key + " = " + value);
        variables.put(key, value);
    }

    public Object get(String key) {
        ExecutionContext current = this;
        while (current != null) {
            if (current.variables.containsKey(key)) {
                Object value = current.variables.get(key);
                System.out.println("Variable encontrada en contexto actual: " + key + " = " + value);
                return value;
            }
            System.out.println("Buscando " + key + " en contexto padre...");
            current = current.parentContext;
        }
        System.out.println("Variable no encontrada: " + key);
        return null;
    }
    

    public void setFunction(String key, LispFunction function) {
        System.out.println("Estableciendo función en contexto: " + key);
        functions.put(key, function);
    }

    public LispFunction getFunction(String key) {
        ExecutionContext current = this;
        while (current != null) {
            if (current.functions.containsKey(key)) {
                LispFunction func = current.functions.get(key);
                System.out.println("Función encontrada en contexto actual: " + key);
                return func;
            }
            System.out.println("Buscando función " + key + " en contexto padre...");
            current = current.parentContext;
        }
        System.out.println("Función no encontrada: " + key);
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