package http;

import java.util.HashMap;
import java.util.Map;

public class HttpHeaders {
    private Map<String, String> headers;

    private HttpHeaders() {
        this.headers = new HashMap<>();
    }

    public static HttpHeaders empty() {
        return new HttpHeaders();
    }

    public HttpHeaders withContentType(String contentType) {
        this.headers.put("Content-Type", contentType);
        return this;
    }

    public HttpHeaders withContentLength(int contentLength) {
        this.headers.put("Content-Length", String.valueOf(contentLength));
        return this;
    }

    public String getHeader(String headerName) {
        return this.headers.get(headerName);
    }

    public static HttpHeaders fromMap(Map<String, String> httpHeaderMap) {
        HttpHeaders headers = new HttpHeaders();
        headers.setHeaders(httpHeaderMap);
        return headers;
    }

    private void setHeaders(Map<String, String> headers) {
        this.headers = headers;
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