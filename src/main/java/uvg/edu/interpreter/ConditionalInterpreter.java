package uvg.edu.interpreter;

            import java.util.List;
            import uvg.edu.ExecutionContext;
            import uvg.edu.LispParser;

            /**
             * The `ConditionalInterpreter` class provides methods to interpret and evaluate conditional expressions in Lisp.
             */
            public class ConditionalInterpreter {

                /**
                 * Constructs a `ConditionalInterpreter`.
                 */
                public ConditionalInterpreter() {
                }

                /**
                 * Interprets and evaluates a list of conditional clauses.
                 *
                 * @param args The list of conditional clauses to interpret.
                 * @param parser The `LispParser` used to evaluate expressions.
                 * @param context The execution context for the Lisp interpreter.
                 * @return The result of the first true condition's action, or `null` if no conditions are true.
                 * @throws RuntimeException If a clause is not a list or if a clause does not have both condition and action.
                 */
                @SuppressWarnings("unchecked")
                public Object interpret(List<Object> args, LispParser parser, ExecutionContext context) {
                    for (Object clause : args) {
                        if (!(clause instanceof List)) {
                            throw new RuntimeException("COND clause must be a list");
                        }

                        List<Object> condPair = (List<Object>) clause;
                        if (condPair.size() < 2) {
                            throw new RuntimeException("COND clause must have condition and action");
                        }

                        // Evaluate the condition
                        Object conditionExpr = condPair.get(0);
                        Object condition;
                        if (conditionExpr instanceof List) {
                            condition = parser.evaluate((List<Object>) conditionExpr, context);
                        } else {
                            condition = (conditionExpr instanceof String) ? context.get((String) conditionExpr) : conditionExpr;
                        }

                        // Check if the condition is true
                        if ((condition instanceof Boolean && (Boolean) condition) ||
                            (condition instanceof Integer && (Integer) condition != 0)) {
                            Object action = condPair.get(1);
                            if (action instanceof List) {
                                return parser.evaluate((List<Object>) action, context);
                            } else if (action instanceof String) {
                                // If it is a symbol, look up its value in the context
                                Object value = context.get((String) action);
                                return value != null ? value : action;
                            } else {
                                return action;
                            }
                        }
                    }
                    return null;
                }
            }