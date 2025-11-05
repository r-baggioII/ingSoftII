package com.uncuyo.greedy_cars.shared.template.repository;

import org.springframework.stereotype.Repository;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends BaseRepository<Usuario, String> {
    //buscar usuario por nombre de usuario (ya lo tienes)
    Optional<Usuario> findByNombreUsuarioAndEliminadoIsFalse(String nombreUsuario);

    //Verificar si existe un usuario
    boolean existsByNombreUsuarioAndEliminadoIsFalse(String nombreUsuario);
}