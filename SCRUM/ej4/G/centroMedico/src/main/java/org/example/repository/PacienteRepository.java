package org.example.repository;

import org.example.entity.Paciente;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PacienteRepository extends BaseRepository<Paciente, String> {
    
    Optional<Paciente> findByDocumento(String documento);
    
    boolean existsByDocumento(String documento);
}
