package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepositorio extends JpaRepository<Mensaje, String> {
}
