// Asegúrate que el paquete sea el correcto (ej: org.sistemaMecanico.service)
package org.sistemaMecanico.service;


import java.util.List;
import java.util.Optional;
import org.sistemaMecanico.entity.BaseEntity;
import org.sistemaMecanico.repository.BaseRepository; // Importa el repo actualizado
import org.sistemaMecanico.exception.ErrorServiceException;
import org.sistemaMecanico.enums.BaseUseCaseService;
import org.slf4j.Logger; // Importa un logger
import org.slf4j.LoggerFactory; // Importa un logger
import org.springframework.transaction.annotation.Transactional;

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

    // --- MÉTODOS CRUD ---

    public T alta(T entidad) throws ErrorServiceException {
        try {
            entidad.setEliminado(false);
            validar(BaseUseCaseService.ALTA, entidad);
            preAlta(entidad);

            T guardado = repository.save(entidad);

            postAlta(guardado);
            return guardado;

        } catch (ErrorServiceException e) {
            throw e; // Relanza la excepción de negocio
        } catch (Exception e) {
            log.error("Error en alta de entidad: {}", e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al dar de alta", e);
        }
    }

    /**
     * Modifica una entidad de forma segura.
     * Previene la pérdida de datos al no guardar directamente 'entidadNueva',
     * sino que usa el hook 'actualizarEntidad' para mapear los campos.
     */
    public Optional<T> modificar(ID id, T entidadNueva) throws ErrorServiceException {
        try {
            // 1. Validar la entidad con los datos nuevos
            entidadNueva.setId(id); // Asigna el ID para la validación
            validar(BaseUseCaseService.MODIFICACION, entidadNueva);

            // 2. Buscar la entidad existente y activa en la BD
            Optional<T> entidadExistenteOpt = repository.findByIdAndEliminadoIsFalse(id);

            if (entidadExistenteOpt.isEmpty()) {
                return Optional.empty(); // No se puede modificar algo que no existe o está eliminado
            }

            // 3. Aplicar hooks y lógica de actualización
            T entidadExistente = entidadExistenteOpt.get();
            preModificacion(entidadExistente); // Llama al hook con la entidad original

            // 4. Copiar campos (usando el nuevo hook abstracto)
            actualizarEntidad(entidadExistente, entidadNueva);

            // 5. Guardar la entidad existente (ahora actualizada)
            T actualizado = repository.save(entidadExistente);
            return Optional.of(actualizado);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error en modificacion de entidad [ID: {}]: {}", id, e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al modificar", e);
        }
    }

    /**
     * Realiza una baja lógica (soft-delete) de la entidad.
     */
    public void baja(ID id) throws ErrorServiceException {
        try {
            // obtenerEntidad ya filtra por 'eliminado = false'
            T entidadBaja = obtenerEntidad(id);
            
            log.info("=== INICIANDO BAJA LÓGICA ===");
            log.info("Entidad a eliminar - ID: {}, Tipo: {}", id, entidadBaja.getClass().getSimpleName());
            log.info("Estado ANTES de baja - eliminado: {}", entidadBaja.getEliminado());

            validar(BaseUseCaseService.BAJA, entidadBaja);

            // CORRECCIÓN: Llamada al hook preBaja
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

    // --- MÉTODOS DE CONSULTA (READ-ONLY) ---

    @Transactional(readOnly = true)
    public Optional<T> obtener(ID id) throws ErrorServiceException {
        try {
            // CORRECCIÓN: Usa el método optimizado del repositorio
            return repository.findByIdAndEliminadoIsFalse(id);
        } catch (Exception e) {
            log.error("Error en obtener entidad [ID: {}]: {}", id, e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al obtener", e);
        }
    }

    @Transactional(readOnly = true)
    public T obtenerEntidad(ID id) throws ErrorServiceException {
        try {
            // CORRECCIÓN: Usa el método optimizado del repositorio
            return repository.findByIdAndEliminadoIsFalse(id)
                    .orElseThrow(() -> new ErrorServiceException("Entidad no encontrada o eliminada."));
        } catch (ErrorServiceException e) {
            throw e; // Relanza la excepción de "no encontrada"
        } catch (Exception e) {
            log.error("Error en obtenerEntidad [ID: {}]: {}", id, e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al obtener entidad", e);
        }
    }

    @Transactional(readOnly = true)
    public List<T> listarActivos() throws ErrorServiceException {
        try {
            // CORRECCIÓN: Método eficiente que filtra en la BD
            return repository.findAllByEliminadoIsFalse();
        } catch (Exception e) {
            log.error("Error en listarActivos: {}", e.getMessage(), e);
            throw new ErrorServiceException("Error de Sistema al listar activos", e);
        }
    }
}