package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.PaisDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PaisDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de País que delega en el DAO remoto y aplica validaciones
 * equivalentes a las del servidor antes de enviar cada petición.
 */
@Service
public class PaisService extends BaseClientService<PaisDTO, Long> {

    public PaisService(PaisDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, PaisDTO pais) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (pais == null) {
            throw new ErrorServiceException("Debe indicar el país");
        }

        if (!StringUtils.hasText(pais.getNombre())) {
            throw new ErrorServiceException("Debe indicar el nombre del país");
        }

        pais.setNombre(pais.getNombre().trim());

        if (Boolean.TRUE.equals(pais.getEliminado())) {
            throw new ErrorServiceException("El país indicado se encuentra eliminado");
        }

        List<PaisDTO> existentes = listarActivos();
        boolean duplicado = existentes.stream()
            .anyMatch(actual -> actual.getNombre().equalsIgnoreCase(pais.getNombre())
                && (useCase == BaseUseCaseService.ALTA || !actual.getId().equals(pais.getId())));

        if (duplicado) {
            throw new ErrorServiceException("Existe un país con el nombre indicado");
        }
    }
}
