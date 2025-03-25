package uvg.edu;

        import java.io.BufferedReader;
        import java.io.FileReader;
        import java.io.IOException;
        import java.util.List;
        import java.util.Scanner;

        /**
         * The LispRunner class provides a simple Lisp interpreter in Java.
         * It allows users to input Lisp expressions directly or read them from a file.
         */
        public class LispRunner {

            /**
             * The main method is the entry point of the Lisp interpreter.
             * It provides a command-line interface for users to input Lisp expressions or read them from a file.
             *
             * @param args Command-line arguments (not used).
             */
            public static void main(String[] args) {
                System.out.println("Bienvenido al intérprete de Lisp en Java.");
                Scanner scanner = new Scanner(System.in);
                ExecutionContext context = new ExecutionContext();
                context.set("T", true);

                while (true) {
                    System.out.print("\nIngrese una expresión Lisp, 'archivo' para leer un archivo, o 'salir' para terminar: ");
                    String input = scanner.nextLine();

                    if (input.equalsIgnoreCase("salir")) {
                        System.out.println("Saliendo del intérprete...");
                        break;
                    } else if (input.equalsIgnoreCase("archivo")) {
                        System.out.print("Ingrese la ruta completa del archivo: ");
                        String filePath = scanner.nextLine();
                        try {
                            processFile(filePath, context);
                        } catch (IOException e) {
                            System.out.println("Error al leer el archivo: " + e.getMessage());
                        }
                    } else {
                        processInput(input, context);
                    }
                }
                scanner.close();
            }

            /**
             * Processes a Lisp file by reading each line and evaluating it.
             *
             * @param filePath The path to the Lisp file.
             * @param context The execution context for the Lisp interpreter.
             * @throws IOException If an I/O error occurs while reading the file.
             */
            private static void processFile(String filePath, ExecutionContext context) throws IOException {
                try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        processInput(line, context);
                    }
                }
            }

            /**
             * Processes a single Lisp expression input by the user.
             *
             * @param input The Lisp expression to process.
             * @param context The execution context for the Lisp interpreter.
             */
            private static void processInput(String input, ExecutionContext context) {
                System.out.println("Operación: " + input);
                LispTokenizer tokenizer = new LispTokenizer(input);
                List<Token> tokens = tokenizer.tokenize();

                LispParser parser = new LispParser(tokens, context);
                Object parsed = parser.parse();

                // Evaluate the parsed result if it’s a list (i.e., an expression)
                Object result = parsed;
                if (parsed instanceof List) {
                    result = parser.evaluate((List<Object>) parsed, context);
                }

                System.out.println("Resultado: " + result);
            }
        }