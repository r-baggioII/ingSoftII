package org.contactoEmpresa.service;

import org.contactoEmpresa.entity.ContactoCorreoElectronico;
import org.contactoEmpresa.repository.ContactoCorreoElectronicoRepository;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactoCorreoElectronicoService extends BaseService<ContactoCorreoElectronico, String> {

    @Autowired
    public ContactoCorreoElectronicoService(ContactoCorreoElectronicoRepository repository) {
        super(repository);
    }

    @Override
    protected void actualizarEntidad(ContactoCorreoElectronico entidadExistente, ContactoCorreoElectronico entidadNueva) {
        if (entidadNueva.getMail() != null) {
            entidadExistente.setMail(entidadNueva.getMail());
        }
        if (entidadNueva.getTipoContacto() != null) {
            entidadExistente.setTipoContacto(entidadNueva.getTipoContacto());
        }
        if (entidadNueva.getObservacion() != null) {
            entidadExistente.setObservacion(entidadNueva.getObservacion());
        }
    }
}



