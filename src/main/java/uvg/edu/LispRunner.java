package uvg.edu;

import java.util.List;
import java.util.Scanner;

public class LispRunner {
    public static void main(String[] args) {
        System.out.println("Bienvenido al intérprete de Lisp en Java.");
        Scanner scanner = new Scanner(System.in);
        ExecutionContext context = new ExecutionContext();
        context.set("T", true);

        while (true) {
            System.out.print("\nIngrese una expresión Lisp (o 'salir' para terminar): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("salir")) {
                System.out.println("Saliendo del intérprete...");
                break;
            }

            LispTokenizer tokenizer = new LispTokenizer(input);
            List<Token> tokens = tokenizer.tokenize();

            LispParser parser = new LispParser(tokens, context);
            Object result = parser.parse();

            System.out.println("Resultado: " + result);
        }
        scanner.close();
    }
}