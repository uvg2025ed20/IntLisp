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
        if (variables.containsKey(key)) {
            Object value = variables.get(key);
            System.out.println("Variable encontrada en contexto actual: " + key + " = " + value);
            return value;
        }
        if (parentContext != null) {
            System.out.println("Buscando " + key + " en contexto padre...");
            return parentContext.get(key);
        }
        System.out.println("Variable no encontrada: " + key);
        return null; // Si no hay padre y no está en el contexto actual, devolver null
    }

    public void setFunction(String key, LispFunction function) {
        System.out.println("Estableciendo función en contexto: " + key);
        functions.put(key, function);
    }

    public LispFunction getFunction(String key) {
        if (functions.containsKey(key)) {
            LispFunction func = functions.get(key);
            System.out.println("Función encontrada en contexto actual: " + key);
            return func;
        }
        if (parentContext != null) {
            System.out.println("Buscando función " + key + " en contexto padre...");
            return parentContext.getFunction(key);
        }
        System.out.println("Función no encontrada: " + key);
        return null; // Si no hay padre y no está en el contexto actual, devolver null
    }

    public List<String> getAllFunctionNames() {
        List<String> names = new ArrayList<>(functions.keySet());
        if (parentContext != null) {
            names.addAll(parentContext.getAllFunctionNames());
        }
        return names;
    }
}