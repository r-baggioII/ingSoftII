package com.is.biblioteca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class Security {

    // Encoder para contraseñas (útil si luego persistimos usuarios con password encriptado)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración compatible con Spring Security 6 / Spring Boot 3
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**").permitAll()
                // Páginas de autenticación/registro públicas
                .requestMatchers("/usuario/login", "/login", "/logincheck", "/usuario/registrar", "/usuario/registro").permitAll()
                // El resto requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                // Usa tu página de login existente
                .loginPage("/usuario/login")
                // Debe coincidir con el action del formulario en templates/login.html
                .loginProcessingUrl("/logincheck")
                // Deben coincidir con los name de los inputs del formulario
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/usuario/inicio", true)
                .failureUrl("/usuario/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/usuario/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}