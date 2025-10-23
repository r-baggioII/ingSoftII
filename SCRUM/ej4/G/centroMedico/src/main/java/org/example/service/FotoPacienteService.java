package org.example.service;

import org.example.entity.FotoPaciente;
import org.example.exception.ErrorServiceException;
import org.example.repository.FotoPacienteRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FotoPacienteService extends BaseService<FotoPaciente, String> {

    private final FotoPacienteRepository fotoPacienteRepository;

    public FotoPacienteService(FotoPacienteRepository fotoPacienteRepository) {
        super(fotoPacienteRepository);
        this.fotoPacienteRepository = fotoPacienteRepository;
    }

    @Override
    protected void preAlta(FotoPaciente entidad) throws ErrorServiceException {
        // Generar ID si no existe
        if (entidad.getId() == null || entidad.getId().isEmpty()) {
            entidad.setId(UUID.randomUUID().toString());
        }
    }

    @Override
    protected void validar(org.example.enums.BaseUseCaseService useCase, FotoPaciente entidad) throws ErrorServiceException {
        if (entidad.getNombre() == null || entidad.getNombre().trim().isEmpty()) {
            throw new ErrorServiceException("El nombre de la foto es obligatorio");
        }
        
        if (entidad.getMime() == null || entidad.getMime().trim().isEmpty()) {
            throw new ErrorServiceException("El tipo MIME es obligatorio");
        }
        
        if (entidad.getContenido() == null || entidad.getContenido().length == 0) {
            throw new ErrorServiceException("El contenido de la foto es obligatorio");
        }
    }
}
