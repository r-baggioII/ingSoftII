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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
}

