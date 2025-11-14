package com.gredy_cars_client.gredy_cars_client.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response del endpoint /api/auth0/post-login
 * Indica si el usuario debe completar registro o puede acceder directamente
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLoginResponse {
    
    /**
     * Estado del usuario:
     * - USER_EXISTS: Usuario ya existe, puede acceder
     * - REQUIRED_MORE_INFO: Usuario debe completar registro intermedio
     */
    private String status;
    
    /**
     * Mensaje descriptivo
     */
    private String message;
    
    /**
     * URL a la que debe redirigir el frontend
     */
    private String redirectTo;
    
    /**
     * Token temporal para completar registro (solo si status = REQUIRED_MORE_INFO)
     */
    private String tempToken;
    
    /**
     * Info del usuario (opcional)
     */
    private UserInfo userInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private String email;
        private String name;
        private String auth0Sub;
    }
}
