package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Socio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocioRepositorio extends JpaRepository<Socio, String> {

    Optional<Socio> findByIdAndEliminadoFalse(String id);

    Optional<Socio> findByNumeroDocumentoAndEliminadoFalse(String numeroDocumento);

    Optional<Socio> findByCorreoElectronicoAndEliminadoFalse(String correoElectronico);

    Optional<Socio> findByNumeroSocioAndEliminadoFalse(Long numeroSocio);

    List<Socio> findAllByOrderByApellidoAscNombreAsc();

    List<Socio> findByEliminadoFalseOrderByApellidoAscNombreAsc();
}
