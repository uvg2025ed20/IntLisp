package uvg.edu;

import java.util.List;
import java.util.Scanner;

public class LispRunner {
    public static void main(String[] args) {
        System.out.println("Bienvenido al intérprete de Lisp en Java.");
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("\nIngrese una expresión Lisp (o escriba 'salir' para terminar): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("salir")) {
                System.out.println("Saliendo del intérprete...");
                break;
            }

            LispTokenizer tokenizer = new LispTokenizer(input);
            List<Token> tokens = tokenizer.tokenize();

            System.out.println("Tokens generados:");
            for (Token token : tokens) {
                System.out.println(token);
            }

            LispParser parser = new LispParser(tokens);
            Object parsedExpression = parser.parse();

            System.out.println("Estructura parseada:");
            System.out.println(parsedExpression);
        }

        scanner.close();
    }
}
