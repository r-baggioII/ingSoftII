package com.uncuyo.greedy_cars.shared.auth0.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el request de verificación de usuario Auth0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckUserRequest {
    private String auth0Sub;
    private String email;
}
