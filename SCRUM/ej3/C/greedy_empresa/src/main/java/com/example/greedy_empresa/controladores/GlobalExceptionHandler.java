package com.example.greedy_empresa.controladores;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception ex, HttpServletRequest request, Model model) {
        log.error("Error interno en la aplicación", ex);
        
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorType", ex.getClass().getSimpleName());
        model.addAttribute("requestUrl", request.getRequestURL().toString());
        model.addAttribute("stackTrace", getStackTraceAsString(ex));
        
        return "error/general";
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request, Model model) {
        log.error("Argumento inválido", ex);
        
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorType", "Datos inválidos");
        model.addAttribute("requestUrl", request.getRequestURL().toString());
        
        return "error/general";
    }
    
    private String getStackTraceAsString(Exception ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
            if (sb.length() > 2000) { // Limitar el tamaño
                sb.append("...");
                break;
            }
        }
        return sb.toString();
    }
}