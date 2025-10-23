package org.example.service;

import org.example.entity.HistoriaClinica;
import org.example.entity.Paciente;
import org.example.exception.ErrorServiceException;
import org.example.repository.HistoriaClinicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class HistoriaClinicaService extends BaseService<HistoriaClinica, String> {

    private final HistoriaClinicaRepository historiaClinicaRepository;

    public HistoriaClinicaService(HistoriaClinicaRepository historiaClinicaRepository) {
        super(historiaClinicaRepository);
        this.historiaClinicaRepository = historiaClinicaRepository;
    }

    @Override
    public List<HistoriaClinica> listarActivos() throws ErrorServiceException {
        // Usar la consulta optimizada que carga los detalles en una sola query
        return historiaClinicaRepository.findAllWithDetalles();
    }

    @Override
    public Optional<HistoriaClinica> obtener(String id) throws ErrorServiceException {
        // Usar la consulta optimizada que carga los detalles en una sola query
        return historiaClinicaRepository.findByIdWithDetalles(id)
            .filter(h -> !Boolean.TRUE.equals(h.getEliminado()));
    }

    @Override
    public HistoriaClinica obtenerEntidad(String id) throws ErrorServiceException {
        // Usar la consulta optimizada que carga los detalles en una sola query
        return historiaClinicaRepository.findByIdWithDetalles(id)
            .filter(h -> !Boolean.TRUE.equals(h.getEliminado()))
            .orElseThrow(() -> new ErrorServiceException("No se encontró la historia clínica con ID: " + id));
    }

    @Override
    protected void preAlta(HistoriaClinica entidad) throws ErrorServiceException {
        // Generar ID si no existe
        if (entidad.getId() == null || entidad.getId().isEmpty()) {
            entidad.setId(UUID.randomUUID().toString());
        }
        
        // Verificar que el paciente no tenga ya una historia clínica
        if (historiaClinicaRepository.existsByPaciente(entidad.getPaciente())) {
            throw new ErrorServiceException("El paciente ya tiene una historia clínica");
        }
    }

    @Override
    protected void validar(org.example.enums.BaseUseCaseService useCase, HistoriaClinica entidad) throws ErrorServiceException {
        if (entidad.getPaciente() == null) {
            throw new ErrorServiceException("El paciente es obligatorio");
        }
    }

    // Métodos adicionales
    public HistoriaClinica buscarPorPaciente(Paciente paciente) throws ErrorServiceException {
        return historiaClinicaRepository.findByPaciente(paciente)
            .filter(h -> !Boolean.TRUE.equals(h.getEliminado()))
            .orElseThrow(() -> new ErrorServiceException("No se encontró la historia clínica del paciente"));
    }
}
