package com.urlshorter;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.net.httpserver.HttpServer;
import com.urlshorter.routes.Router;

import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        Dotenv dotenv = Dotenv.load();

        int port = dotenv.get("PORTt") != null
                ? Integer.parseInt(dotenv.get("PORT"))
                : 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        Router router = new Router();

        server.createContext("/", router);

        server.setExecutor(null);

        server.start();

        logger.info("O servidor está rodando na porta {}", port); 
    }
}