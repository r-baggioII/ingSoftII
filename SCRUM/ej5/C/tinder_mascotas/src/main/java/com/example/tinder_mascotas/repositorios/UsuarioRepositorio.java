package com.example.tinder_mascotas.repositorios;

import com.example.tinder_mascotas.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String>{

    @Query("SELECT u FROM Usuario u WHERE u.email = ?1")
    public Usuario buscarPorCorreo(String email);

}
