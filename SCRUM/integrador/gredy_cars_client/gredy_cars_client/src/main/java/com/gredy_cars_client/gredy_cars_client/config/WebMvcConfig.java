package com.gredy_cars_client.gredy_cars_client.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC para registrar interceptores.
 * Configura el AuthCheckInterceptor para proteger rutas que requieren autenticación.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${greedy.api.base-url}")
    private String backendBase;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String verifyUrl = backendBase + "/auth/verificar";
        
        registry.addInterceptor(new AuthCheckInterceptor(restTemplate, verifyUrl))
                .addPathPatterns("/**") // Aplicar a todas las rutas
                .excludePathPatterns(
                    "/login",           // Página de login
                    "/logout",          // Logout
                    "/",                // Página principal (landing page pública)
                    "/registro",        // Registro de usuarios
                    "/registro-cliente.html",
                    "/about.html",      // Páginas informativas públicas
                    "/contact.html",
                    "/cars.html",
                    "/blog.html",
                    "/services.html",
                    "/pricing.html",
                    "/css/**",          // Recursos estáticos
                    "/js/**",
                    "/images/**",
                    "/fonts/**",
                    "/vendor/**",
                    "/style.css",
                    "/public/**",
                    "/webjars/**",
                    "/error",           // Página de error
                    "/favicon.ico"
                );
    }
}
