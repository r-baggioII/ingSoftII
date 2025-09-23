package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.EstadoFactura;
import com.example.greedy_gym.entidades.Factura;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacturaRepositorio extends JpaRepository<Factura, String> {

    Optional<Factura> findByIdAndEliminadoFalse(String id);

    Optional<Factura> findByNumeroFacturaAndEliminadoFalse(Long numeroFactura);

    @Query("select f from Factura f where f.eliminado=false and (:estado is null or f.estado=:estado)")
    Page<Factura> search(@Param("estado") EstadoFactura estado, Pageable pageable);

    @Query("select coalesce(max(f.numeroFactura), 0) from Factura f")
    Long obtenerMaxNumeroFactura();
}
