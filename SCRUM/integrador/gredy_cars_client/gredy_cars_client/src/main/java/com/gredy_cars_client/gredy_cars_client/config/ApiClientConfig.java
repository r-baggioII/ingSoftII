package com.gredy_cars_client.gredy_cars_client.config;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

/**
 * Central configuration for REST client infrastructure required by the DAO
 * template.
 */
@Configuration
@EnableConfigurationProperties(GreedyApiProperties.class)
public class ApiClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, GreedyApiProperties properties) {
        return builder
            .defaultHeader(HttpHeaders.AUTHORIZATION, createBasicAuthHeader(properties))
            .build();
    }

    private String createBasicAuthHeader(GreedyApiProperties properties) {
        String auth = properties.getUsername() + ":" + properties.getPassword();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);
    }
}

