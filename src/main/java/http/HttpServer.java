package http;

import http.constants.HttpMethod;
import http.utils.MapUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Executable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {

    private final int port;
    private final Map<Url, EnumMap<HttpMethod, EndpointHandler>> endpointMap;
    private final ExecutorService executorService;

    public HttpServer(int port, int concurrencyLevel) {
        this.port = port;
        this.endpointMap = new HashMap<>();
        this.executorService = Executors.newFixedThreadPool(concurrencyLevel);
    }

    public HttpServer handle(HttpMethod method, String endpoint, EndpointHandler handler) {
        Url endpointUrl = new Url(endpoint);

        var handlers = endpointMap.computeIfAbsent(endpointUrl, url -> new EnumMap<>(HttpMethod.class));

        var endpointHandler = handlers.putIfAbsent(method, handler);

        if (endpointHandler != null) {
            throw new IllegalArgumentException("Duplicate url specified");
        }
        return this;
    }

    public void listenAndServe() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);

            // Keep open
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Wait for connection from client.

                // Handle concurrent connections
                executorService.execute(() -> {
                    System.out.println("accepted new connection");
                    handleRequest(clientSocket);
                });
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private void handleRequest(Socket clientSocket) {
        // Setup IO
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            // Read in from client
            StringBuilder sb = new StringBuilder();
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                sb.append(line)
                  .append("\r\n");
            }

            HttpRequest clientReq = new HttpRequest(sb.toString());

            System.out.println("client request: " + clientReq.url());
            EndpointHandler handler = MapUtils.findHandler(clientReq, endpointMap);

            String response;

            // Handle response
            if (handler == null) {
                response = HttpResponse.notFound()
                                       .buildString();
                out.write(response.getBytes());
            } else {
                response = handler.handle(clientReq)
                                  .buildString();
                out.write(response.getBytes());
            }
        } catch (IOException e) {
            System.out.println("Failed accessing stream: " + e.getMessage());
        }

    }
}
