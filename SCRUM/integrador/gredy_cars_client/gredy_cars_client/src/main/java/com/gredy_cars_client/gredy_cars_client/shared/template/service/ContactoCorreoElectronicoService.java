package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.ContactoCorreoElectronicoDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto para contactos de correo electrónico.
 */
@Service
public class ContactoCorreoElectronicoService extends BaseClientService<ContactoCorreoElectronicoDTO, String> {

    public ContactoCorreoElectronicoService(ContactoCorreoElectronicoDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, ContactoCorreoElectronicoDTO contacto) throws ErrorServiceException {
        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (contacto == null) {
            throw new ErrorServiceException("Debe indicar el contacto de correo electrónico");
        }

        if (!StringUtils.hasText(contacto.getMail())) {
            throw new ErrorServiceException("Debe indicar el correo electrónico");
        }
        contacto.setMail(contacto.getMail().trim());

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
    protected void preAlta(ContactoCorreoElectronicoDTO dto) throws ErrorServiceException {
        if (dto.getId() != null && dto.getId().isBlank()) {
            dto.setId(null);
        }
    }

    @Override
    protected void preModificacion(String id, ContactoCorreoElectronicoDTO dto) throws ErrorServiceException {
        if (!StringUtils.hasText(id)) {
            throw new ErrorServiceException("El id del contacto es obligatorio para modificar");
        }
        dto.setId(id);
    }
}
