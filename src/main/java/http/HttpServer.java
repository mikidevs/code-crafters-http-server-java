package http;

import http.constants.HttpMethod;
import http.utils.MapUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {

    private final int port;
    private final Map<Url, EnumMap<HttpMethod, EndpointHandler>> endpointMap;

    public HttpServer(int port) {
        this.port = port;
        this.endpointMap = new HashMap<>();
    }

    public HttpServer handle(HttpMethod method, String endpoint, EndpointHandler handler) {
        Url endpointUrl = new Url(endpoint);

        var handlers = endpointMap.computeIfAbsent(endpointUrl, url -> new EnumMap<>(HttpMethod.class));

        var endpointHandler = handlers.putIfAbsent(method, handler);

        if (endpointHandler != null) {
            throw new IllegalArgumentException("Duplicate url specified");
        }
//        MapUtils.printMap(endpointMap);
        return this;
    }

    public void listenAndServe() {

        Socket clientSocket;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept(); // Wait for connection from client.
            System.out.println("accepted new connection");

            // Setup IO
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            OutputStream out = clientSocket.getOutputStream();

            // Read in from client
            StringBuilder sb = new StringBuilder();
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                sb.append(line)
                  .append("\r\n");
            }

            HttpRequest clientReq = new HttpRequest(sb.toString());
            EndpointHandler handler = MapUtils.findHandler(clientReq, endpointMap);

            String response;

            // Handle response
            if (handler == null) {
                response = HttpResponse.notFound().buildString();
                out.write(response.getBytes());
            } else {
                response = handler.handle(clientReq).buildString();
                out.write(response.getBytes());
            }

        } catch (
                IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
