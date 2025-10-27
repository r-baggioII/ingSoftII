package org.sistemaMecanico;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Security {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll() // Permitir todas las peticiones sin autenticación
            )
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para pruebas
            .formLogin(form -> form.disable()) // Deshabilitar el formulario de login
            .httpBasic(basic -> basic.disable()); // Deshabilitar autenticación HTTP Basic
        
        return http.build();
    }
}
