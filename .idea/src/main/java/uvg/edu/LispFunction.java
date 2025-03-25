package uvg.edu;

import java.util.List;
public class LispFunction {
    private String name; // Agregado
    private List<String> parameters;
    private List<Object> body;

    public LispFunction(String name, List<String> parameters, List<Object> body) {
        this.name = name; // Agregado
        this.parameters = parameters;
        this.body = body;
    }

    public String getName() { // Agregado
        return name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public List<Object> getBody() {
        return body;
    }
}
