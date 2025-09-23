package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.DetalleFactura;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleFacturaRepositorio extends JpaRepository<DetalleFactura, String> {

    List<DetalleFactura> findByFacturaIdAndEliminadoFalse(String facturaId);

    Optional<DetalleFactura> findByIdAndEliminadoFalse(String id);
}
