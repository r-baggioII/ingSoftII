package com.gredy_cars_client.gredy_cars_client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security.
 * Deshabilitamos la seguridad por defecto ya que usamos autenticación personalizada
 * mediante el AuthCheckInterceptor que verifica JWT con el backend.
 * 
 * Para Auth0: El cliente NO valida tokens JWT. Solo el backend lo hace.
 * El cliente obtiene el access_token de Auth0 y lo envía al backend.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos
                .requestMatchers(
                    "/",
                    "/login",
                    "/login-auth0",
                    "/callback",
                    "/auth0/**",
                    "/registro",
                    "/registro-intermedio",
                    "/api/registro/**",
                    "/api/auth0/**",
                    "/api/public/**",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/style.css",
                    "/error"
                ).permitAll()
                // Todo lo demás permitido (la seguridad se maneja en AuthCheckInterceptor)
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())
            .logout(logout -> logout.disable());
        
        return http.build();
    }
}
