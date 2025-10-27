package com.example.greedy_gym;

import com.example.greedy_gym.config.LoginSuccessHandler;
import com.example.greedy_gym.servicios.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class Security {

    private final CustomUserDetailsService customUserDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;

    public Security(CustomUserDetailsService customUserDetailsService,
                    LoginSuccessHandler loginSuccessHandler) {
        this.customUserDetailsService = customUserDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    // Configuración de seguridad usando Spring Security para el login
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitamos CSRF para simplificar (puedes habilitarlo después)
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos
                .requestMatchers("/assets/**").permitAll()
                // Páginas públicas
                .requestMatchers("/", "/login", "/error").permitAll()
                // Admin direcciones público (según tu template)
                .requestMatchers("/admin/direcciones").permitAll()
                // Endpoints API públicos
                .requestMatchers("/api/**").permitAll()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            
            // Configurar formLogin para usar tu página de login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")  // Spring Security procesa POST /login
                .usernameParameter("nombreUsuario")  // Campo del formulario
                .passwordParameter("clave")  // Campo del formulario
                .successHandler(loginSuccessHandler)  // Handler personalizado
                .failureUrl("/login?error=true")
                .permitAll()
            )
            
            // Configurar logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    // Encoder para contraseñas - BCrypt
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationProvider que usa el UsuarioDetailsService
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager requerido para la autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}