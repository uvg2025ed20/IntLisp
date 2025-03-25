package uvg.edu.interpreter;

import java.util.List;
import uvg.edu.ExecutionContext;
import uvg.edu.LispParser;

public class ConditionalInterpreter {
    public ConditionalInterpreter() {
    }

    @SuppressWarnings("unchecked")
    public Object interpret(List<Object> args, LispParser parser, ExecutionContext context) {
        for (Object clause : args) {
            if (!(clause instanceof List)) {
                throw new RuntimeException("COND clause must be a list");
            }
            
            List<Object> condPair = (List<Object>) clause;
            if (condPair.size() < 2) {
                throw new RuntimeException("COND clause must have condition and action");
            }

            // Evaluar la condición
            Object conditionExpr = condPair.get(0);
            Object condition;
            if (conditionExpr instanceof List) {
                condition = parser.evaluate((List<Object>) conditionExpr, context);
            } else {
                condition = (conditionExpr instanceof String) ? context.get((String) conditionExpr) : conditionExpr;
            }

            // Verificar si la condición es verdadera
            if ((condition instanceof Boolean && (Boolean) condition) || 
                (condition instanceof Integer && (Integer) condition != 0)) {
                Object action = condPair.get(1);
                if (action instanceof List) {
                    return parser.evaluate((List<Object>) action, context);
                } else if (action instanceof String) {
                    // Si es un símbolo, buscar su valor en el contexto
                    Object value = context.get((String) action);
                    return value != null ? value : action;
                } else {
                    return action;
                }
            }
        }
        return null;
    }
}