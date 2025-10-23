package com.is.biblioteca.business.persistence.repository;

import com.is.biblioteca.business.domain.entity.Persona;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends BaseRepository<Persona, String> {
}
