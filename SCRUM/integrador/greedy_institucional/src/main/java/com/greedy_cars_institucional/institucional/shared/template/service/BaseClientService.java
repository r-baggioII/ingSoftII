package com.greedy_cars_institucional.institucional.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.greedy_cars_institucional.institucional.shared.template.dao.BaseApiDao;
import com.greedy_cars_institucional.institucional.shared.template.dto.BaseDTO;
import com.greedy_cars_institucional.institucional.shared.template.enums.BaseUseCaseService;
import com.greedy_cars_institucional.institucional.shared.template.exception.ErrorServiceException;

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
        try {
            return dao.findAll();
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
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
            validar(BaseUseCaseService.ALTA, dto);
            preAlta(dto);
            T creado = dao.create(dto);
            postAlta(creado);
            return creado;
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante el alta de {}", dto, e);
            throw new ErrorServiceException("Error de sistema al crear el registro", e);
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
