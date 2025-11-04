package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;
import java.util.Optional;

import com.uncuyo.greedy_cars.shared.template.dto.ContactoTelefonicoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoTelefonico;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.ContactoTelefonicoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoTelefonicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactoTelefonicoService extends BaseService<ContactoTelefonico, String> {

    private final ContactoTelefonicoMapper contactoTelefonicoMapper;

    @Autowired
    public ContactoTelefonicoService(ContactoTelefonicoRepository repository, ContactoTelefonicoMapper contactoTelefonicoMapper) {
        super(repository);
        this.contactoTelefonicoMapper = contactoTelefonicoMapper;
    }

    // Métodos con DTOs
    public List<ContactoTelefonicoDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<ContactoTelefonico> contactos = listarActivos();
            return contactoTelefonicoMapper.toDTOList(contactos);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar contactos telefónicos: " + e.getMessage());
        }
    }

    public Optional<ContactoTelefonicoDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<ContactoTelefonico> contacto = obtener(id);
            return contacto.map(contactoTelefonicoMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener contacto telefónico: " + e.getMessage());
        }
    }

    public ContactoTelefonicoDTO altaDTO(ContactoTelefonicoDTO contactoTelefonicoDTO) throws ErrorServiceException {
        try {
            ContactoTelefonico contacto = contactoTelefonicoMapper.toEntity(contactoTelefonicoDTO);
            ContactoTelefonico contactoGuardado = alta(contacto);
            return contactoTelefonicoMapper.toDTO(contactoGuardado);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear contacto telefónico: " + e.getMessage());
        }
    }

    public Optional<ContactoTelefonicoDTO> modificarDTO(String id, ContactoTelefonicoDTO contactoTelefonicoDTO) throws ErrorServiceException {
        try {
            ContactoTelefonico contacto = contactoTelefonicoMapper.toEntity(contactoTelefonicoDTO);
            Optional<ContactoTelefonico> contactoModificado = modificar(id, contacto);
            return contactoModificado.map(contactoTelefonicoMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar contacto telefónico: " + e.getMessage());
        }
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
