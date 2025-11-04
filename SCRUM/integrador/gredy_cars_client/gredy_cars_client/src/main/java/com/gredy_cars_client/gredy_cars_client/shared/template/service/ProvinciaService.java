package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.ProvinciaDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PaisDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ProvinciaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Provincia que delega en el DAO remoto y aplica las
 * mismas validaciones de negocio que el servidor antes de cada petición.
 */
@Service
public class ProvinciaService extends BaseClientService<ProvinciaDTO, Long> {

    public ProvinciaService(ProvinciaDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, ProvinciaDTO provincia) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (provincia == null) {
            throw new ErrorServiceException("Debe indicar la provincia");
        }

        if (!StringUtils.hasText(provincia.getNombre())) {
            throw new ErrorServiceException("Debe indicar el nombre de la provincia");
        }

        provincia.setNombre(provincia.getNombre().trim());

        if (Boolean.TRUE.equals(provincia.getEliminado())) {
            throw new ErrorServiceException("La provincia indicada se encuentra eliminada");
        }

        PaisDTO pais = provincia.getPais();
        if (pais == null || pais.getId() == null) {
            throw new ErrorServiceException("Debe seleccionar un país");
        }

        if (Boolean.TRUE.equals(pais.getEliminado())) {
            throw new ErrorServiceException("El país de la provincia indicado es incorrecto");
        }

        List<ProvinciaDTO> existentes = listarActivos();
        boolean duplicado = existentes.stream()
            .filter(actual -> actual.getPais() != null && actual.getPais().getId() != null)
            .filter(actual -> actual.getPais().getId().equals(pais.getId()))
            .anyMatch(actual -> actual.getNombre().equalsIgnoreCase(provincia.getNombre())
                && (useCase == BaseUseCaseService.ALTA || !actual.getId().equals(provincia.getId())));

        if (duplicado) {
            throw new ErrorServiceException("Existe una provincia con el nombre indicado");
        }
    }
}
