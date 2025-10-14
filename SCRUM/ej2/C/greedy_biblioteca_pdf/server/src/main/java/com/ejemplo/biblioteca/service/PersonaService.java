package com.ejemplo.biblioteca.service;

import com.ejemplo.biblioteca.domain.Domicilio;
import com.ejemplo.biblioteca.domain.Localidad;
import com.ejemplo.biblioteca.domain.Persona;
import com.ejemplo.biblioteca.repository.LocalidadRepository;
import com.ejemplo.biblioteca.repository.PersonaRepository;
import com.ejemplo.biblioteca.web.dto.DomicilioDTO;
import com.ejemplo.biblioteca.web.dto.DomicilioRequest;
import com.ejemplo.biblioteca.web.dto.LibroDTO;
import com.ejemplo.biblioteca.web.dto.LocalidadDTO;
import com.ejemplo.biblioteca.web.dto.PersonaDTO;
import com.ejemplo.biblioteca.web.dto.PersonaRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class PersonaService {

    private final PersonaRepository personaRepository;
    private final LocalidadRepository localidadRepository;
    private final LibroService libroService;

    @Transactional(readOnly = true)
    public Page<PersonaDTO> search(String apellido, Integer dni, Pageable pageable) {
        return personaRepository.search(
                        apellido == null || apellido.isBlank() ? null : apellido,
                        dni,
                        pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public PersonaDTO findById(Long id) {
        return personaRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
    }

    public PersonaDTO create(PersonaRequest request) {
        Persona persona = new Persona();
        applyValues(persona, request);
        return toDto(personaRepository.save(persona));
    }

    public PersonaDTO update(Long id, PersonaRequest request) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada"));
        applyValues(persona, request);
        return toDto(personaRepository.save(persona));
    }

    public void delete(Long id) {
        if (!personaRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada");
        }
        personaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<LibroDTO> findLibros(Long personaId, Pageable pageable) {
        return libroService.findByPersona(personaId, pageable);
    }

    private void applyValues(Persona persona, PersonaRequest request) {
        persona.setNombre(request.nombre());
        persona.setApellido(request.apellido());
        persona.setDni(request.dni());

        DomicilioRequest domicilioRequest = request.domicilio();
        Localidad localidad = localidadRepository.findById(domicilioRequest.localidadId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Localidad no encontrada"));

        Domicilio domicilio = persona.getDomicilio();
        if (domicilio == null) {
            domicilio = new Domicilio();
            persona.setDomicilio(domicilio);
        }
        domicilio.setCalle(domicilioRequest.calle());
        domicilio.setNumero(domicilioRequest.numero());
        domicilio.setLocalidad(localidad);
    }

    private PersonaDTO toDto(Persona persona) {
        Domicilio domicilio = persona.getDomicilio();
        Localidad localidad = domicilio.getLocalidad();
        LocalidadDTO localidadDTO = new LocalidadDTO(localidad.getId(), localidad.getDenominacion());
        DomicilioDTO domicilioDTO = new DomicilioDTO(
                domicilio.getId(),
                domicilio.getCalle(),
                domicilio.getNumero(),
                localidadDTO
        );
        return new PersonaDTO(
                persona.getId(),
                persona.getNombre(),
                persona.getApellido(),
                persona.getDni(),
                domicilioDTO
        );
    }
}
