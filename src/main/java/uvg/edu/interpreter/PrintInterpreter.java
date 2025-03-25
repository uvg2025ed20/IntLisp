package uvg.edu.interpreter;

import java.util.List;

public class PrintInterpreter {
    public Object interpret(List<Object> args) {
        if (args.size() != 1) {
            throw new RuntimeException("PRINT requires 1 argument");
        }
        System.out.println(args.get(0));
        return args.get(0);
    }
}
