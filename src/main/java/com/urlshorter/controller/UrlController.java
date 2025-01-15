package com.urlshorter.controller;

import com.sun.net.httpserver.HttpExchange;
import com.urlshorter.exceptions.UrlException;
import com.urlshorter.service.UrlService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UrlController {

    private UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    public void saveUrl(HttpExchange exchange) throws IOException {

        String jsonResponse = "";
        String host = exchange.getRequestHeaders().getFirst("Host");

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        try (InputStream inputStream = exchange.getRequestBody(); OutputStream outputStream = exchange.getResponseBody();) {

            String requestBody = new String(inputStream.readAllBytes());
            String url = extractUrl(requestBody);

            try {
                String key = this.service.saveUrl(url);
                jsonResponse = "{ \"data\": \"" + host + "/get-url/" + key + "\" }";
                exchange.sendResponseHeaders(200, jsonResponse.length());
            } catch (UrlException e) {
                jsonResponse = "{ \"message\":" + e.getMessage() + " }";
                exchange.sendResponseHeaders(e.getStatusCode(), jsonResponse.length());
            }

            outputStream.write(jsonResponse.getBytes());
        }

    }

    public void getUrl(HttpExchange exchange) throws IOException {
        String jsonResponse = "";

        try {
            String key = exchange.getRequestURI().getPath().substring("/get-url/".length());
            String url = this.service.getUrl(key);

            exchange.getResponseHeaders().add("Location", "http://" + url);

            exchange.sendResponseHeaders(302, -1);
        } catch (UrlException e) {
            jsonResponse = "{ \"message\": \"" + e.getMessage() + "\" }";
            byte[] responseBytes = jsonResponse.getBytes("UTF-8");
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(e.getStatusCode(), responseBytes.length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(responseBytes);
            }
        } finally {
            exchange.close();
        }
    }

    private static String extractUrl(String json) {
        int start = json.indexOf("\"url\": \"") + 8;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
