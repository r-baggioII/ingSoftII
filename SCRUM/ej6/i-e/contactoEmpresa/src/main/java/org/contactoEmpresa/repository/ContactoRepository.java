package org.contactoEmpresa.repository;

import org.springframework.stereotype.Repository;
import org.contactoEmpresa.entity.Contacto;

@Repository
public interface ContactoRepository extends BaseRepository<Contacto, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
    // Por ejemplo:
    // List<Persona> findByNombreContainingAndEliminadoIsFalse(String nombre);
}
