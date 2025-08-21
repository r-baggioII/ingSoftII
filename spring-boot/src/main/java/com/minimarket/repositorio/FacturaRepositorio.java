package com.minimarket.repositorio;

import com.minimarket.modelo.Factura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturaRepositorio extends JpaRepository<Factura, Long> {
}
