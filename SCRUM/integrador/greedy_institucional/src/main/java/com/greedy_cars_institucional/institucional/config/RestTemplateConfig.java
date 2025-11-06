package com.greedy_cars_institucional.institucional.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * Mirrors the HTTP stack defined in the existing Greedy Cars client to ensure
 * the same behaviour regarding redirects and connection handling.
 */
@Configuration
public class RestTemplateConfig {

    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {
        return HttpClients.custom()
            .disableRedirectHandling()
            .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        return factory;
    }
}
