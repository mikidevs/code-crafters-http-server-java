package http;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    private final HttpStatus httpStatus;
    private final String headers;
    private final String responseBody;

    public HttpResponse(HttpStatus httpStatus, String headers, String responseBody) {
        this.httpStatus = httpStatus;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public HttpResponse(HttpStatus httpStatus, String headers) {
        this(httpStatus, headers, "");
    }

    public HttpResponse(HttpStatus httpStatus) {
        this(httpStatus, "");
    }

    public String getResponse() {
        String statusLine = HTTP_VERSION + " " + httpStatus.getStatus();
        return Stream.of(
                        statusLine,
                        headers,
                        responseBody
                )
                .map(Object::toString)
                .collect(Collectors.joining(CRLF));
    }
}
