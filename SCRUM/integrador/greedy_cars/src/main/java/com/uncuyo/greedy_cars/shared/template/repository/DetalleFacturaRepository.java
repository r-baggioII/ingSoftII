package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.DetalleFactura;
import java.util.List;

public interface DetalleFacturaRepository extends BaseRepository<DetalleFactura, String> {

    List<DetalleFactura> findAllByFacturaIdAndEliminadoIsFalse(String facturaId);
}

