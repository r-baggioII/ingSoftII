package com.example.tinder_mascotas.repositorios;

import com.example.tinder_mascotas.entidades.Voto;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface VotoRepositorio extends JpaRepository<Voto, String>{

    @Query("SELECT v FROM Voto v WHERE v.mascota1.id = ?1 ORDER BY v.fecha DESC")
    public List<Voto> buscarVotosPropios(String id);

    @Query("SELECT v FROM Voto v WHERE v.mascota2 = ?1 ORDER BY v.fecha DESC")
    public List<Voto> buscarVotosRecibidos(String id);

}
