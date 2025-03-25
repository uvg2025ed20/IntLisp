package uvg.edu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class LispRunner {
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

    private static void processFile(String filePath, ExecutionContext context) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                processInput(line, context);
            }
        }
    }

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