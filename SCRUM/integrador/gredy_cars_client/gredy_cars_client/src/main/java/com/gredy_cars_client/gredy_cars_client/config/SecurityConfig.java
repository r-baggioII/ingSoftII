package com.gredy_cars_client.gredy_cars_client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad para el cliente.
 * Protege las vistas de gestión bajo autenticación, mantiene acceso
 * público a recursos estáticos y páginas públicas, y deja CSRF habilitado
 * para formularios (Thymeleaf incluye el token como hidden input).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", "/webjars/**",
                    "/", "/index", "/favicon.ico", "/login"
                ).permitAll()
                .requestMatchers(
                    "/gestion/**", "/direcciones/**", 
                    "/paises/**", "/provincias/**", 
                    "/departamentos/**", "/localidades/**"
                ).authenticated()
                .anyRequest().permitAll()
            )
            .formLogin(form -> form.permitAll())
            .logout(logout -> logout.permitAll())
            .httpBasic(basic -> basic.disable());
        
        return http.build();
    }
}
