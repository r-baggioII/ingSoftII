package com.is.biblioteca.config;

import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final DefaultOAuth2AuthorizationRequestResolver defaultResolver;

    public CustomAuthorizationRequestResolver(ClientRegistrationRepository clientRegistrationRepository) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository, "/oauth2/authorization");
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request);
        if (authorizationRequest != null) {
            System.out.println("=== CustomAuthorizationRequestResolver.resolve(request) ===");
            System.out.println("Original authorizationRequest: " + authorizationRequest.getAuthorizationUri());
            OAuth2AuthorizationRequest customized = customizeAuthorizationRequest(authorizationRequest);
            System.out.println("Customized authorizationRequest: " + customized.getAuthorizationUri());
            return customized;
        }
        return null;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        OAuth2AuthorizationRequest authorizationRequest = defaultResolver.resolve(request, clientRegistrationId);
        if (authorizationRequest != null) {
            System.out.println("=== CustomAuthorizationRequestResolver.resolve(request, clientRegistrationId) ===");
            System.out.println("Client Registration ID: " + clientRegistrationId);
            System.out.println("Original authorizationRequest: " + authorizationRequest.getAuthorizationUri());
            OAuth2AuthorizationRequest customized = customizeAuthorizationRequest(authorizationRequest);
            System.out.println("Customized authorizationRequest: " + customized.getAuthorizationUri());
            return customized;
        }
        return null;
    }

    private OAuth2AuthorizationRequest customizeAuthorizationRequest(
            OAuth2AuthorizationRequest authorizationRequest) {
        // Agregar el parámetro prompt=select_account para forzar la selección de cuenta
        System.out.println("=== Customizing authorization request ===");
        System.out.println("Adding prompt=select_account parameter");
        
        OAuth2AuthorizationRequest customized = OAuth2AuthorizationRequest.from(authorizationRequest)
                .additionalParameters(params -> {
                    params.put("prompt", "select_account");
                    System.out.println("Additional parameters: " + params);
                })
                .build();
        
        return customized;
    }
}
