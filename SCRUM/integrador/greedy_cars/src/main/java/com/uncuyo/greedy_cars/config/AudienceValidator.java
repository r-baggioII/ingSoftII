package com.uncuyo.greedy_cars.config;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Validador personalizado para verificar el audience en tokens JWT de Auth0.
 * Auth0 incluye el audience en el claim "aud" del token.
 */
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    
    private final String audience;
    
    public AudienceValidator(String audience) {
        this.audience = audience;
    }
    
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if (jwt.getAudience().contains(this.audience)) {
            return OAuth2TokenValidatorResult.success();
        }
        
        OAuth2Error error = new OAuth2Error(
            "invalid_token",
            "The required audience is missing",
            null
        );
        
        return OAuth2TokenValidatorResult.failure(error);
    }
}
