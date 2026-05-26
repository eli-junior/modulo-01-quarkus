package com.eli;

import com.sun.net.httpserver.HttpServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class StarWarsTestResource implements QuarkusTestResourceLifecycleManager {

    private HttpServer server;

    @Override
    public Map<String, String> start() {
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
            server.createContext("/starships", exchange -> {
                byte[] response = "[{\"name\":\"Millennium Falcon\"}]".getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                try (OutputStream responseBody = exchange.getResponseBody()) {
                    responseBody.write(response);
                }
            });
            server.start();

            String baseUrl = "http://localhost:" + server.getAddress().getPort();
            return Map.of("quarkus.rest-client.\"com.eli.StarWarsService\".url", baseUrl);
        } catch (IOException e) {
            throw new IllegalStateException("Could not start Star Wars test server", e);
        }
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
