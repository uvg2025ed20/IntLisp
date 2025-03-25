package uvg.edu;

        import java.util.List;

        /**
         * The `LispFunction` class represents a user-defined function in the Lisp interpreter.
         * It contains the function's name, parameters, and body.
         */
        public class LispFunction {
            private String name; // The name of the function
            private List<String> parameters; // The parameters of the function
            private List<Object> body; // The body of the function

            /**
             * Constructs a `LispFunction` with the given name, parameters, and body.
             *
             * @param name The name of the function.
             * @param parameters The parameters of the function.
             * @param body The body of the function.
             */
            public LispFunction(String name, List<String> parameters, List<Object> body) {
                this.name = name; // Initialize the name of the function
                this.parameters = parameters; // Initialize the parameters of the function
                this.body = body; // Initialize the body of the function
            }

            /**
             * Returns the name of the function.
             *
             * @return The name of the function.
             */
            public String getName() {
                return name;
            }

            /**
             * Returns the parameters of the function.
             *
             * @return The parameters of the function.
             */
            public List<String> getParameters() {
                return parameters;
            }

            /**
             * Returns the body of the function.
             *
             * @return The body of the function.
             */
            public List<Object> getBody() {
                return body;
            }
        }