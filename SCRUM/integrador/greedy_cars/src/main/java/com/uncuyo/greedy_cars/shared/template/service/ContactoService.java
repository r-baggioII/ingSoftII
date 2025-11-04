package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
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



