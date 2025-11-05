package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoFactura;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;

public interface FacturaRepository extends BaseRepository<Factura, String> {

    Optional<Factura> findByNumeroFacturaAndEliminadoIsFalse(Long numeroFactura);

    List<Factura> findAllByEstadoAndEliminadoIsFalse(EstadoFactura estado);

    @Query("select coalesce(max(f.numeroFactura), 0) from Factura f where f.eliminado = false")
    Long obtenerMaxNumeroFactura();
}

