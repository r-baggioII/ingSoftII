package org.contactoEmpresa.repository;

import org.springframework.stereotype.Repository;
import org.contactoEmpresa.entity.Persona;

@Repository
public interface PersonaRepository extends BaseRepository<Persona, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
    // Por ejemplo:
    // List<Persona> findByNombreContainingAndEliminadoIsFalse(String nombre);
}
