package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.ContactoCorreoElectronico;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoCorreoElectronicoRepository;
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



