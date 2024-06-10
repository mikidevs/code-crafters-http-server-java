package http;

@FunctionalInterface
public interface EndpointHandler {
    HttpResponse handle(HttpRequest req);
}
