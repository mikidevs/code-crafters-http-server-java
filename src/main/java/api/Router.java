package api;

import http.HttpResponse;
import http.constants.HttpMethod;
import http.HttpServer;

public class Router {
    public void route() {
        HttpServer httpServer = new HttpServer(4221, 4);

        httpServer
                .handle(HttpMethod.GET, "/", req -> HttpResponse.ok())
                .handle(HttpMethod.GET, "/hello", req -> HttpResponse.ok()
                                                                     .withBody("hello"))
                .handle(HttpMethod.GET, "/echo/{str}", (req -> {
                    String body = req.getPathVariable("str");
                    return HttpResponse.ok().withContent("text/plain", body);
                }))
                .handle(HttpMethod.GET, "/user-agent", req -> {
                    String body = req.readHeader("User-Agent");
                    return HttpResponse.ok().withContent("text/plain", body);
                });

        httpServer.listenAndServe();
    }
}
