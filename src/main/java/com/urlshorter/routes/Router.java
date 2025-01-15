package com.urlshorter.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.urlshorter.controller.UrlController;
import com.urlshorter.factory.UrlFactory;

import java.io.IOException;
import java.io.OutputStream;

public class Router implements HttpHandler {

    private UrlController controller = UrlFactory.createController();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (path.equals("/shorten-url") && "POST".equalsIgnoreCase(method)) {
            controller.saveUrl(exchange);
        } else if (path.contains("/get-url/") && "GET".equalsIgnoreCase(method)) {
            controller.getUrl(exchange);
        } else {
            String response = "Rota não encontrada!";

            byte[] responseBytes = response.getBytes();

            exchange.sendResponseHeaders(404, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }

    }

}
