package api;

import http.HttpHeaders;
import http.HttpResponse;
import http.constants.HttpMethod;
import http.HttpServer;

public class Router {
    public void route() {
        HttpServer httpServer = new HttpServer(4221);

        httpServer
                .handle(HttpMethod.GET, "/hello", req -> HttpResponse.ok()
                                                                    .withBody("hello"))
                .handle(HttpMethod.GET, "/echo/{str}", (req -> {
                    String body = req.getPathVariable("str");
                    return HttpResponse.ok()
                                       .withHeaders(HttpHeaders.builder()
                                                               .contentLength(body.length())
                                                               .contentType("text/plain"))
                                       .withBody(body);
                }));

        httpServer.listenAndServe();
    }
}
