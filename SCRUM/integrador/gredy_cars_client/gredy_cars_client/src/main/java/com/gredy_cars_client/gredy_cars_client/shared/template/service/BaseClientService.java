package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.BaseApiDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.BaseDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Template service that performs validations around DAO usage and exposes hook
 * methods so concrete services can inject specific logic in each step.
 *
 * @param <T>  dto type
 * @param <ID> identifier type
 */
public abstract class BaseClientService<T extends BaseDTO<ID>, ID> {

    protected static final Logger log = LoggerFactory.getLogger(BaseClientService.class);

    protected final BaseApiDao<T, ID> dao;

    protected BaseClientService(BaseApiDao<T, ID> dao) {
        this.dao = dao;
    }

    protected void validar(BaseUseCaseService useCase, T dto) throws ErrorServiceException {}

    protected void preAlta(T dto) throws ErrorServiceException {}

    protected void postAlta(T dto) throws ErrorServiceException {}

    protected void preModificacion(ID id, T dto) throws ErrorServiceException {}

    protected void postModificacion(T dto) throws ErrorServiceException {}

    protected void preBaja(ID id) throws ErrorServiceException {}

    protected void postBaja(ID id) throws ErrorServiceException {}

    public List<T> listarActivos() throws ErrorServiceException {
        System.err.println("=== INICIO BaseClientService.listarActivos ===");
        try {
            System.err.println("Llamando a dao.findAll()...");
            List<T> result = dao.findAll();
            System.err.println("dao.findAll() retornó: " + (result != null ? result.size() : "null") + " elementos");
            if (result != null) {
                result.forEach(item -> System.err.println("  - Elemento: " + item));
            }
            System.err.println("=== FIN BaseClientService.listarActivos ===");
            return result;
        } catch (ErrorServiceException e) {
            System.err.println("ErrorServiceException en listarActivos: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Exception inesperada en listarActivos: " + e.getMessage());
            e.printStackTrace();
            log.error("Error inesperado al listar DTOs", e);
            throw new ErrorServiceException("Error de sistema al listar", e);
        }
    }

    public Optional<T> obtener(ID id) throws ErrorServiceException {
        try {
            return dao.findById(id);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al obtener DTO con id {}", id, e);
            throw new ErrorServiceException("Error de sistema al obtener", e);
        }
    }

    public T alta(T dto) throws ErrorServiceException {
        try {
            log.info("=== INICIO alta() - DTO: {}", dto);
            log.info("Validando DTO...");
            validar(BaseUseCaseService.ALTA, dto);
            log.info("Validación OK. Ejecutando preAlta()...");
            preAlta(dto);
            log.info("preAlta() OK. Llamando a dao.create()...");
            T creado = dao.create(dto);
            log.info("dao.create() OK. Resultado: {}", creado);
            log.info("Ejecutando postAlta()...");
            postAlta(creado);
            log.info("=== FIN alta() - Éxito");
            return creado;
        } catch (ErrorServiceException e) {
            log.error("ErrorServiceException en alta(): {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante el alta de {}", dto, e);
            throw new ErrorServiceException("Error de Sistema al dar de alta", e);
        }
    }

    public Optional<T> modificar(ID id, T dto) throws ErrorServiceException {
        try {
            dto.setId(id);
            validar(BaseUseCaseService.MODIFICACION, dto);
            preModificacion(id, dto);
            Optional<T> actualizado = dao.update(id, dto);
            if (actualizado.isPresent()) {
                postModificacion(actualizado.get());
            }
            return actualizado;
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante la modificación de id {}", id, e);
            throw new ErrorServiceException("Error de sistema al modificar el registro", e);
        }
    }

    public void baja(ID id) throws ErrorServiceException {
        try {
            preBaja(id);
            dao.delete(id);
            postBaja(id);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante la baja de id {}", id, e);
            throw new ErrorServiceException("Error de sistema al eliminar el registro", e);
        }
    }
}

