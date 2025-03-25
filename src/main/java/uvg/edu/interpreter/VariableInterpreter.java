package uvg.edu.interpreter;

        import java.util.List;

        import uvg.edu.ExecutionContext;

        /**
         * The `VariableInterpreter` class provides methods to interpret and execute the SETQ operation in Lisp.
         */
        public class VariableInterpreter {
            private final ExecutionContext context; // The execution context for the Lisp interpreter

            /**
             * Constructs a `VariableInterpreter` with the given execution context.
             *
             * @param context The execution context for the Lisp interpreter.
             */
            public VariableInterpreter(ExecutionContext context) {
                this.context = context;
            }

            /**
             * Interprets the SETQ operation and sets the value of a variable in the execution context.
             *
             * @param args The list of arguments for the SETQ operation. Must contain exactly two arguments: the variable name and the value.
             * @return The value that was set.
             * @throws RuntimeException If the number of arguments is not exactly two or if the first argument is not a symbol.
             */
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