package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Empresa;
import com.example.greedy_gym.entidades.Sucursal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SucursalRepositorio extends JpaRepository<Sucursal, String> {

    Optional<Sucursal> findByIdAndEliminadoFalse(String id);

    Optional<Sucursal> findByNombreIgnoreCaseAndEliminadoFalse(String nombre);

    Optional<Sucursal> findByNombreIgnoreCaseAndEmpresaAndEliminadoFalse(String nombre, Empresa empresa);

    List<Sucursal> findByEliminadoFalseOrderByNombreAsc();

    List<Sucursal> findAllByOrderByNombreAsc();
}

