package uvg.edu;

    /**
     * The `Token` class represents a token in the Lisp interpreter, consisting of a type and a value.
     */
    public class Token {
        private final TokenType type; // The type of the token
        private final String value;   // The value of the token

        /**
         * Constructs a `Token` with the specified type and value.
         *
         * @param type The type of the token.
         * @param value The value of the token.
         */
        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        /**
         * Returns the type of the token.
         *
         * @return The type of the token.
         */
        public TokenType getType() {
            return type;
        }

        /**
         * Returns the value of the token.
         *
         * @return The value of the token.
         */
        public String getValue() {
            return value;
        }

        /**
         * Returns a string representation of the token.
         *
         * @return A string representation of the token.
         */
        @Override
        public String toString() {
            return "Token{" + " type=" + type + ", value='" + value + '\'' + '}';
        }
    }