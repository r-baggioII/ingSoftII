package org.sistemaMecanico;

import org.sistemaMecanico.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class Security {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Recursos públicos (CSS, JS, imágenes)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/webjars/**").permitAll()

                        // Páginas públicas
                        .requestMatchers("/", "/login", "/registro", "/error").permitAll()

                        // Rutas protegidas por rol
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/mecanico/**").hasAnyRole("ADMIN", "MECANICO")

                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                    // Tu página de login personalizada
                        .loginProcessingUrl("/login")           // URL donde se envía el formulario
                        .defaultSuccessUrl("/inicio", true)  // Redirige aquí después del login
                        .failureUrl("/login?error=true")        // Redirige aquí si falla
                        .usernameParameter("nombreUsuario")     // ✨ Nombre del campo en tu formulario
                        .passwordParameter("clave")             // ✨ Nombre del campo en tu formulario
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acceso-denegado")
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)                      // Máximo 1 sesión por usuario
                        .maxSessionsPreventsLogin(false)         // La nueva sesión cierra la anterior
                );

        return http.build();
    }

    /**
     * Bean para encriptar contraseñas con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Proveedor de autenticación que usa tu UserDetailsService
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Manager de autenticación
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}