package http;

@FunctionalInterface
public interface EndpointHandler {
    void handle(HttpResponse response, HttpRequest request);
}
