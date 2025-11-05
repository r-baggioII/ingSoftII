package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.FormaDePago;
import com.uncuyo.greedy_cars.shared.template.enums.TipoPago;
import java.util.List;
import java.util.Optional;

public interface FormaDePagoRepository extends BaseRepository<FormaDePago, String> {

    List<FormaDePago> findAllByFacturaIdAndEliminadoIsFalse(String facturaId);

    Optional<FormaDePago> findFirstByFacturaIdAndTipoPagoAndEliminadoIsFalse(String facturaId, TipoPago tipoPago);
}

