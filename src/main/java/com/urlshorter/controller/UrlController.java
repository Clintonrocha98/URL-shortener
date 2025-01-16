package com.urlshorter.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.HttpExchange;
import com.urlshorter.exceptions.UrlException;
import com.urlshorter.service.UrlService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlController {

    private UrlService service;
    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);
    private static final String pathUrlGet = "/get-url/";

    public UrlController(UrlService service) {
        this.service = service;
    }

    public void saveUrl(HttpExchange exchange) throws IOException {
        logger.info("Solicitação recebida para salvar URL");

        ObjectMapper objectMapper = new ObjectMapper();

        String host = exchange.getRequestHeaders().getFirst("Host");

        exchange.getResponseHeaders().set("Content-Type", "application/json");

        try (
                InputStream inputStream = exchange.getRequestBody();
                OutputStream outputStream = exchange.getResponseBody();) {

            String requestBody = new String(inputStream.readAllBytes());

            String url = extractUrlFromJson(requestBody, objectMapper);
            String key = this.service.saveUrl(url);

            Response<String> response = new Response<>(host + pathUrlGet + key);

            byte[] responseBytes = objectMapper.writeValueAsBytes(response);

            exchange.sendResponseHeaders(200, responseBytes.length);

            outputStream.write(responseBytes);

        } catch (UrlException e) {
            sendErrorResponse(exchange, e.getStatusCode(), e.getMessage());
        } catch (IOException e) {
            logger.error("Erro de I/O ao processar a solicitação: {}", e.getMessage());
            sendErrorResponse(exchange, 500, "Erro interno de entrada/saída.");
        } catch (Exception e) {
            logger.error("Erro inesperado: {}", e.getMessage());
            sendErrorResponse(exchange, 400, e.getMessage());
        }
    }

    public void getUrl(HttpExchange exchange) throws IOException {
        logger.info("Solicitação recebida para resgatar URL");
        try {
            String key = exchange.getRequestURI().getPath().substring("/get-url/".length());
            String url = this.service.getUrl(key);

            exchange.getResponseHeaders().add("Location", "http://" + url);

            exchange.sendResponseHeaders(302, -1);
        } catch (UrlException e) {
            Response<Void> response = new Response<>(e.getMessage());
            ObjectMapper objectMapper = new ObjectMapper();

            byte[] responseBytes = objectMapper.writeValueAsBytes(response);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(e.getStatusCode(), responseBytes.length);

            try (OutputStream outputStream = exchange.getResponseBody()) {
                outputStream.write(responseBytes);
            }
        } finally {
            exchange.close();
        }
    }

    private void sendErrorResponse(HttpExchange exchange, int statusCode, String message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Response<Void> response = new Response<>(message);
        byte[] errorBytes = objectMapper.writeValueAsBytes(response);

        exchange.sendResponseHeaders(statusCode, errorBytes.length);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(errorBytes);
        }
    }

    private String extractUrlFromJson(String json, ObjectMapper objectMapper) throws IOException {
        JsonNode rootNode = objectMapper.readTree(json);

        if (rootNode.isMissingNode() || !rootNode.isObject()) {
            throw new IllegalArgumentException("O JSON enviado não é válido ou está vazio.");
        }

        ObjectNode objectNode = (ObjectNode) rootNode;

        JsonNode urlNode = objectNode.get("url");

        if (urlNode == null || urlNode.isNull() || !urlNode.isTextual()) {
            throw new IllegalArgumentException("O campo 'url' é obrigatório e deve ser uma string.");
        }

        return urlNode.asText();
    }

}
