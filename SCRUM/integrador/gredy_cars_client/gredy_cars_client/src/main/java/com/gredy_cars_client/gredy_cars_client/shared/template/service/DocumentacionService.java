package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.DocumentacionDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DocumentacionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio cliente para operar con la documentación del backend.
 */
@Service
public class DocumentacionService extends BaseClientService<DocumentacionDTO, String> {

    public DocumentacionService(DocumentacionDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, DocumentacionDTO dto) throws ErrorServiceException {
        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (dto == null) {
            throw new ErrorServiceException("Debe indicar la documentación");
        }

        if (dto.getTipoDocumentacion() == null) {
            throw new ErrorServiceException("Debe seleccionar el tipo de documentación");
        }

        if (!StringUtils.hasText(dto.getPathArchivo())) {
            throw new ErrorServiceException("Debe indicar la ruta del archivo");
        }

        if (dto.getPathArchivo().length() > 500) {
            throw new ErrorServiceException("La ruta del archivo no puede exceder 500 caracteres");
        }

        if (!StringUtils.hasText(dto.getNombreArchivo())) {
            throw new ErrorServiceException("Debe indicar el nombre del archivo");
        }

        if (dto.getNombreArchivo().length() > 255) {
            throw new ErrorServiceException("El nombre del archivo no puede exceder 255 caracteres");
        }

        if (dto.getObservacion() != null && dto.getObservacion().length() > 500) {
            throw new ErrorServiceException("La observación no puede exceder 500 caracteres");
        }
    }

    @Override
    protected void preAlta(DocumentacionDTO dto) {
        if (dto.getId() != null && dto.getId().isBlank()) {
            dto.setId(null);
        }
    }

    @Override
    protected void preModificacion(String id, DocumentacionDTO dto) throws ErrorServiceException {
        if (!StringUtils.hasText(id)) {
            throw new ErrorServiceException("El id de la documentación es obligatorio para modificar");
        }
        dto.setId(id);
    }
}
