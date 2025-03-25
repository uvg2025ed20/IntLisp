package uvg.edu.interpreter;

import java.util.List;

/**
 * The `PrintInterpreter` class provides methods to interpret and execute the PRINT operation in Lisp.
 */
public class PrintInterpreter {

    /**
     * Interprets the PRINT operation and prints the provided argument.
     *
     * @param args The list of arguments to print. Must contain exactly one argument.
     * @return The printed argument.
     * @throws RuntimeException If the number of arguments is not exactly one.
     */
    public Object interpret(List<Object> args) {
        if (args.size() != 1) {
            throw new RuntimeException("PRINT requires 1 argument");
        }
        System.out.println(args.get(0));
        return args.get(0);
    }
}