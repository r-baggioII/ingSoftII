package com.example.tinder_mascotas.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.tinder_mascotas.entidades.Foto;

public interface FotoRepositorio extends JpaRepository<Foto, String> {

}
