package com.is.biblioteca.business.logic.service;

import com.is.biblioteca.business.domain.entity.Persona;
import com.is.biblioteca.business.persistence.repository.PersonaRepository;
import org.springframework.stereotype.Service;

@Service
public class PersonaService extends BaseService<Persona, String> {
    
    public PersonaService(PersonaRepository repository) {
        super(repository);
    }
}
