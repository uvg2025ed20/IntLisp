package uvg.edu.interpreter;

    import java.util.ArrayList;
    import java.util.List;

    import uvg.edu.ExecutionContext;
    import uvg.edu.LispFunction;

    /**
     * The `FunctionInterpreter` class provides methods to interpret and define functions in the Lisp interpreter.
     */
    public class FunctionInterpreter {
        private final ExecutionContext context; // The execution context for the Lisp interpreter

        /**
         * Constructs a `FunctionInterpreter` with the given execution context.
         *
         * @param context The execution context for the Lisp interpreter.
         */
        public FunctionInterpreter(ExecutionContext context) {
            this.context = context;
        }

        /**
         * Interprets and defines a new function in the Lisp interpreter.
         *
         * @param args The list of arguments defining the function (name, parameters, and body).
         * @return The name of the defined function.
         * @throws RuntimeException If the arguments are invalid.
         */
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