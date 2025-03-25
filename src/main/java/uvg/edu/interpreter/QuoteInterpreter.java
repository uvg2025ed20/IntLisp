package uvg.edu.interpreter;

            import java.util.List;

            /**
             * The `QuoteInterpreter` class provides methods to interpret and evaluate the QUOTE operation in Lisp.
             */
            public class QuoteInterpreter {

                /**
                 * Interprets the QUOTE operation and returns the provided argument without evaluating it.
                 *
                 * @param args The list of arguments to quote. Must contain exactly one argument.
                 * @return The quoted argument.
                 * @throws RuntimeException If the number of arguments is not exactly one.
                 */
                public Object interpret(List<Object> args) {
                    if (args.size() != 1) {
                        throw new RuntimeException("QUOTE requires 1 argument");
                    }
                    return args.get(0);
                }
            }