package com.example.tinder_mascotas.repositorios;

import com.example.tinder_mascotas.entidades.Voto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepositorio extends JpaRepository<Voto, String> {

    @Query("SELECT v FROM Voto v WHERE v.mascota1.id = ?1 ORDER BY v.fecha DESC")
    List<Voto> buscarVotosPropios(String idMascota1);

    // FIX: comparar por id
    @Query("SELECT v FROM Voto v WHERE v.mascota2.id = ?1 ORDER BY v.fecha DESC")
    List<Voto> buscarVotosRecibidos(String idMascota2);
}
