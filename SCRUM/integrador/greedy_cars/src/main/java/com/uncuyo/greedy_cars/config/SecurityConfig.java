package com.uncuyo.greedy_cars.config;

import com.uncuyo.greedy_cars.shared.template.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de seguridad para la aplicación.
 * Configura autenticación JWT tradicional y OAuth2 Resource Server para Auth0.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    
    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;
    
    @Value("${auth0.audience}")
    private String audience;

    /**
     * IMPORTANTE PARA IMPLEMENTAR AUTH0:
     *
     * - Las reglas de autorización definidas en authorizeHttpRequests(...) NO dependen
     *   del proveedor de autenticación.
     * - Mientras el Authentication tenga roles Spring (ROLE_JEFE, ROLE_ADMIN, ROLE_CLIENTE),
     *   estas reglas siguen funcionando tanto para:
     *      ✔ Nuestro JWT propio (JwtAuthenticationFilter)
     *      ✔ Tokens de Auth0 via oauth2ResourceServer().jwt()
     *
     * - Si en el futuro se elimina el JWT propio, SOLO se debe adaptar cómo se mapean
     *   los claims de Auth0 → GrantedAuthority, pero NO tocar estas reglas de acceso.
     *
     * - NO agregar .requestMatchers("/api/**").permitAll() al final porque rompe
     *   toda la seguridad actual.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                // Registro público
                .requestMatchers("/api/registro/**").permitAll()
                
                // Auth0 endpoints - permitAll porque validaremos el JWT manualmente en el controlador
                .requestMatchers("/api/auth0/**").permitAll()

                // Mercado Pago callbacks y creación de preferencias
                .requestMatchers(
                    "/api/pagos/mp/preferencia",
                    "/api/pagos/mp/success",
                    "/api/pagos/mp/failure",
                    "/api/pagos/mp/pending"
                ).permitAll()

                // Promociones: SOLO JEFE
                .requestMatchers("/api/promociones/**").hasRole("JEFE")

                // Facturación & Alquileres: deben estar autenticados
                .requestMatchers("/api/alquileres/**").authenticated()
                .requestMatchers("/api/facturas/**").authenticated()

                // Config correo (solo lo usa UI interna)
                .requestMatchers("/api/correos/**").authenticated()
                .requestMatchers("/api/configuracion-correo/**").authenticated()

                // Endpoint para que el cliente consulte sus promociones vigentes
                .requestMatchers("/api/promociones/vigentes/cliente/**").authenticated()

                // Recursos estáticos públicos
                .requestMatchers("/css/**","/js/**","/images/**","/img/**","/webjars/**").permitAll()

                // NO poner un .denyAll() global porque rompe módulos existentes.
                .anyRequest().permitAll()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sin estado, usando JWT
            )
            // Configurar OAuth2 Resource Server para validar tokens de Auth0
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    /**
     * Decoder JWT para validar tokens de Auth0
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);
        
        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
        
        jwtDecoder.setJwtValidator(withAudience);
        
        return jwtDecoder;
    }
    
    /**
     * Configuración de CORS para permitir peticiones desde el cliente.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://161.153.217.110:18082"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * Bean para encriptar contraseñas con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Proveedor de autenticación que usa el UserDetailsService personalizado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
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
