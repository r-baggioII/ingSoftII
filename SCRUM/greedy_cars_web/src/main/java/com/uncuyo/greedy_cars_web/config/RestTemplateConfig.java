package com.uncuyo.greedy_cars_web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuración para RestTemplate
 * Bean que se usará para hacer peticiones HTTP al backend API
 */
@Configuration
public class RestTemplateConfig {

    @Value("${api.connection.timeout:5000}")
    private int connectionTimeout;

    @Value("${api.read.timeout:5000}")
    private int readTimeout;

    /**
     * Bean de RestTemplate configurado con timeouts
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofMillis(connectionTimeout))
                .setReadTimeout(Duration.ofMillis(readTimeout))
                .build();
    }

    /**
     * Alternativa: Factory personalizado para configuración más detallada
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectionTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
