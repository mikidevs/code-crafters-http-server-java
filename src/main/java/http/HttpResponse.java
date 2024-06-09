package http;

import http.constants.HttpStatus;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private HttpStatus httpStatus;
    private HttpHeaders headers;
    private String responseBody;

    public HttpResponse(HttpStatus httpStatus, HttpHeaders headers, String responseBody) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public HttpResponse(HttpStatus httpStatus, HttpHeaders headers) {
        this(httpStatus, headers, "");
    }

    public HttpResponse(HttpStatus httpStatus) {
        this(httpStatus, new HttpHeaders());
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

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
    }
}
