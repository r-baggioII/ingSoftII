package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Persona;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.greedy_empresa.repositorios.BaseRepository;

public interface PersonaRepository extends BaseRepository<Persona, String> {

    Page<Persona> findByEliminadoFalse(Pageable pageable);

    Page<Persona> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseAndEliminadoFalse(String nombre,
            String apellido, Pageable pageable);

    Optional<Persona> findByCorreoElectronicoIgnoreCase(String correoElectronico);

    Optional<Persona> findByIdAndEliminadoFalse(String id);

    long countByEliminadoFalse();
}
