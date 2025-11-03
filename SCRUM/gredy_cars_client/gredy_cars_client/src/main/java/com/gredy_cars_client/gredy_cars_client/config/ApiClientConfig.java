package com.gredy_cars_client.gredy_cars_client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Central configuration for REST client infrastructure required by the DAO
 * template.
 */
@Configuration
@EnableConfigurationProperties(GreedyApiProperties.class)
public class ApiClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

