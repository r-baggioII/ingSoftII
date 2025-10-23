package org.example.repository;

import org.example.entity.Medico;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MedicoRepository extends BaseRepository<Medico, String> {
    
    Optional<Medico> findByDocumento(String documento);
    
    boolean existsByDocumento(String documento);
}
