package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.AlquilerDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Alquiler que delega en el DAO remoto y aplica validaciones
 * equivalentes a las del servidor antes de enviar cada petición.
 */
@Service
public class AlquilerService extends BaseClientService<AlquilerDTO, String> {

    public AlquilerService(AlquilerDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, AlquilerDTO alquiler) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (alquiler == null) {
            throw new ErrorServiceException("Debe indicar el alquiler");
        }

        if (alquiler.getFechaDesde() == null) {
            throw new ErrorServiceException("Debe indicar la fecha de inicio del alquiler");
        }

        if (alquiler.getFechaHasta() == null) {
            throw new ErrorServiceException("Debe indicar la fecha de fin del alquiler");
        }

        if (alquiler.getFechaDesde().isAfter(alquiler.getFechaHasta())) {
            throw new ErrorServiceException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }

        if (!StringUtils.hasText(alquiler.getIdCliente())) {
            throw new ErrorServiceException("Debe indicar el cliente");
        }

        if (!StringUtils.hasText(alquiler.getIdVehiculo())) {
            throw new ErrorServiceException("Debe indicar el vehículo");
        }
    }

    @Override
    protected void preAlta(AlquilerDTO dto) throws ErrorServiceException {
        // Evitar enviar un id vacío en altas; el servidor genera UUID
        if (dto.getId() != null && dto.getId().isBlank()) {
            dto.setId(null);
        }
    }

    @Override
    protected void preModificacion(String id, AlquilerDTO dto) throws ErrorServiceException {
        // Normalizar id en modificación
        if (id == null || id.isBlank()) {
            throw new ErrorServiceException("El id del alquiler es obligatorio para modificar");
        }
        dto.setId(id);
    }
}