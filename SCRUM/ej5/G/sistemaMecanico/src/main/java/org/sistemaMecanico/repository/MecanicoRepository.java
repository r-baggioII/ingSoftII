package org.sistemaMecanico.repository;

import org.springframework.stereotype.Repository;
import org.sistemaMecanico.entity.Mecanico;

@Repository
public interface MecanicoRepository extends BaseRepository<Mecanico, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
    // Por ejemplo:
    // Optional<Mecanico> findByLegajoAndEliminadoIsFalse(String legajo);
}
