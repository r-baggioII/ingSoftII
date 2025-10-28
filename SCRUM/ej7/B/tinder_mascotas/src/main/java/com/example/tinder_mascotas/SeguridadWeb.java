package com.example.tinder_mascotas;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SeguridadWeb {


    // Encoder para contraseñas (útil si luego persistimos usuarios con password encriptado)
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Exponer el AuthenticationManager para el login programático (/auth/login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Configuración compatible con Spring Security 6 / Spring Boot 3
    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/vendor/**").permitAll()
                // Páginas de autenticación/registro públicas
                .requestMatchers("/", "/login", "/logincheck", "/registro", "/registrar", "/usuario/registrar", "/exito").permitAll()
                // Endpoints públicos para autenticación JWT y JWKS
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers("/.well-known/**", "/oauth2/jwks").permitAll()
                // Abrir algunas utilidades (opcional)
                .requestMatchers("/mail/**").permitAll()
                // El resto requiere autenticación
                .anyRequest().authenticated()
            )
            // Habilitar soporte de Resource Server con JWT (Bearer tokens)
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
            // Para APIs con JWT, trabajamos sin estado; para vistas, Spring manejará sesión si corresponde
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().newSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .formLogin(form -> form
                // Usa tu página de login existente
                .loginPage("/login")
                // Debe coincidir con el action del formulario en templates/login.html
                .loginProcessingUrl("/logincheck")
                // Deben coincidir con los name de los inputs del formulario
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/inicio", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .addLogoutHandler(new SecurityContextLogoutHandler())
                .logoutRequestMatcher(request -> 
                    request.getServletPath().equals("/logout") && 
                    (request.getMethod().equals("POST") || request.getMethod().equals("GET"))
                )
                .permitAll()
            )
            // Deshabilitar CSRF para endpoints de API típicos
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/auth/**", "/api/**")
            );

        return http.build();
    }
}


