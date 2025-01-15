package com.urlshorter;

import com.sun.net.httpserver.HttpServer;
import com.urlshorter.routes.Router;

import java.io.IOException;

import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args)throws IOException {
       HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        Router router = new Router();
            
        server.createContext("/", router);

        server.setExecutor(null);

        server.start();

        System.out.println("server is running on port 8000");
    }
}