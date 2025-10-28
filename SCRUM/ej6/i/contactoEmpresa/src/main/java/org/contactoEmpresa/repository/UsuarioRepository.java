package org.contactoEmpresa.repository;

import org.springframework.stereotype.Repository;
import org.contactoEmpresa.entity.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, String> {
    // Método para buscar usuario por nombre de usuario
    Optional<Usuario> findByNombreUsuarioAndEliminadoIsFalse(String nombreUsuario);

    //NUEVO: Verificar si existe un usuario
    boolean existsByNombreUsuarioAndEliminadoIsFalse(String nombreUsuario);
}