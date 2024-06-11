package http;

import http.constants.HttpStatus;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private final HttpStatus httpStatus;
    private HttpHeaders headers;
    private String responseBody;

    private HttpResponse(HttpStatus httpStatus, HttpHeaders headers, String responseBody) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok() {
        return new HttpResponse(HttpStatus.OK, HttpHeaders.builder().withContentLength(0), "");
    }

    public static HttpResponse notFound() {
        return new HttpResponse(HttpStatus.NOT_FOUND, HttpHeaders.builder().withContentLength(0), "");
    }

    public HttpResponse withContent(String contentType, String body) {
        this.headers = HttpHeaders.builder()
                                  .withContentLength(body.length())
                                  .withContentType(contentType);
        return this.withBody(body);
    }

    public HttpResponse withBody(String body) {
        this.responseBody = body;
        return this;
    }

    public HttpResponse withHeaders(HttpHeaders headers) {
        this.headers = headers;
        return this;
    }

    public HttpStatus status() {
        return httpStatus;
    }

    public String buildString() {
        String statusLine = HTTP_VERSION + " " + httpStatus.getStatus();
        String headers = this.headers.buildString();

        return String.join(CRLF,
                statusLine,
                headers,
                responseBody
        );
    }

    public String getResponseBody() {
        return responseBody;
    }
}