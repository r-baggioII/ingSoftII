package org.example.service;

import org.example.entity.Paciente;
import org.example.exception.ErrorServiceException;
import org.example.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PacienteService extends BaseService<Paciente, String> {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        super(pacienteRepository);
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    protected void preAlta(Paciente entidad) throws ErrorServiceException {
        // Generar ID si no existe
        if (entidad.getId() == null || entidad.getId().isEmpty()) {
            entidad.setId(UUID.randomUUID().toString());
        }
        
        // Verificar que no exista un paciente con el mismo documento (que no esté eliminado)
        pacienteRepository.findByDocumento(entidad.getDocumento())
            .ifPresent(pacienteExistente -> {
                if (!Boolean.TRUE.equals(pacienteExistente.getEliminado())) {
                    throw new RuntimeException("Ya existe un paciente con el documento: " + entidad.getDocumento());
                }
            });
    }

    @Override
    protected void preModificacion(Paciente entidad) throws ErrorServiceException {
        // Verificar que no exista otro paciente con el mismo documento
        pacienteRepository.findByDocumento(entidad.getDocumento())
            .ifPresent(pacienteExistente -> {
                if (!pacienteExistente.getId().equals(entidad.getId())) {
                    try {
                        throw new ErrorServiceException("Ya existe otro paciente con el documento: " + entidad.getDocumento());
                    } catch (ErrorServiceException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
    }

    @Override
    protected void validar(org.example.enums.BaseUseCaseService useCase, Paciente entidad) throws ErrorServiceException {
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
    public Paciente buscarPorDocumento(String documento) throws ErrorServiceException {
        return pacienteRepository.findByDocumento(documento)
            .filter(p -> !Boolean.TRUE.equals(p.getEliminado()))
            .orElseThrow(() -> new ErrorServiceException("No se encontró un paciente con el documento: " + documento));
    }
}
