package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Socio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SocioRepositorio extends JpaRepository<Socio, String> {

    Optional<Socio> findByIdAndEliminadoFalse(String id);

    Optional<Socio> findByNumeroDocumentoAndEliminadoFalse(String numeroDocumento);

    Optional<Socio> findByCorreoElectronicoAndEliminadoFalse(String correoElectronico);

    Optional<Socio> findByNumeroSocioAndEliminadoFalse(Long numeroSocio);

    Optional<Socio> findByUsuario_IdAndEliminadoFalse(String usuarioId);

    List<Socio> findAllByOrderByApellidoAscNombreAsc();

    List<Socio> findByEliminadoFalseOrderByApellidoAscNombreAsc();

    @Query("select coalesce(max(s.numeroSocio), 0) from Socio s")
    Long obtenerMaxNumeroSocio();
}
