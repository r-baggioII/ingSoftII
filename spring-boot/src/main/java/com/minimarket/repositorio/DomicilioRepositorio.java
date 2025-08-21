package com.minimarket.repositorio;

import com.minimarket.modelo.Domicilio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DomicilioRepositorio extends JpaRepository<Domicilio, Long> {
}
