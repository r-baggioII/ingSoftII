package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.DireccionDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DireccionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.LocalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Dirección que verifica los datos antes de invocar a la
 * API del servidor.
 */
@Service
public class DireccionService extends BaseClientService<DireccionDTO, Long> {

    public DireccionService(DireccionDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, DireccionDTO direccion) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (direccion == null) {
            throw new ErrorServiceException("Debe indicar la dirección");
        }

        if (!StringUtils.hasText(direccion.getCalle())) {
            throw new ErrorServiceException("Debe indicar la calle");
        }
        direccion.setCalle(direccion.getCalle().trim());

        if (!StringUtils.hasText(direccion.getNumeracion())) {
            throw new ErrorServiceException("Debe indicar la numeración");
        }
        direccion.setNumeracion(direccion.getNumeracion().trim());

        if (!StringUtils.hasText(direccion.getBarrio())) {
            throw new ErrorServiceException("Debe indicar el barrio");
        }
        direccion.setBarrio(direccion.getBarrio().trim());

        if (!StringUtils.hasText(direccion.getPisoCasa())) {
            throw new ErrorServiceException("Debe indicar el piso / casa");
        }
        direccion.setPisoCasa(direccion.getPisoCasa().trim());

        if (!StringUtils.hasText(direccion.getPuertaManzana())) {
            throw new ErrorServiceException("Debe indicar el puerta / manzana");
        }
        direccion.setPuertaManzana(direccion.getPuertaManzana().trim());

        if (StringUtils.hasText(direccion.getObservacion())) {
            direccion.setObservacion(direccion.getObservacion().trim());
        }

        if (Boolean.TRUE.equals(direccion.getEliminado())) {
            throw new ErrorServiceException("La dirección indicada se encuentra eliminada");
        }

        LocalidadDTO localidad = direccion.getLocalidad();
        if (localidad == null || localidad.getId() == null) {
            throw new ErrorServiceException("Debe indicar la localidad");
        }

        if (Boolean.TRUE.equals(localidad.getEliminado())) {
            throw new ErrorServiceException("La localidad indicada es incorrecta");
        }
    }
}
