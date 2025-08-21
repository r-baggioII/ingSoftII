package com.minimarket.repositorio;

import com.minimarket.modelo.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
    Page<Cliente> findByApellidoContainingIgnoreCase(String texto, Pageable pageable);
}
