package com.uncuyo.greedy_cars.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para la aplicación.
 * Por ahora, permite acceso sin autenticación a los endpoints de la API.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para pruebas con API REST
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/**").permitAll() // Permitir acceso a todos los endpoints /api/**
                .anyRequest().authenticated() // Requerir autenticación para el resto
            );
        
        return http.build();
    }
}
