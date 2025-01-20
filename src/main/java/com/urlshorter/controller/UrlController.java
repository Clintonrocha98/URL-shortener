package com.urlshorter.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.urlshorter.exceptions.UrlException;
import com.urlshorter.model.UrlModel;
import com.urlshorter.service.UrlService;

public class UrlController {

    private UrlService service;
    private static final Logger logger = LoggerFactory.getLogger(UrlController.class);

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

            Response<String> response = new Response<>("success", "http://" + host + "/" + key);
            byte[] responseBytes = objectMapper.writeValueAsBytes(response);

            exchange.sendResponseHeaders(201, responseBytes.length);
            outputStream.write(responseBytes);

        } catch (UrlException e) {
            logger.error("Erro: {}", e.getMessage());
            sendErrorResponse(exchange, e.getStatusCode(), e.getMessage());
        } catch (IOException e) {
            logger.error("Erro: {}", e.getMessage());
            sendErrorResponse(exchange, 500, "Erro interno de entrada/saída.");
        } catch (SQLException e) {
            logger.error("Erro: {}", e.getMessage());
            sendErrorResponse(exchange, 500, "Erro interno do servidor");
        } catch (Exception e) {
            logger.error("Erro: {}", e.getMessage());
            sendErrorResponse(exchange, 500, "Erro interno do servidor");
        } finally {
            exchange.close();
        }
    }

    public void getUrl(HttpExchange exchange, String keyUrl) throws IOException {
        logger.info("Solicitação recebida para resgatar URL");
        try {
            UrlModel url = this.service.getUrl(keyUrl);

            exchange.getResponseHeaders().add("Location", "http://" + url.getOriginalUrl());

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
        } catch (SQLException e) {
            sendErrorResponse(exchange, 500, "Erro interno do servidor");
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

    private String extractUrlFromJson(String json, ObjectMapper objectMapper) throws IOException, UrlException {
        JsonNode rootNode = objectMapper.readTree(json);

        if (!rootNode.isObject() || !rootNode.has("url")) {
            throw new UrlException("O JSON enviado não é válido ou está vazio", 400);
        }

        JsonNode urlNode = rootNode.get("url");

        if (urlNode.isNull() || !urlNode.isTextual()) {
            throw new UrlException("O campo 'url' é obrigatório e deve ser uma string.", 400);
        }

        String url = urlNode.asText();
        return validateAndFormatUrl(url);
    }

    private String validateAndFormatUrl(String url) throws UrlException {
        if (!url.matches("^(http|https)://.*$")) {
            url = "http://" + url;
        }

        try {
            new URL(url).toURI();
        } catch (Exception e) {
            throw new UrlException("Url invalida", 400);
        }

        return url;
    }

}
