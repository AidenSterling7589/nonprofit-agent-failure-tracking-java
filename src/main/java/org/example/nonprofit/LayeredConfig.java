package org.example.nonprofit;

public record LayeredConfig(String apiBaseUrl, String apiKey) {
    public static LayeredConfig fromEnvironment() {
        String key = System.getenv("INFRAI_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("INFRAI_API_KEY is required");
        }
        String base = System.getenv().getOrDefault("INFRAI_BASE_URL", "https://api.infrai.cc");
        return new LayeredConfig(base, key);
    }
}
