package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.ContactoTelefonico;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoTelefonicoRepository;
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
