package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.BaseEntity;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.BaseRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Template service that centralises shared CRUD behaviour while exposing hook
 * methods for concrete services to extend or override specific steps.
 *
 * @param <T> entity type
 * @param <ID> identifier type
 */
@Transactional
public abstract class BaseService<T extends BaseEntity<ID>, ID> {

    private static final Logger log = LoggerFactory.getLogger(BaseService.class);

    protected final BaseRepository<T, ID> repository;

    protected BaseService(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    protected void validar(BaseUseCaseService useCase, T entidad) throws ErrorServiceException {}

    protected void preAlta(T entidad) throws ErrorServiceException {}

    protected void postAlta(T entidad) throws ErrorServiceException {}

    protected void preModificacion(T entidad) throws ErrorServiceException {}

    protected void preBaja(T entidad) throws ErrorServiceException {}

    protected abstract void actualizarEntidad(T entidadExistente, T entidadNueva);

    public T alta(T entidad) throws ErrorServiceException {
        try {
            entidad.setEliminado(false);
            validar(BaseUseCaseService.ALTA, entidad);
            
            log.info("=== ALTA: ID de la entidad ANTES de preAlta: {}", entidad.getId());
            preAlta(entidad);
            log.info("=== ALTA: ID de la entidad DESPUÉS de preAlta: {}", entidad.getId());

            T guardado = repository.save(entidad);

            postAlta(guardado);
            return guardado;

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error en alta de entidad: {}", e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al dar de alta", e);
        }
    }

    public Optional<T> modificar(ID id, T entidadNueva) throws ErrorServiceException {
        try {
            entidadNueva.setId(id);
            validar(BaseUseCaseService.MODIFICACION, entidadNueva);

            Optional<T> entidadExistenteOpt = repository.findByIdAndEliminadoIsFalse(id);

            if (entidadExistenteOpt.isEmpty()) {
                return Optional.empty();
            }

            T entidadExistente = entidadExistenteOpt.get();
            preModificacion(entidadExistente);

            actualizarEntidad(entidadExistente, entidadNueva);

            T actualizado = repository.save(entidadExistente);
            return Optional.of(actualizado);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error en modificacion de entidad [ID: {}]: {}", id, e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al modificar", e);
        }
    }

    public void baja(ID id) throws ErrorServiceException {
        try {
            T entidadBaja = obtenerEntidad(id);

            log.info("=== INICIANDO BAJA LÓGICA ===");
            log.info("Entidad a eliminar - ID: {}, Tipo: {}", id, entidadBaja.getClass().getSimpleName());
            log.info("Estado ANTES de baja - eliminado: {}", entidadBaja.getEliminado());

            validar(BaseUseCaseService.BAJA, entidadBaja);

            preBaja(entidadBaja);

            entidadBaja.setEliminado(true);
            log.info("Estado DESPUÉS de setEliminado(true) - eliminado: {}", entidadBaja.getEliminado());

            T guardado = repository.save(entidadBaja);
            log.info("Estado DESPUÉS de save() - eliminado: {}", guardado.getEliminado());
            log.info("=== FIN BAJA LÓGICA ===");

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error en baja de entidad [ID: {}]: {}", id, e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al dar de baja", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<T> obtener(ID id) throws ErrorServiceException {
        try {
            return repository.findByIdAndEliminadoIsFalse(id);
        } catch (Exception e) {
            log.error("Error en obtener entidad [ID: {}]: {}", id, e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al obtener", e);
        }
    }

    @Transactional(readOnly = true)
    public T obtenerEntidad(ID id) throws ErrorServiceException {
        try {
            return repository.findByIdAndEliminadoIsFalse(id)
                    .orElseThrow(() -> new ErrorServiceException("Entidad no encontrada o eliminada."));
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error en obtenerEntidad [ID: {}]: {}", id, e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al obtener entidad", e);
        }
    }

    @Transactional(readOnly = true)
    public List<T> listarActivos() throws ErrorServiceException {
        try {
            return repository.findAllByEliminadoIsFalse();
        } catch (Exception e) {
            log.error("Error en listarActivos: {}", e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al listar activos", e);
        }
    }
}
