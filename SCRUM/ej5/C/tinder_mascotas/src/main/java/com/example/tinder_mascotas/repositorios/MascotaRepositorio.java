package com.example.tinder_mascotas.repositorios;

import java.util.List;

import com.example.tinder_mascotas.entidades.Mascota;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface MascotaRepositorio extends JpaRepository<Mascota, String> {

    @Query("SELECT m FROM Mascota m WHERE m.usuario.id = ?1")
    public List<Mascota> buscarPorUsuario(String id);
    
}
