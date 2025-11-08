package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.CaracteristicaVehiculoDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CaracteristicaVehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de CaracteristicaVehiculo que delega en el DAO remoto.
 */
@Service
public class CaracteristicaVehiculoService extends BaseClientService<CaracteristicaVehiculoDTO, String> {

    public CaracteristicaVehiculoService(CaracteristicaVehiculoDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, CaracteristicaVehiculoDTO caracteristica) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (caracteristica == null) {
            throw new ErrorServiceException("Debe indicar la característica del vehículo");
        }

        if (!StringUtils.hasText(caracteristica.getMarca())) {
            throw new ErrorServiceException("Debe indicar la marca del vehículo");
        }

        if (!StringUtils.hasText(caracteristica.getModelo())) {
            throw new ErrorServiceException("Debe indicar el modelo del vehículo");
        }

        caracteristica.setMarca(caracteristica.getMarca().trim());
        caracteristica.setModelo(caracteristica.getModelo().trim());

        if (Boolean.TRUE.equals(caracteristica.getEliminado())) {
            throw new ErrorServiceException("La característica indicada se encuentra eliminada");
        }
    }

    @Override
    protected void preAlta(CaracteristicaVehiculoDTO dto) throws ErrorServiceException {
        if (dto.getId() != null && dto.getId().isBlank()) {
            dto.setId(null);
        }
    }

    @Override
    protected void preModificacion(String id, CaracteristicaVehiculoDTO dto) throws ErrorServiceException {
        if (id == null || id.isBlank()) {
            throw new ErrorServiceException("El id de la característica es obligatorio para modificar");
        }
        dto.setId(id);
    }
}
