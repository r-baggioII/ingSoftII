package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.VehiculoDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CaracteristicaVehiculoMinDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.VehiculoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.EstadoVehiculo;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Vehiculo que delega en el DAO remoto y aplica validaciones
 * equivalentes a las del servidor antes de enviar cada petición.
 */
@Service
public class VehiculoService extends BaseClientService<VehiculoDTO, String> {

    public VehiculoService(VehiculoDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, VehiculoDTO vehiculo) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (vehiculo == null) {
            throw new ErrorServiceException("Debe indicar el vehículo");
        }

        if (!StringUtils.hasText(vehiculo.getPatente())) {
            throw new ErrorServiceException("Debe indicar la patente del vehículo");
        }

        vehiculo.setPatente(vehiculo.getPatente().trim().toUpperCase());

        if (!StringUtils.hasText(vehiculo.getCaracteristicaVehiculoId())) {
            throw new ErrorServiceException("Debe asociar una característica al vehículo");
        }

        if (vehiculo.getEstadoVehiculo() == null) {
            vehiculo.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
        }

        if (Boolean.TRUE.equals(vehiculo.getEliminado())) {
            throw new ErrorServiceException("El vehículo indicado se encuentra eliminado");
        }

        List<VehiculoDTO> existentes = listarActivos();
        boolean duplicado = existentes.stream()
            .anyMatch(actual -> actual.getPatente().equalsIgnoreCase(vehiculo.getPatente())
                && (useCase == BaseUseCaseService.ALTA || !actual.getId().equals(vehiculo.getId())));

        if (duplicado) {
            throw new ErrorServiceException("Existe un vehículo con la patente indicada");
        }
    }

    @Override
    protected void preAlta(VehiculoDTO dto) throws ErrorServiceException {
        if (dto.getId() != null && dto.getId().isBlank()) {
            dto.setId(null);
        }
        normalizarEstado(dto);
        vincularCaracteristica(dto);
    }

    @Override
    protected void preModificacion(String id, VehiculoDTO dto) throws ErrorServiceException {
        if (!StringUtils.hasText(id)) {
            throw new ErrorServiceException("El id del vehículo es obligatorio para modificar");
        }
        dto.setId(id);
        normalizarEstado(dto);
        vincularCaracteristica(dto);
    }

    private void normalizarEstado(VehiculoDTO dto) {
        if (dto.getEstadoVehiculo() == null) {
            dto.setEstadoVehiculo(EstadoVehiculo.DISPONIBLE);
        }
    }

    private void vincularCaracteristica(VehiculoDTO dto) throws ErrorServiceException {
        if (!StringUtils.hasText(dto.getCaracteristicaVehiculoId())) {
            throw new ErrorServiceException("Debe seleccionar o crear una característica para el vehículo");
        }
        String caracId = dto.getCaracteristicaVehiculoId().trim();
        dto.setCaracteristicaVehiculoId(caracId);
        if (dto.getCaracteristica() == null) {
            dto.setCaracteristicaVehiculo(new CaracteristicaVehiculoMinDTO());
        }
        dto.getCaracteristica().setId(caracId);
    }
}
