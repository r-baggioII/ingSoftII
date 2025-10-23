package com.example.greedy_gym;

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

import com.example.greedy_gym.config.LoginSuccessHandler;
import com.example.greedy_gym.config.UsuarioDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SeguridadWeb {

    private final UsuarioDetailsService usuarioDetailsService;
    private final LoginSuccessHandler loginSuccessHandler;

    public SeguridadWeb(UsuarioDetailsService usuarioDetailsService,
                        LoginSuccessHandler loginSuccessHandler) {
        this.usuarioDetailsService = usuarioDetailsService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    // Encoder para contraseñas - BCrypt para uso futuro
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // PasswordEncoder actual - NoOp porque las contraseñas están en texto plano
    // Cuando migres a BCrypt, cambia este bean a: return new BCryptPasswordEncoder()
    @Bean
    public PasswordEncoder actualPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    // AuthenticationProvider que usa el UsuarioDetailsService
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(usuarioDetailsService);
        provider.setPasswordEncoder(actualPasswordEncoder());
        return provider;
    }

    // AuthenticationManager requerido para la autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
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
}