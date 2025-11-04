package com.gredy_cars_client.gredy_cars_client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Externalised configuration for the Greedy Cars server endpoints.
 */
@ConfigurationProperties(prefix = "greedy.api")
public class GreedyApiProperties {

    /**
     * Base URL for the Greedy server. Should include protocol and context path,
     * for example: http://localhost:8080/api
     */
    private String baseUrl = "http://localhost:8080/api";

    /**
     * Username for Basic Authentication
     */
    private String username = "admin";

    /**
     * Password for Basic Authentication
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
}
