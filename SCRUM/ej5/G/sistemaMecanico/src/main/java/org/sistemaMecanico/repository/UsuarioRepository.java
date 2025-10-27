package org.sistemaMecanico.repository;

import org.springframework.stereotype.Repository;
import org.sistemaMecanico.entity.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, String> {
    // Método personalizado para buscar usuario por nombre de usuario
    Optional<Usuario> findByNombreUsuarioAndEliminadoIsFalse(String nombreUsuario);
}
