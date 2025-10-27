package org.sistemaMecanico.repository;

import org.springframework.stereotype.Repository;
import org.sistemaMecanico.entity.Cliente;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
    // Por ejemplo:
    // Optional<Cliente> findByDocumentoAndEliminadoIsFalse(String documento);
}
