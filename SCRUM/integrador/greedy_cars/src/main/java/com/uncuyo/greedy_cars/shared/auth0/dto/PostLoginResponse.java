package com.uncuyo.greedy_cars.shared.auth0.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response del endpoint /api/auth0/post-login
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginResponse {
    
    private String status; // USER_EXISTS, REQUIRED_MORE_INFO, USER_CREATED, ERROR
    private String email;
    private String externalId;
    private String provider;
    private Boolean emailVerified;
    private String token;
    private String nombre;
    private String apellido;
    private String message;
}
