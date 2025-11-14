package com.uncuyo.greedy_cars.shared.auth0.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de verificación de usuario Auth0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserResponse {
    private Boolean exists;
    private String userId;
    private String message;
}
