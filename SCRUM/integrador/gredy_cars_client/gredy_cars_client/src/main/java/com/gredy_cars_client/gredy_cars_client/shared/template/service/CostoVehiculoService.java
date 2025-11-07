package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import org.springframework.stereotype.Service;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.CostoVehiculoDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CostoVehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de CostoVehiculo que delega en el DAO remoto.
 */
@Service
public class CostoVehiculoService extends BaseClientService<CostoVehiculoDTO, String> {

    public CostoVehiculoService(CostoVehiculoDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, CostoVehiculoDTO costo) throws ErrorServiceException {
        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }
        if (costo == null) {
            throw new ErrorServiceException("Debe indicar el costo del vehículo");
        }
    }

    @Override
    protected void preAlta(CostoVehiculoDTO dto) throws ErrorServiceException {
        if (dto.getId() != null && dto.getId().isBlank()) {
            dto.setId(null);
        }
    }

    @Override
    protected void preModificacion(String id, CostoVehiculoDTO dto) throws ErrorServiceException {
        if (id == null || id.isBlank()) {
            throw new ErrorServiceException("El id del costo es obligatorio para modificar");
        }
        dto.setId(id);
    }
}
