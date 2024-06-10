import server.HttpServer;

public class Main {
    public static void main(String[] args) {
        HttpServer httpServer = new HttpServer(4221);

        httpServer.handle("/echo/{str}", (resp, req) ->
                resp.setResponseBody(req.getParameter("str")));
        httpServer.handle("hello", (response, request) -> response.setResponseBody("hello"));

        httpServer.listenAndServe();
    }
}
