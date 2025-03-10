package uvg.edu;

import java.util.List;

public class LispFunction {
    private List<String> parameters;
    private List<Object> body; // Esto será una lista de tokens o expresiones.

    public LispFunction(List<String> parameters, List<Object> body) {
        this.parameters = parameters;
        this.body = body;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<Object> getBody() {
        return body;
    }
}
