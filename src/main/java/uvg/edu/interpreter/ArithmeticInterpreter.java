package uvg.edu.interpreter;

    import java.util.List;

    /**
     * The `ArithmeticInterpreter` class provides methods to interpret and evaluate basic arithmetic operations.
     */
    public class ArithmeticInterpreter {

        /**
         * Interprets the given arithmetic operator and applies it to the provided arguments.
         *
         * @param operator The arithmetic operator to apply (e.g., "+", "-", "*", "/").
         * @param args The list of arguments to which the operator is applied.
         * @return The result of the arithmetic operation.
         * @throws RuntimeException If the operator is unknown or if there are non-numeric arguments.
         */
        public Object interpret(String operator, List<Object> args) {
            switch (operator.toUpperCase()) {
                case "+":
                    return add(args);
                case "-":
                    return subtract(args);
                case "*":
                    return multiply(args);
                case "/":
                    return divide(args);
                default:
                    throw new RuntimeException("Unknown arithmetic operator: " + operator);
            }
        }

        /**
         * Adds the provided arguments.
         *
         * @param args The list of arguments to add.
         * @return The sum of the arguments.
         * @throws RuntimeException If any argument is non-numeric.
         */
        private Integer add(List<Object> args) {
            int sum = 0;
            for (Object arg : args) {
                if (!(arg instanceof Integer)) {
                    throw new RuntimeException("Non-numeric argument in +: " + arg);
                }
                sum += (Integer) arg;
            }
            return sum;
        }

        /**
         * Subtracts the provided arguments.
         *
         * @param args The list of arguments to subtract.
         * @return The result of the subtraction.
         * @throws RuntimeException If any argument is non-numeric or if no arguments are provided.
         */
        private Integer subtract(List<Object> args) {
            if (args.isEmpty()) throw new RuntimeException("- requires arguments");
            if (!(args.get(0) instanceof Integer)) {
                throw new RuntimeException("Non-numeric argument in -: " + args.get(0));
            }
            int result = (Integer) args.get(0);
            for (int i = 1; i < args.size(); i++) {
                if (!(args.get(i) instanceof Integer)) {
                    throw new RuntimeException("Non-numeric argument in -: " + args.get(i));
                }
                result -= (Integer) args.get(i);
            }
            return result;
        }

        /**
         * Multiplies the provided arguments.
         *
         * @param args The list of arguments to multiply.
         * @return The product of the arguments.
         * @throws RuntimeException If any argument is non-numeric.
         */
        private Integer multiply(List<Object> args) {
            int result = 1;
            for (Object arg : args) {
                if (!(arg instanceof Integer)) {
                    throw new RuntimeException("Non-numeric argument in *: " + arg);
                }
                result *= (Integer) arg;
            }
            return result;
        }

        /**
         * Divides the provided arguments.
         *
         * @param args The list of arguments to divide.
         * @return The result of the division.
         * @throws RuntimeException If any argument is non-numeric, if no arguments are provided, or if division by zero occurs.
         */
        private Integer divide(List<Object> args) {
            if (args.isEmpty()) throw new RuntimeException("/ requires arguments");
            if (!(args.get(0) instanceof Integer)) {
                throw new RuntimeException("Non-numeric argument in /: " + args.get(0));
            }
            int result = (Integer) args.get(0);
            for (int i = 1; i < args.size(); i++) {
                if (!(args.get(i) instanceof Integer)) {
                    throw new RuntimeException("Non-numeric argument in /: " + args.get(i));
                }
                int divisor = (Integer) args.get(i);
                if (divisor == 0) throw new RuntimeException("Division by zero");
                result /= divisor;
            }
            return result;
        }
    }