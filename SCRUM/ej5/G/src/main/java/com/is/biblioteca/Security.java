package com.is.biblioteca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class Security {

    // DaoAuthenticationProvider que conecta UserDetailsService con PasswordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    // AuthenticationManager para que Spring Security use el PasswordEncoder
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Configuración compatible con Spring Security 6 / Spring Boot 3
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos
                .requestMatchers(
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/img/**"),
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/h2-console/**")
                ).permitAll()
                // Páginas de autenticación/registro públicas
                .requestMatchers(
                    new AntPathRequestMatcher("/usuario/login"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/logincheck"),
                    new AntPathRequestMatcher("/usuario/registrar"),
                    new AntPathRequestMatcher("/usuario/registro")
                ).permitAll()
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
                // Redirigir a /usuario/inicio después del login exitoso
                .defaultSuccessUrl("/usuario/inicio", true)
                // En caso de error, volver a login con parámetro error
                .failureUrl("/usuario/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/usuario/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Deshabilitar CSRF para la consola H2
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
            )
            // Permitir frames para la consola H2
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}