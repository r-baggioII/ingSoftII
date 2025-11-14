package com.gredy_cars_client.gredy_cars_client.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuración de Auth0 inyectada desde application.properties
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auth0Config {
    private String domain;
    private String clientId;
    private String clientSecret;
    private String audience;
    private String callbackUrl;
    private String logoutUrl;
}
