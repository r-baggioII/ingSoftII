package com.is.biblioteca;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.is.biblioteca.config.CustomAuthorizationRequestResolver;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class Security {

    @Autowired
    private CustomAuthorizationRequestResolver customAuthorizationRequestResolver;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain para OAuth2 y Auth0
     * Order(1) = mayor prioridad, se evalúa primero
     */
    @Bean
    @Order(1)
    public SecurityFilterChain auth0SecurityFilterChain(HttpSecurity http) throws Exception {
        http
            // Aplica a rutas de OAuth2 y rutas protegidas con Auth0
            .securityMatcher("/oauth2/**", "/login/oauth2/**", "/auth0/**")
            .authorizeHttpRequests(auth -> auth
                // Permitir el inicio del flujo OAuth2
                .requestMatchers("/oauth2/authorization/**").permitAll()
                .requestMatchers("/login/oauth2/code/**").permitAll()
                // El resto de rutas /auth0/** requieren autenticación
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                    .authorizationRequestResolver(customAuthorizationRequestResolver)
                )
                .defaultSuccessUrl("/usuario/inicio", true)
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {})  // Valida JWT tokens de Auth0
            )
            .logout(logout -> logout
                .logoutUrl("/auth0/logout")
                .logoutSuccessHandler((request, response, authentication) -> {
                    // Redirigir al endpoint de logout de Auth0 y luego al login
                    String returnTo = "http://localhost:8080/usuario/login";
                    String logoutUrl = String.format(
                        "https://dev-cxc35bm4ceatbg7p.us.auth0.com/v2/logout?client_id=WXANt7liImoKcJJWBH0iiwy41EvUfHJu&returnTo=%s",
                        java.net.URLEncoder.encode(returnTo, "UTF-8")
                    );
                    response.sendRedirect(logoutUrl);
                })
            );

        return http.build();
    }

    /**
     * SecurityFilterChain para tu sistema actual (form login)
     * Order(2) = menor prioridad, se evalúa después
     */
    @Bean
    @Order(2)
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**").permitAll()
                // Páginas de autenticación/registro públicas
                .requestMatchers("/usuario/login", "/login", "/logincheck", "/usuario/registrar", "/usuario/registro").permitAll()
                // Página de inicio pública (para redirigir desde Auth0)
                .requestMatchers("/").permitAll()
                // Debug (temporal, eliminar en producción)
                .requestMatchers("/debug/**").permitAll()
                // El resto requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/usuario/login")  // Usar /usuario/login como página de login
                .loginProcessingUrl("/logincheck")
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