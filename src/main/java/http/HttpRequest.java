package http;

import http.constants.HttpMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpRequest {
    private HttpMethod method;
    private String requestTarget;
    private Map<String, String> headers;
    private final Map<String, String> parameters;

    private String requestBody;

    public HttpRequest(String request) {
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
        parseRequest(request);
    }

    private void parseRequest(String request) {
        Pattern pattern = Pattern.compile("(?<method>\\w+)\\s(?<target>.*)\\sHTTP/1.1\r\n");
        Matcher matcher = pattern.matcher(request);

        if (matcher.find()) {
            String method = matcher.group("method");
            String target = matcher.group("target");

            if (method.equals("GET")) {
                this.method = HttpMethod.GET;
            }
            this.requestTarget = target;

            String[] parts = request.split("\r\n", -1);
            this.headers = Arrays.stream(parts).skip(1)
                    .limit(parts.length - 2)
                    .filter(s -> !s.isEmpty())
                    .map((s) -> s.split(": "))
                    .collect(Collectors.toMap((l) -> l[0], (l) -> l[1]));

            this.requestBody = parts[parts.length - 1];
        } else {
            throw new RuntimeException("Invalid Http String");
        }
    }

    public HttpMethod method() {
        return method;
    }

    public String requestTarget() {
        return requestTarget;
    }

    public String requestBody() {
        return requestBody;
    }

    public void addParameter(String key, String value) {
        this.parameters.put(key, value);
    }

    public String getParameter(String key) {
        return this.parameters.get(key);
    }
}
