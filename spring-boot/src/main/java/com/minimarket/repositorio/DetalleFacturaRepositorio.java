package com.minimarket.repositorio;

import com.minimarket.modelo.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleFacturaRepositorio extends JpaRepository<DetalleFactura, Long> {
}
