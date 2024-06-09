import com.sun.tools.jconsole.JConsoleContext;
import http.HttpRequest;
import http.HttpStatus;
import http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // You can use print statements as follows for debugging, they'll be visible when running tests.
        System.out.println("Logs from your program will appear here!");

        // Uncomment this block to pass the first stage
        //
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        try {
            serverSocket = new ServerSocket(4221);
            // Since the tester restarts your program quite often, setting SO_REUSEADDR
            // ensures that we don't run into 'Address already in use' errors
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept(); // Wait for connection from client.
            System.out.println("accepted new connection");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));

            StringBuilder sb = new StringBuilder();

            String line;
            while (!(line = in.readLine()).isEmpty()) {
                sb.append(line).append("\r\n");
            }

            HttpRequest req = new HttpRequest(sb.toString());

            HttpResponse resp;

            switch (req.getRequestTarget()) {
                case "/" -> resp = new HttpResponse(HttpStatus.OK);
                default -> resp = new HttpResponse(HttpStatus.NOT_FOUND);
            }

            System.out.println(resp.getResponse());
            clientSocket.getOutputStream().write(resp.getResponse().getBytes());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
