package uvg.edu.interpreter;

import java.util.List;

public class QuoteInterpreter {
    public Object interpret(List<Object> args) {
        if (args.size() != 1) {
            throw new RuntimeException("QUOTE requires 1 argument");
        }
        return args.get(0);
    }
}
