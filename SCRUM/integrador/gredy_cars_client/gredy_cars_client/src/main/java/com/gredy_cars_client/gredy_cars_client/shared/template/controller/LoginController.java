package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Controlador MVC para autenticación.
 * Actúa como proxy entre el navegador y el backend, gestionando cookies HttpOnly.
 */
@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${greedy.api.base-url}")
    private String backendBase;

    /**
     * Muestra el formulario de login
     */
    @GetMapping("/login")
    public String loginForm(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Credenciales inválidas. Por favor, intenta de nuevo.");
        }
        return "login";
    }

    /**
     * Procesa el login enviando credenciales al backend y copiando la cookie JWT
     */
    @PostMapping("/login")
    public String doLogin(
            @RequestParam String nombreUsuario,
            @RequestParam String clave,
            HttpServletResponse servletResponse,
            RedirectAttributes ra
    ) {
        try {
            String url = backendBase + "/auth/login";
            log.info("Intentando login para usuario: {} en URL: {}", nombreUsuario, url);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("nombreUsuario", nombreUsuario, "clave", clave);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            // Copiar cookies Set-Cookie del backend al response del cliente
            List<String> setCookies = resp.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookies != null && !setCookies.isEmpty()) {
                log.info("Copiando {} cookies del backend al cliente", setCookies.size());
                for (String cookie : setCookies) {
                    servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie);
                }
            }

            if (resp.getStatusCode().is2xxSuccessful()) {
                log.info("Login exitoso para usuario: {}", nombreUsuario);
                // Opcional: guardar info mínima en sesión para UI
                Map bodyMap = resp.getBody();
                if (bodyMap != null && bodyMap.containsKey("usuario")) {
                    // Aquí podrías guardar datos del usuario en sesión si lo necesitas
                }
                // Redirigir al dashboard de gestión
                return "redirect:/dashboard";
            } else {
                log.warn("Login fallido para usuario: {} - Status: {}", nombreUsuario, resp.getStatusCode());
                ra.addFlashAttribute("error", "Credenciales inválidas");
                return "redirect:/login?error";
            }
        } catch (Exception e) {
            log.error("Error al intentar login para usuario: {}", nombreUsuario, e);
            ra.addFlashAttribute("error", "Error al conectar con el servidor: " + e.getMessage());
            return "redirect:/login?error";
        }
    }

    /**
     * Procesa el logout llamando al backend y eliminando la cookie
     */
    @PostMapping("/logout")
    public String doLogout(HttpServletRequest request, HttpServletResponse servletResponse) {
        try {
            String url = backendBase + "/auth/logout";
            log.info("Procesando logout en URL: {}", url);
            
            HttpHeaders headers = new HttpHeaders();
            // Reenviar cookies del navegador al backend
            String cookieHeader = request.getHeader("Cookie");
            if (cookieHeader != null) {
                headers.add(HttpHeaders.COOKIE, cookieHeader);
            }

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> resp = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            // Copiar Set-Cookie del backend (debería venir con Max-Age=0 para borrar)
            List<String> setCookies = resp.getHeaders().get(HttpHeaders.SET_COOKIE);
            if (setCookies != null && !setCookies.isEmpty()) {
                for (String cookie : setCookies) {
                    servletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie);
                }
            } else {
                // Si el backend no envió Set-Cookie, forzamos borrado de la cookie jwt
                servletResponse.addHeader(HttpHeaders.SET_COOKIE, "jwt=; Path=/; Max-Age=0; HttpOnly");
            }

            log.info("Logout exitoso");
            return "redirect:/login?logout";
        } catch (Exception e) {
            log.error("Error durante logout", e);
            // Intentar borrar la cookie de todas formas
            servletResponse.addHeader(HttpHeaders.SET_COOKIE, "jwt=; Path=/; Max-Age=0; HttpOnly");
            return "redirect:/login?logout";
        }
    }

    /**
     * Método alternativo GET para logout (para enlaces)
     */
    @GetMapping("/logout")
    public String doLogoutGet(HttpServletRequest request, HttpServletResponse servletResponse) {
        return doLogout(request, servletResponse);
    }
}
