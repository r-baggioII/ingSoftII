package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByNombreUsuarioIgnoreCase(String nombreUsuario);

    Optional<Usuario> findByIdAndEliminadoFalse(String id);

    Optional<Usuario> findByNombreUsuarioAndEliminadoIsFalse(String username);
}
