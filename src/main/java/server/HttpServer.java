package server;

import http.*;
import http.constants.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpServer {
    private final int port;
    private final Map<String, EndpointHandler> endpoints;

    public HttpServer(int port) {
        this.port = port;
        this.endpoints = new HashMap<>();
    }

    public void handle(String endpoint, EndpointHandler handler) {
        this.endpoints.put(endpoint, handler);
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
            HttpResponse resp = new HttpResponse(HttpStatus.NOT_FOUND);

            var handler = findHandlerAndUpdateRequest(endpoints, clientReq);

            // Handle requests
            if (handler != null) {
                resp.setHttpStatus(HttpStatus.OK);
                handler.handle(resp, clientReq);

                int contentLength = resp.getResponseBody()
                                        .length();

                if (contentLength > 0) {
                    resp.setHeaders(HttpHeaders.builder()
                                               .contentType("text/plain")
                                               .contentLength(contentLength));
                }
            }
            System.out.println("resp.buildString() = " + resp.buildString());

            out.write(resp.buildString()
                          .getBytes());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private EndpointHandler findHandlerAndUpdateRequest(Map<String, EndpointHandler> endpoints, HttpRequest req) {
        String[] requestFragments = req.requestTarget()
                                       .split("/");

        for (String url : endpoints.keySet()) {
            String[] urlFragments = url.split("/");
            String lastUF = urlFragments[urlFragments.length - 1];

            // requestParams
            if (lastUF.startsWith("{") && lastUF.endsWith("}")) {
                String sDropLast = Arrays.stream(urlFragments)
                                         .limit(urlFragments.length - 1)
                                         .collect(Collectors.joining("/"));

                String cDropLast = Arrays.stream(requestFragments)
                                         .limit(urlFragments.length - 1)
                                         .collect(Collectors.joining("/"));

                if (sDropLast.equals(cDropLast)) {
                    String lastRF = requestFragments[requestFragments.length - 1];

                    String param = lastUF.replace("{", "");
                    param = param.replace("}", "");
                    req.addParameter(param, lastRF);
                    return endpoints.get(url);
                }
            }
        }

        return null;
    }
}
