package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Usuario;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByUsernameIgnoreCase(String username);

    Optional<Usuario> findByUsernameIgnoreCaseAndEliminadoFalse(String username);

    Page<Usuario> findByEliminadoFalse(Pageable pageable);

    Page<Usuario> findByUsernameContainingIgnoreCaseAndEliminadoFalse(String username, Pageable pageable);

    long countByEliminadoFalse();
}
