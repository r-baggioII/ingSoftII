package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.ContactoTelefonicoDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoTelefonicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio para contactos telefónicos, alineado con las validaciones del backend.
 */
@Service
public class ContactoTelefonicoService extends BaseClientService<ContactoTelefonicoDTO, String> {

    public ContactoTelefonicoService(ContactoTelefonicoDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, ContactoTelefonicoDTO contacto) throws ErrorServiceException {
        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (contacto == null) {
            throw new ErrorServiceException("Debe indicar el contacto telefónico");
        }

        if (!StringUtils.hasText(contacto.getTelefono())) {
            throw new ErrorServiceException("Debe indicar el número de teléfono");
        }
        contacto.setTelefono(contacto.getTelefono().trim());

        if (contacto.getTipoTelefono() == null) {
            throw new ErrorServiceException("Debe seleccionar el tipo de teléfono");
        }

        if (contacto.getTipoContacto() == null) {
            throw new ErrorServiceException("Debe seleccionar el tipo de contacto");
        }

        if (StringUtils.hasText(contacto.getObservacion())) {
            contacto.setObservacion(contacto.getObservacion().trim());
        }

        if (StringUtils.hasText(contacto.getPersonaId())) {
            contacto.setPersonaId(contacto.getPersonaId().trim());
        } else {
            contacto.setPersonaId(null);
        }

        if (Boolean.TRUE.equals(contacto.getEliminado())) {
            throw new ErrorServiceException("El contacto indicado se encuentra eliminado");
        }
    }

    @Override
    protected void preAlta(ContactoTelefonicoDTO dto) throws ErrorServiceException {
        if (dto.getId() != null && dto.getId().isBlank()) {
            dto.setId(null);
        }
    }

    @Override
    protected void preModificacion(String id, ContactoTelefonicoDTO dto) throws ErrorServiceException {
        if (!StringUtils.hasText(id)) {
            throw new ErrorServiceException("El id del contacto es obligatorio para modificar");
        }
        dto.setId(id);
    }
}

