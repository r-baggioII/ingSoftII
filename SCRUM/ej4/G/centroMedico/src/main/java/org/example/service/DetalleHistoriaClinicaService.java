package org.example.service;

import org.example.entity.DetalleHistoriaClinica;
import org.example.entity.HistoriaClinica;
import org.example.exception.ErrorServiceException;
import org.example.repository.DetalleHistoriaClinicaRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class DetalleHistoriaClinicaService extends BaseService<DetalleHistoriaClinica, String> {

    private final DetalleHistoriaClinicaRepository detalleHistoriaClinicaRepository;

    public DetalleHistoriaClinicaService(DetalleHistoriaClinicaRepository detalleHistoriaClinicaRepository) {
        super(detalleHistoriaClinicaRepository);
        this.detalleHistoriaClinicaRepository = detalleHistoriaClinicaRepository;
    }

    @Override
    protected void preAlta(DetalleHistoriaClinica entidad) throws ErrorServiceException {
        // Generar ID si no existe
        if (entidad.getId() == null || entidad.getId().isEmpty()) {
            entidad.setId(UUID.randomUUID().toString());
        }
        
        // Establecer fecha actual si no existe
        if (entidad.getFechaHistoria() == null) {
            entidad.setFechaHistoria(new Date());
        }
    }

    @Override
    protected void validar(org.example.enums.BaseUseCaseService useCase, DetalleHistoriaClinica entidad) throws ErrorServiceException {
        if (entidad.getFechaHistoria() == null) {
            throw new ErrorServiceException("La fecha de la historia es obligatoria");
        }
        
        if (entidad.getDetalleHistoria() == null || entidad.getDetalleHistoria().trim().isEmpty()) {
            throw new ErrorServiceException("El detalle de la historia es obligatorio");
        }
        
        if (entidad.getHistoriaClinica() == null) {
            throw new ErrorServiceException("La historia clínica es obligatoria");
        }
        
        if (entidad.getMedico() == null) {
            throw new ErrorServiceException("El médico es obligatorio");
        }
    }

    // Métodos adicionales
    public List<DetalleHistoriaClinica> listarPorHistoriaClinica(HistoriaClinica historiaClinica) throws ErrorServiceException {
        try {
            return detalleHistoriaClinicaRepository
                .findByHistoriaClinicaOrderByFechaHistoriaDesc(historiaClinica)
                .stream()
                .filter(d -> !Boolean.TRUE.equals(d.getEliminado()))
                .toList();
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar los detalles de la historia clínica");
        }
    }
}
