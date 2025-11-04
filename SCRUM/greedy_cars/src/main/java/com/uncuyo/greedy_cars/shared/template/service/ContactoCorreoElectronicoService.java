package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;
import java.util.Optional;

import com.uncuyo.greedy_cars.shared.template.dto.ContactoCorreoElectronicoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoCorreoElectronico;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.ContactoCorreoElectronicoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoCorreoElectronicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactoCorreoElectronicoService extends BaseService<ContactoCorreoElectronico, String> {

    private final ContactoCorreoElectronicoMapper contactoCorreoElectronicoMapper;

    @Autowired
    public ContactoCorreoElectronicoService(ContactoCorreoElectronicoRepository repository, ContactoCorreoElectronicoMapper contactoCorreoElectronicoMapper) {
        super(repository);
        this.contactoCorreoElectronicoMapper = contactoCorreoElectronicoMapper;
    }

    // Métodos con DTOs
    public List<ContactoCorreoElectronicoDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<ContactoCorreoElectronico> contactos = listarActivos();
            return contactoCorreoElectronicoMapper.toDTOList(contactos);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar contactos de correo electrónico: " + e.getMessage());
        }
    }

    public Optional<ContactoCorreoElectronicoDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<ContactoCorreoElectronico> contacto = obtener(id);
            return contacto.map(contactoCorreoElectronicoMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener contacto de correo electrónico: " + e.getMessage());
        }
    }

    public ContactoCorreoElectronicoDTO altaDTO(ContactoCorreoElectronicoDTO contactoCorreoElectronicoDTO) throws ErrorServiceException {
        try {
            ContactoCorreoElectronico contacto = contactoCorreoElectronicoMapper.toEntity(contactoCorreoElectronicoDTO);
            ContactoCorreoElectronico contactoGuardado = alta(contacto);
            return contactoCorreoElectronicoMapper.toDTO(contactoGuardado);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear contacto de correo electrónico: " + e.getMessage());
        }
    }

    public Optional<ContactoCorreoElectronicoDTO> modificarDTO(String id, ContactoCorreoElectronicoDTO contactoCorreoElectronicoDTO) throws ErrorServiceException {
        try {
            ContactoCorreoElectronico contacto = contactoCorreoElectronicoMapper.toEntity(contactoCorreoElectronicoDTO);
            Optional<ContactoCorreoElectronico> contactoModificado = modificar(id, contacto);
            return contactoModificado.map(contactoCorreoElectronicoMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar contacto de correo electrónico: " + e.getMessage());
        }
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



