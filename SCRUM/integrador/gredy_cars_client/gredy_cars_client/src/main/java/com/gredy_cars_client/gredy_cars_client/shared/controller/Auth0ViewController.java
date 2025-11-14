package com.gredy_cars_client.gredy_cars_client.shared.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller para servir las vistas relacionadas con Auth0
 */
@Controller
public class Auth0ViewController {
    
    @Value("${auth0.domain}")
    private String auth0Domain;
    
    @Value("${auth0.clientId}")
    private String auth0ClientId;
    
    @Value("${auth0.audience}")
    private String auth0Audience;
    
    @Value("${server.servlet.context-path:/}")
    private String contextPath;
    
    /**
     * Redirige a Auth0 Universal Login
     */
    @GetMapping("/auth0/login")
    public RedirectView auth0Login() throws UnsupportedEncodingException {
        // Construir la URL de callback
        String redirectUri = "http://161.153.217.110:18082" + contextPath + "/auth0/callback";
        
        // Construir la URL de autorización de Auth0
        String authUrl = String.format(
            "https://%s/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=openid profile email&audience=%s",
            auth0Domain,
            auth0ClientId,
            URLEncoder.encode(redirectUri, StandardCharsets.UTF_8.toString()),
            URLEncoder.encode(auth0Audience, StandardCharsets.UTF_8.toString())
        );
        
        return new RedirectView(authUrl);
    }
    
    /**
     * Página de login con Auth0 (login social)
     */
    @GetMapping("/login-auth0")
    public String loginAuth0() {
        return "login-auth0";
    }
    
    /**
     * Página de callback después de autenticación con Auth0
     */
    @GetMapping("/callback")
    public String callback() {
        return "callback";
    }
    
    /**
     * Página de registro intermedio para completar datos después de login social
     */
    @GetMapping("/auth0/registro-intermedio")
    public String registroIntermedio(HttpSession session, Model model) {
        // Verificar que haya datos de Auth0 en la sesión
        String email = (String) session.getAttribute("auth0_user_email");
        String externalId = (String) session.getAttribute("auth0_user_sub");
        String accessToken = (String) session.getAttribute("auth0_access_token");
        
        if (email == null || externalId == null || accessToken == null) {
            // No hay datos de Auth0, redirigir al login
            return "redirect:/login?error=no_auth0_session";
        }
        
        // Pasar los datos al modelo para que el formulario los use
        model.addAttribute("email", email);
        model.addAttribute("externalId", externalId);
        // NO pasar el token al modelo/vista por seguridad
        // El formulario lo enviará desde la sesión
        
        return "registro-intermedio-test";
    }
}
