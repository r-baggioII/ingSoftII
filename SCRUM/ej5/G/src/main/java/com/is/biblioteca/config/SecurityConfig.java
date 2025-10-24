package com.is.biblioteca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.is.biblioteca.business.logic.service.UsuarioService;

/**
 * Configuración de Spring Security - Elementos básicos para autenticación y autorización
 * 
 * COMPONENTES BÁSICOS DE SPRING SECURITY:
 * 
 * 1. @EnableWebSecurity: Habilita la configuración de seguridad web de Spring
 * 2. SecurityFilterChain: Define las reglas de autorización y configuración de login
 * 3. DaoAuthenticationProvider: Conecta el servicio de usuarios con el codificador de contraseñas
 * 4. AuthenticationManager: Gestiona el proceso de autenticación
 * 5. PasswordEncoder: Codifica y verifica contraseñas (BCrypt en este caso)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * COMPONENTE BÁSICO 1: DaoAuthenticationProvider
     * Conecta el servicio que carga usuarios (UserDetailsService) con el codificador de contraseñas
     * Esto permite a Spring Security autenticar usuarios comparando contraseñas encriptadas
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioService); // Servicio que implementa UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder);    // BCryptPasswordEncoder para verificar contraseñas
        return authProvider;
    }
    
    /**
     * COMPONENTE BÁSICO 2: AuthenticationManager
     * Gestiona el proceso de autenticación delegando a los AuthenticationProviders configurados
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * COMPONENTE BÁSICO 3: SecurityFilterChain
     * Configura las reglas de seguridad HTTP, incluyendo:
     * - Qué URLs son públicas y cuáles requieren autenticación
     * - Configuración del formulario de login
     * - Configuración del logout
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CONFIGURACIÓN DE AUTORIZACIÓN: Define qué URLs son públicas y cuáles requieren login
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos (CSS, JS, imágenes) son públicos
                .requestMatchers(
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/img/**"),
                    new AntPathRequestMatcher("/")
                ).permitAll()
                
                // Páginas de autenticación son públicas
                .requestMatchers(
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/registro"),
                    new AntPathRequestMatcher("/registrar")
                ).permitAll()
                
                // Consola H2 (solo para desarrollo)
                .requestMatchers(
                    new AntPathRequestMatcher("/h2-console/**")
                ).permitAll()
                
                // Cualquier otra petición requiere estar autenticado
                .anyRequest().authenticated()
            )
            
            // CONFIGURACIÓN DEL LOGIN: Define cómo funciona el formulario de login
            .formLogin(form -> form
                .loginPage("/login")                      // URL de la página de login personalizada
                .loginProcessingUrl("/logincheck")        // URL que procesa el formulario (POST)
                .usernameParameter("email")               // Nombre del campo email en el formulario
                .passwordParameter("password")            // Nombre del campo password en el formulario
                .defaultSuccessUrl("/inicio", true)       // Redirección después de login exitoso
                .failureUrl("/login?error=true")          // Redirección si el login falla
                .permitAll()                              // Permitir acceso a todos a la página de login
            )
            
            // CONFIGURACIÓN DEL LOGOUT: Define cómo funciona el cierre de sesión
            .logout(logout -> logout
                .logoutUrl("/logout")                     // URL para cerrar sesión
                .logoutSuccessUrl("/login?logout=true")   // Redirección después del logout
                .invalidateHttpSession(true)              // Invalidar la sesión HTTP
                .deleteCookies("JSESSIONID")              // Eliminar la cookie de sesión
                .permitAll()
            )
            
            // CONFIGURACIÓN CSRF: Protección contra ataques Cross-Site Request Forgery
            .csrf(csrf -> csrf
                .ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**"))
            )
            
            // CONFIGURACIÓN DE HEADERS: Permitir frames para H2 Console
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );

        return http.build();
    }
}
