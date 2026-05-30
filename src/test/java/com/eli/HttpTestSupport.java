package com.eli;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class HttpTestSupport {

    private static final Pattern UP_STATUS = Pattern.compile("\"status\"\\s*:\\s*\"UP\"");

    private final HttpClient httpClient = HttpClient.newHttpClient();

    protected HttpResponse<String> get(URI uri) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder(uri).GET());
    }

    protected HttpResponse<String> get(URI uri, String bearerToken) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder(uri)
                .header("Authorization", "Bearer " + bearerToken)
                .GET());
    }

    protected HttpResponse<String> post(URI uri, String body, String contentType) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder(uri)
                .header("Content-Type", contentType)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)));
    }

    protected HttpResponse<String> put(URI uri, String body, String contentType) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder(uri)
                .header("Content-Type", contentType)
                .PUT(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)));
    }

    protected HttpResponse<String> delete(URI uri, String body, String contentType) throws IOException, InterruptedException {
        return send(HttpRequest.newBuilder(uri)
                .header("Content-Type", contentType)
                .method("DELETE", HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8)));
    }

    private HttpResponse<String> send(HttpRequest.Builder builder) throws IOException, InterruptedException {
        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    protected void assertUpStatus(String body) {
        assertTrue(UP_STATUS.matcher(body).find(), "Response body should contain status UP: " + body);
    }
}
