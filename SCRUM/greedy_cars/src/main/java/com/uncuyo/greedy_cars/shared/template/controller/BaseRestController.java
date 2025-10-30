package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.entity.BaseEntity;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseController;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.BaseService;
import java.util.List;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

public abstract class BaseRestController<T extends BaseEntity<ID>, ID> {

    protected final BaseService<T, ID> service;
    private String nameClass = "";
    protected T entity;
    protected Class<T> entityClass;
    protected String nameEntityLower;

    protected BaseRestController(BaseService<T, ID> service) {
        this.service = service;
    }

    @SuppressWarnings("unchecked")
    protected void initController(T entity) {
        this.entity = entity;
        this.entityClass = (Class<T>) entity.getClass();
        this.nameClass = getNameEntity(this.entity);
        this.nameEntityLower = nameClass.toLowerCase();
    }

    private String getNameEntity(T object) {
        return ((((T) object).getClass()).getSimpleName());
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<T> listEntity = service.listarActivos();
            return ResponseEntity.ok(listEntity);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable ID id) {
        try {
            preUseCase(BaseUseCaseController.CONSULTAR);
            T entity = service.obtener(id)
                .orElseThrow(() -> new ErrorServiceException("No encontrado con ID: " + id));
            return ResponseEntity.ok(entity);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody T entity) {
        try {
            this.entity = entity;
            preUseCase(BaseUseCaseController.ALTA);
            
            T savedEntity = service.alta(entity);
            
            postUseCase(BaseUseCaseController.ALTA);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEntity);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable ID id, @RequestBody T entity) {
        try {
            this.entity = entity;
            preUseCase(BaseUseCaseController.MODIFICACION);
            
            T updatedEntity = service.modificar(id, entity)
                .orElseThrow(() -> new ErrorServiceException("No encontrado con ID: " + id));
            
            postUseCase(BaseUseCaseController.MODIFICACION);
            
            return ResponseEntity.ok(updatedEntity);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable ID id) {
        try {
            preUseCase(BaseUseCaseController.BAJA);
            
            service.baja(id);
            
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    protected ResponseEntity<?> buildErrorResponse(String mensaje, HttpStatus status) {
        Map<String, String> error = new HashMap<>();
        error.put("error", mensaje);
        return ResponseEntity.status(status).body(error);
    }

    protected ResponseEntity<?> buildSuccessResponse(String mensaje) {
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", mensaje);
        return ResponseEntity.ok(response);
    }

    // Métodos para ser redefinidos en los controladores que heredan, con el objetivo
    // de realizar acciones previas o posteriores en las Altas, Bajas y Modificaciones.
    protected void preUseCase(BaseUseCaseController useCase) throws ErrorServiceException {}
    protected void postUseCase(BaseUseCaseController useCase) throws ErrorServiceException {}
}