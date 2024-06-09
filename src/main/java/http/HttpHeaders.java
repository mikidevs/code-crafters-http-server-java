package http;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {
    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public HttpHeaders contentType(String contentType) {
        this.headers.put("Content-Type", contentType);
        return this;
    }

    public HttpHeaders contentLength(int contentLength) {
        this.headers.put("Content-Length", String.valueOf(contentLength));
        return this;
    }

    public static HttpHeaders builder() {
        return new HttpHeaders();
    }

    public String buildString() {
        StringBuilder sb = new StringBuilder();
        for (String key : this.headers.keySet()) {
            sb.append(key)
              .append(": ")
              .append(this.headers.get(key))
              .append("\r\n");
        }
        return sb.toString();
    }
}