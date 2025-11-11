package com.gredy_cars_client.gredy_cars_client.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

/**
 * Configuración de RestTemplate para realizar llamadas HTTP al backend.
 * Deshabilita el manejo automático de redirecciones para tener control total sobre las respuestas.
 */
@Configuration
public class RestTemplateConfig {

    @Bean(destroyMethod = "close")
    public CloseableHttpClient httpClient() {
        return HttpClients.custom()
            .disableRedirectHandling() // No seguir redirecciones automáticamente
            .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory httpRequestFactory(CloseableHttpClient httpClient) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(httpClient);
        return factory;
    }
}
