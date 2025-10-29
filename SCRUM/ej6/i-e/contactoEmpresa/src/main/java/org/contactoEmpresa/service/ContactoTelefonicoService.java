package org.contactoEmpresa.service;

import org.contactoEmpresa.entity.ContactoTelefonico;
import org.contactoEmpresa.repository.ContactoTelefonicoRepository;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactoTelefonicoService extends BaseService<ContactoTelefonico, String> {

    @Autowired
    public ContactoTelefonicoService(ContactoTelefonicoRepository repository) {
        super(repository);
    }

    @Override
    protected void actualizarEntidad(ContactoTelefonico entidadExistente, ContactoTelefonico entidadNueva) {
        if (entidadNueva.getTelefono() != null) {
            entidadExistente.setTelefono(entidadNueva.getTelefono());
        }
        if (entidadNueva.getTipoTelefono() != null) {
            entidadExistente.setTipoTelefono(entidadNueva.getTipoTelefono());
        }
        if (entidadNueva.getTipoContacto() != null) {
            entidadExistente.setTipoContacto(entidadNueva.getTipoContacto());
        }
        if (entidadNueva.getObservacion() != null) {
            entidadExistente.setObservacion(entidadNueva.getObservacion());
        }
    }
}
