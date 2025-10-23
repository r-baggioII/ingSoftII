package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.entity.BaseEntity;
import org.example.repository.BaseRepository;
import org.example.exception.ErrorServiceException;
import org.example.enums.BaseUseCaseService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class BaseService<T extends BaseEntity<ID>, ID> {

    protected final BaseRepository<T, ID> repository;

    protected BaseService(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    public T alta(T entidad) throws ErrorServiceException {
        try {

            entidad.setEliminado(false);
            validar(BaseUseCaseService.ALTA, entidad);
            preAlta(entidad);

            T guardado = repository.save(entidad);

            postAlta(guardado);
            return guardado;

        }catch(ErrorServiceException e) {
            throw e;
        }catch(Exception e) {
            e.printStackTrace(); // Para debug
            throw new ErrorServiceException("Error de Sistemas: " + e.getMessage());
        }
    }

    public Optional<T> modificar(ID id, T entidadNueva)throws ErrorServiceException {
        try {

            entidadNueva.setId(id);
            validar(BaseUseCaseService.MODIFICACION,entidadNueva);
            preModificacion(entidadNueva);

            return repository.findById(id).map(entidad -> {
                entidadNueva.setId(id);
                T actualizado = repository.save(entidadNueva);
                return actualizado;
            });

        }catch(ErrorServiceException e) {
            throw e;
        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    public void baja(ID id)throws ErrorServiceException {
        try {

            T entidadBaja = obtenerEntidad(id);
            validar(BaseUseCaseService.BAJA,entidadBaja);
            entidadBaja.setEliminado(true);
            repository.save(entidadBaja);

        }catch(ErrorServiceException e) {
            throw e;
        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional(readOnly = true)
    public Optional<T> obtener(ID id)throws ErrorServiceException {
        try {

            return repository.findById(id).
                    filter(e -> !Boolean.TRUE.equals(e.getEliminado()));

        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional(readOnly = true)
    public T obtenerEntidad(ID id) throws ErrorServiceException {
        try {
            return repository.findById(id)
                    .filter(e -> !Boolean.TRUE.equals(e.getEliminado()))
                    .orElseThrow(() -> new ErrorServiceException("Entidad no encontrada o eliminada."));
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional(readOnly = true)
    public List<T> listarActivos()throws ErrorServiceException {
        try {

            return repository.findAll().stream()
                    .filter(e -> !Boolean.TRUE.equals(e.getEliminado()))
                    .toList();

        }catch(Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }



    //Metodos para ser redefinidos en las clases de servicio que heredan, con el objetivo
    //que sea necesario realizar acciones previas o posteriores en las Altas, Bajas y
    //Modificaciones.
    //Se deberá redefinir el comportamiento en la clase que hereda.
    protected void validar(BaseUseCaseService useCase, T entidad) throws ErrorServiceException {}
    protected void preAlta(T entidad) throws ErrorServiceException {}
    protected void postAlta(T entidad)throws ErrorServiceException {}
    protected void preModificacion(T entidad)throws ErrorServiceException {}
    protected void preBaja(T entidad)throws ErrorServiceException {}
}