package com.greedy_cars_institucional.institucional.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalised configuration mirroring the settings used by the Greedy Cars
 * client applications. Keeps consistency across clients regarding base URL,
 * credentials and helper utilities.
 */
@ConfigurationProperties(prefix = "greedy.api")
public class GreedyApiProperties {

    /**
     * Base URL for the Greedy Cars REST API (e.g. http://localhost:9000/greedy_cars/api)
     */
    private String baseUrl = "http://localhost:9000/api";

    /**
     * Optional username when the API is protected with HTTP Basic auth.
     */
    private String username = "admin";

    /**
     * Optional password when the API is protected with HTTP Basic auth.
     */
    private String password = "GreedyAdmin123!";

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Helper that ensures we always return a valid API URL for arbitrary paths.
     */
    public String buildUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return baseUrl;
        }
        if (relativePath.startsWith("/")) {
            return baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1) + relativePath
                : baseUrl + relativePath;
        }
        return baseUrl.endsWith("/")
            ? baseUrl + relativePath
            : baseUrl + "/" + relativePath;
    }

    public String buildImageContentUrl(String imageId) {
        if (Objects.isNull(imageId) || imageId.isBlank()) {
            return null;
        }
        return buildUrl("/imagenes/" + imageId + "/contenido");
    }
}
