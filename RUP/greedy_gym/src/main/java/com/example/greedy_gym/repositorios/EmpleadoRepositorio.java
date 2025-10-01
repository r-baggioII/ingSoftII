package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Empleado;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpleadoRepositorio extends JpaRepository<Empleado, String> {

    Optional<Empleado> findByIdAndEliminadoFalse(String id);

    Optional<Empleado> findByNumeroDocumentoAndEliminadoFalse(String numeroDocumento);

    Optional<Empleado> findByCorreoElectronicoAndEliminadoFalse(String correoElectronico);

    Optional<Empleado> findByUsuario_IdAndEliminadoFalse(String usuarioId);

    List<Empleado> findAllByOrderByApellidoAscNombreAsc();

    List<Empleado> findByEliminadoFalseOrderByApellidoAscNombreAsc();
}
