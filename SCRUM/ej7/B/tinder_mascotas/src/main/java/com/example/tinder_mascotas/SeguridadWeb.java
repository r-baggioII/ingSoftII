package com.example.tinder_mascotas;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.core.annotation.Order;
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
                // El resto requiere autenticación
                .anyRequest().authenticated()
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
            .sessionManagement(session -> session
                .sessionFixation().newSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        return http.build();
    }
}


