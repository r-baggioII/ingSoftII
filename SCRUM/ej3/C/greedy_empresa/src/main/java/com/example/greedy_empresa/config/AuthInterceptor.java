package com.example.greedy_empresa.config;

import com.example.greedy_empresa.controladores.AuthController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getRequestURI();

        if (isPublicPath(path)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(AuthController.SESSION_USER) != null) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }

    private boolean isPublicPath(String path) {
        if (path.equals("/login") || path.equals("/register") || path.equals("/logout")) {
            return true;
        }
        return path.startsWith("/dist/")
                || path.startsWith("/bootstrap/")
                || path.startsWith("/plugins/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/assets/")
                || path.startsWith("/favicon")
                || path.equals("/error");
    }
}
