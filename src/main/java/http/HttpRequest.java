package http;

import http.constants.HttpMethod;
import http.utils.ArrayUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpRequest {
    private HttpMethod method;
    private Url url;
    private HttpHeaders headers;
    private String body;

    private final Map<String, String> pathVariables;

    public HttpRequest(String request) {
        this.headers = HttpHeaders.empty();
        this.pathVariables = new HashMap<>();
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
            this.url = new Url(target);

            String[] parts = request.split("\r\n", -1);
            var headerMap = Arrays.stream(parts).skip(1)
                    .limit(parts.length - 2)
                    .filter(s -> !s.isEmpty())
                    .map((s) -> s.split(": "))
                    .collect(Collectors.toMap((l) -> l[0], (l) -> l[1]));

            this.headers = HttpHeaders.fromMap(headerMap);
            this.body = ArrayUtils.lastElement(parts);
        } else {
            throw new RuntimeException("Invalid Http String");
        }
    }

    public HttpMethod method() {
        return method;
    }

    public Url url() { return url; }

    public String body() {
        return body;
    }

    public String readHeader(String headerName) {
        return this.headers.getHeader(headerName);
    }

    public String getPathVariable(String key) {
        return pathVariables.get(key);
    }

    public void setPathVariable(String key, String value) {
        this.pathVariables.put(key, value);
    }
}