package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.Usuario;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.greedy_empresa.repositorios.BaseRepository;

public interface UsuarioRepository extends BaseRepository<Usuario, String> {

    Optional<Usuario> findByUsernameIgnoreCase(String username);

    Optional<Usuario> findByUsernameIgnoreCaseAndEliminadoFalse(String username);

    Page<Usuario> findByEliminadoFalse(Pageable pageable);

    Page<Usuario> findByUsernameContainingIgnoreCaseAndEliminadoFalse(String username, Pageable pageable);

    long countByEliminadoFalse();
}
