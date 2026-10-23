package org.example.nonprofit;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class InfraiErrorClient {
    // The public capability used by this client is errors.capture.
    private final LayeredConfig config;
    private final HttpClient http;

    public InfraiErrorClient(LayeredConfig config) {
        this.config = config;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    }

    public void capture(String exceptionPayload) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.apiBaseUrl() + "/v1/errors/capture"))
                .header("Authorization", "Bearer " + config.apiKey())
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(exceptionPayload))
                .build();
        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            String body = response.body();
            if (!body.contains("\"ok\":true")) {
                throw new IllegalStateException("Infrai error capture rejected: " + errorText(body));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Infrai error capture interrupted", e);
        } catch (Exception e) {
            throw new IllegalStateException("Infrai error capture transport failed", e);
        }
    }

    private String errorText(String body) {
        int start = body.indexOf("\"error\"");
        return start >= 0 ? body.substring(start) : body;
    }
}
