package server;

import http.*;
import http.constants.HttpStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpServer {
    private final int port;
    private Map<String, EndpointHandler> endpoints;

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
                sb.append(line).append("\r\n");
            }

            HttpRequest clientReq = new HttpRequest(sb.toString());
            HttpResponse resp = new HttpResponse(HttpStatus.NOT_FOUND);

            //! Has a side effect of modifying the endpoints map
            addParamsToRequest(clientReq);

            // Handle requests
            if (endpoints.containsKey(clientReq.requestTarget())) {
                resp.setHttpStatus(HttpStatus.OK);
                endpoints.get(clientReq.requestTarget()).handle(resp, clientReq);
                int contentLength = resp.getResponseBody().length();
                if (contentLength > 0) {
                    resp.setHeaders(HttpHeaders.builder().contentType("text/plain").contentLength(contentLength));
                }
            }
            System.out.println("resp.buildString() = " + resp.buildString());

            out.write(resp.buildString().getBytes());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private void addParamsToRequest(HttpRequest clientReq) {
        var transformedEndpoints = new HashMap<String, EndpointHandler>();

        for (String url : endpoints.keySet()) {
            String[] fragments = url.split("/");

            String last = fragments[fragments.length - 1];

            // find params if there are any
            if(last.startsWith("{") && last.endsWith("}")) {
                String param = last.replace("{", "");
                param = param.replace("}", "");

                String[] clientFragments = clientReq.requestTarget().split("/");
                String lastCF = clientFragments[fragments.length - 1];

                clientReq.addParameter(param, lastCF);
                fragments[fragments.length - 1] = lastCF;
            }
            transformedEndpoints.put(String.join("/", fragments), endpoints.get(url));
        }
        this.endpoints = transformedEndpoints;
    }
}
