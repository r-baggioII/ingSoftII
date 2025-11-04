package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.LocalidadDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DepartamentoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.LocalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Localidad que emula las validaciones del servicio del
 * servidor antes de invocar la API REST.
 */
@Service
public class LocalidadService extends BaseClientService<LocalidadDTO, Long> {

    public LocalidadService(LocalidadDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, LocalidadDTO localidad) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (localidad == null) {
            throw new ErrorServiceException("Debe indicar la localidad");
        }

        if (!StringUtils.hasText(localidad.getNombre())) {
            throw new ErrorServiceException("Debe indicar el nombre de la localidad");
        }

        localidad.setNombre(localidad.getNombre().trim());

        if (!StringUtils.hasText(localidad.getCodigoPostal())) {
            throw new ErrorServiceException("Debe indicar el nombre código postal");
        }

        localidad.setCodigoPostal(localidad.getCodigoPostal().trim());

        if (Boolean.TRUE.equals(localidad.getEliminado())) {
            throw new ErrorServiceException("La localidad indicada se encuentra eliminada");
        }

        DepartamentoDTO departamento = localidad.getDepartamento();
        if (departamento == null || departamento.getId() == null) {
            throw new ErrorServiceException("Debe indicar el departamento");
        }

        if (Boolean.TRUE.equals(departamento.getEliminado())) {
            throw new ErrorServiceException("El departamento de la localidad indicada es incorrecto");
        }

        List<LocalidadDTO> existentes = listarActivos();
        boolean duplicado = existentes.stream()
            .filter(actual -> actual.getDepartamento() != null && actual.getDepartamento().getId() != null)
            .filter(actual -> actual.getDepartamento().getId().equals(departamento.getId()))
            .anyMatch(actual -> actual.getNombre().equalsIgnoreCase(localidad.getNombre())
                && (useCase == BaseUseCaseService.ALTA || !actual.getId().equals(localidad.getId())));

        if (duplicado) {
            throw new ErrorServiceException("Existe una localidad con el nombre indicado");
        }
    }
}
