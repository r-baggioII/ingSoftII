package com.ejemplo.biblioteca.web.error;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final URI NOT_FOUND_TYPE = URI.create("https://api.biblioteca/errors/not-found");
    private static final URI BAD_REQUEST_TYPE = URI.create("https://api.biblioteca/errors/bad-request");

    @ExceptionHandler(EntityNotFoundException.class)
    public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        detail.setTitle("Recurso no encontrado");
        detail.setDetail(ex.getMessage());
        detail.setType(NOT_FOUND_TYPE);
        return detail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Petición inválida");
        detail.setDetail(ex.getMessage());
        detail.setType(BAD_REQUEST_TYPE);
        return detail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setTitle("Error de validación");
        detail.setType(BAD_REQUEST_TYPE);
        detail.setDetail("Existen campos inválidos");

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        if (!errors.isEmpty()) {
            detail.setProperty("errors", errors);
        }
        return detail;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail handleResponseStatus(ResponseStatusException ex) {
        ProblemDetail detail = ProblemDetail.forStatus(ex.getStatusCode());
        String reason = ex.getReason() != null ? ex.getReason() : "Error de aplicación";
        detail.setTitle(reason);
        detail.setDetail(reason);
        detail.setType(URI.create("https://api.biblioteca/errors/" + ex.getStatusCode().value()));
        return detail;
    }
}
