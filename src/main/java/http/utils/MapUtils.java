package http.utils;

import http.EndpointHandler;
import http.HttpRequest;
import http.Url;
import http.constants.HttpMethod;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class MapUtils {
    public static void printMap(Map<Url, Map<HttpMethod, EndpointHandler>> endpointMap) {
        StringBuilder sb = new StringBuilder();
        for (Url url : endpointMap.keySet()) {
            sb.append("Url: ")
              .append(url)
              .append("\n");
            var methodToHandlerMap = endpointMap.get(url);

            for (HttpMethod httpMethod : methodToHandlerMap.keySet()) {
                sb.append("\t")
                  .append(httpMethod.name())
                  .append(" ")
                  .append(methodToHandlerMap.get(httpMethod))
                  .append("\n");
            }
        }
        System.out.println(sb);
    }

    public static EndpointHandler findHandler(HttpRequest clientReq, Map<Url, EnumMap<HttpMethod, EndpointHandler>> endpointMap) {
        // Exact Match
        var map = endpointMap.get(clientReq.url());
        if (map != null) {
            return map.get(clientReq.method());
        }

        // Partial match
        Optional<Url> match = endpointMap.keySet()
                                         .stream()
                                         .parallel()
                                         .filter(url ->
                                                 clientReq.url()
                                                          .initial()
                                                          .equals(url.initial())
                                         )
                                         .findFirst();

        match.ifPresent(url -> clientReq.setPathVariable(stripBraces(url.last()), clientReq.url()
                                                                                           .last()));

        return match.map(url -> endpointMap.get(url)
                                           .get(clientReq.method()))
                    .orElse(null);
    }

    private static String stripBraces(String path) {
        String stripped = path.replace("{", "");
        stripped = stripped.replace("}", "");
        return stripped;
    }
}
