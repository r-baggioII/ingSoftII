package org.contactoEmpresa.service;

import org.contactoEmpresa.entity.Contacto;
import org.contactoEmpresa.repository.ContactoRepository;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactoService extends BaseService<Contacto, String> {

    @Autowired
    public ContactoService(ContactoRepository repository) {
        super(repository);
    }

    @Override
    protected void actualizarEntidad(Contacto entidadExistente, Contacto entidadNueva) {
        if (entidadNueva.getTipoContacto() != null) {
            entidadExistente.setTipoContacto(entidadNueva.getTipoContacto());
        }
        if (entidadNueva.getObservacion() != null) {
            entidadExistente.setObservacion(entidadNueva.getObservacion());
        }
    }
}



