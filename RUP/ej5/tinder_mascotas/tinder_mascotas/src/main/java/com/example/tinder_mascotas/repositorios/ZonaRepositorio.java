package com.example.tinder_mascotas.repositorios;

import com.example.tinder_mascotas.entidades.Zona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonaRepositorio extends JpaRepository<Zona, String>{
    
}
