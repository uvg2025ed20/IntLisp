package uvg.edu;

    import java.util.HashMap;
    import java.util.List;
    import java.util.ArrayList;
    import java.util.Map;

    /**
     * The `ExecutionContext` class represents the execution context for the Lisp interpreter.
     * It stores variables and functions, and supports nested contexts.
     */
    public class ExecutionContext {
        private Map<String, Object> variables = new HashMap<>(); // Stores variables in the current context
        private Map<String, LispFunction> functions = new HashMap<>(); // Stores functions in the current context
        private ExecutionContext parentContext; // Reference to the parent context, if any

        /**
         * Constructs an `ExecutionContext` with no parent context.
         */
        public ExecutionContext() {
            this(null);
        }

        /**
         * Constructs an `ExecutionContext` with the specified parent context.
         *
         * @param parent The parent execution context.
         */
        public ExecutionContext(ExecutionContext parent) {
            this.parentContext = parent;
        }

        /**
         * Sets a variable in the current context.
         *
         * @param key The name of the variable.
         * @param value The value of the variable.
         */
        public void set(String key, Object value) {
            variables.put(key, value);
        }

        /**
         * Gets the value of a variable from the current context or any parent context.
         *
         * @param key The name of the variable.
         * @return The value of the variable, or `null` if not found.
         */
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

        /**
         * Sets a function in the current context.
         *
         * @param key The name of the function.
         * @param function The `LispFunction` object representing the function.
         */
        public void setFunction(String key, LispFunction function) {
            functions.put(key, function);
        }

        /**
         * Gets a function from the current context or any parent context.
         *
         * @param key The name of the function.
         * @return The `LispFunction` object representing the function, or `null` if not found.
         */
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

        /**
         * Gets the names of all functions in the current context and all parent contexts.
         *
         * @return A list of function names.
         */
        public List<String> getAllFunctionNames() {
            List<String> names = new ArrayList<>(functions.keySet());
            if (parentContext != null) {
                names.addAll(parentContext.getAllFunctionNames());
            }
            return names;
        }
    }