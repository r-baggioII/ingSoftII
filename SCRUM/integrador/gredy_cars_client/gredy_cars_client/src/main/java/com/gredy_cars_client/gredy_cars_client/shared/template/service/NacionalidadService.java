package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.NacionalidadDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.NacionalidadDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Nacionalidad que delega en el DAO remoto y aplica validaciones
 * equivalentes a las del servidor antes de enviar cada petición.
 */
@Service
public class NacionalidadService extends BaseClientService<NacionalidadDTO, String> {

    public NacionalidadService(NacionalidadDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, NacionalidadDTO nacionalidad) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (nacionalidad == null) {
            throw new ErrorServiceException("Debe indicar la nacionalidad");
        }

        if (!StringUtils.hasText(nacionalidad.getNombre())) {
            throw new ErrorServiceException("Debe indicar el nombre de la nacionalidad");
        }

        nacionalidad.setNombre(nacionalidad.getNombre().trim());

        if (Boolean.TRUE.equals(nacionalidad.getEliminado())) {
            throw new ErrorServiceException("La nacionalidad indicada se encuentra eliminada");
        }

        List<NacionalidadDTO> existentes = listarActivos();
        boolean duplicado = existentes.stream()
            .anyMatch(actual -> actual.getNombre().equalsIgnoreCase(nacionalidad.getNombre())
                && (useCase == BaseUseCaseService.ALTA || !actual.getId().equals(nacionalidad.getId())));

        if (duplicado) {
            throw new ErrorServiceException("Existe una nacionalidad con el nombre indicado");
        }
    }

    @Override
    protected void preAlta(NacionalidadDTO dto) throws ErrorServiceException {
        // Evitar enviar un id vacío en altas; el servidor genera UUID
        if (dto.getId() != null && dto.getId().isBlank()) {
            dto.setId(null);
        }
    }

    @Override
    protected void preModificacion(String id, NacionalidadDTO dto) throws ErrorServiceException {
        // Normalizar id en modificación
        if (id == null || id.isBlank()) {
            throw new ErrorServiceException("El id de la nacionalidad es obligatorio para modificar");
        }
        dto.setId(id);
    }
}
