package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Persona;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, String> {

    Page<Persona> findByEliminadoFalse(Pageable pageable);

    Page<Persona> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCaseAndEliminadoFalse(String nombre,
            String apellido, Pageable pageable);

    Optional<Persona> findByCorreoElectronicoIgnoreCase(String correoElectronico);

    Optional<Persona> findByUsuario_Id(String usuarioId);
}
