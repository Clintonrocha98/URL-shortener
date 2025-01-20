package com.urlshorter.routes;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.urlshorter.controller.UrlController;
import com.urlshorter.factory.UrlControllerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

public class Router implements HttpHandler {

    private UrlController controller;

    public Router() {
        try {
            this.controller = new UrlControllerFactory().createController();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar o UrlController", e);
        }
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if (path.contains("/shorten-url") && "POST".equalsIgnoreCase(method)) {
            controller.saveUrl(exchange);
        } else if (path.matches("^/[a-zA-Z0-9]{1,10}$") && "GET".equalsIgnoreCase(method)) {
            String keyUrl = path.substring(1);
            controller.getUrl(exchange,keyUrl);
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
