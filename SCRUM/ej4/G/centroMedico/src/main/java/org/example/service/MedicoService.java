package org.example.service;

import org.example.entity.Medico;
import org.example.exception.ErrorServiceException;
import org.example.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MedicoService extends BaseService<Medico, String> {

    private final MedicoRepository medicoRepository;

    public MedicoService(MedicoRepository medicoRepository) {
        super(medicoRepository);
        this.medicoRepository = medicoRepository;
    }

    @Override
    protected void preAlta(Medico entidad) throws ErrorServiceException {
        // Generar ID si no existe
        if (entidad.getId() == null || entidad.getId().isEmpty()) {
            entidad.setId(UUID.randomUUID().toString());
        }
        
        // Verificar que no exista un médico con el mismo documento (que no esté eliminado)
        medicoRepository.findByDocumento(entidad.getDocumento())
            .ifPresent(medicoExistente -> {
                if (!Boolean.TRUE.equals(medicoExistente.getEliminado())) {
                    throw new RuntimeException("Ya existe un médico con el documento: " + entidad.getDocumento());
                }
            });
    }

    @Override
    protected void preModificacion(Medico entidad) throws ErrorServiceException {
        // Verificar que no exista otro médico con el mismo documento
        medicoRepository.findByDocumento(entidad.getDocumento())
            .ifPresent(medicoExistente -> {
                if (!medicoExistente.getId().equals(entidad.getId())) {
                    try {
                        throw new ErrorServiceException("Ya existe otro médico con el documento: " + entidad.getDocumento());
                    } catch (ErrorServiceException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
    }

    @Override
    protected void validar(org.example.enums.BaseUseCaseService useCase, Medico entidad) throws ErrorServiceException {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new ErrorServiceException("El nombre es obligatorio");
        }
        
        if (entidad.getApellido() == null || entidad.getApellido().trim().isEmpty()) {
            throw new ErrorServiceException("El apellido es obligatorio");
        }
        
        if (entidad.getDocumento() == null || entidad.getDocumento().trim().isEmpty()) {
            throw new ErrorServiceException("El documento es obligatorio");
        }
    }

    // Métodos adicionales
    public Medico buscarPorDocumento(String documento) throws ErrorServiceException {
        return medicoRepository.findByDocumento(documento)
            .filter(m -> !Boolean.TRUE.equals(m.getEliminado()))
            .orElseThrow(() -> new ErrorServiceException("No se encontró un médico con el documento: " + documento));
    }
}
