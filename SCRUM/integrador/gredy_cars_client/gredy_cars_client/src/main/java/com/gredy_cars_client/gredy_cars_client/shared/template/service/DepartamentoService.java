package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.DepartamentoDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DepartamentoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ProvinciaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Departamento que replica las validaciones de negocio
 * expuestas por el servidor antes de invocar a la API remota.
 */
@Service
public class DepartamentoService extends BaseClientService<DepartamentoDTO, Long> {

    public DepartamentoService(DepartamentoDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, DepartamentoDTO departamento) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (departamento == null) {
            throw new ErrorServiceException("Debe indicar el departamento");
        }

        if (!StringUtils.hasText(departamento.getNombre())) {
            throw new ErrorServiceException("Debe indicar el nombre del departamento");
        }

        departamento.setNombre(departamento.getNombre().trim());

        if (Boolean.TRUE.equals(departamento.getEliminado())) {
            throw new ErrorServiceException("El departamento indicado se encuentra eliminado");
        }

        ProvinciaDTO provincia = departamento.getProvincia();
        if (provincia == null || provincia.getId() == null) {
            throw new ErrorServiceException("Debe indicar la provincia");
        }

        if (Boolean.TRUE.equals(provincia.getEliminado())) {
            throw new ErrorServiceException("La provincia indicada es incorrecta");
        }

        List<DepartamentoDTO> existentes = listarActivos();
        boolean duplicado = existentes.stream()
            .filter(actual -> actual.getProvincia() != null && actual.getProvincia().getId() != null)
            .filter(actual -> actual.getProvincia().getId().equals(provincia.getId()))
            .anyMatch(actual -> actual.getNombre().equalsIgnoreCase(departamento.getNombre())
                && (useCase == BaseUseCaseService.ALTA || !actual.getId().equals(departamento.getId())));

        if (duplicado) {
            throw new ErrorServiceException("Existe un departamento con el nombre indicado");
        }
    }
}
