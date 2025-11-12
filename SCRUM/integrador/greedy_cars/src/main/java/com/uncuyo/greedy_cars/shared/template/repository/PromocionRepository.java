package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Promocion;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PromocionRepository extends BaseRepository<Promocion, String> {

    Optional<Promocion> findByCodigoDescuentoIgnoreCaseAndEliminadoIsFalse(String codigoDescuento);

    @Query("""
        SELECT p FROM Promocion p
        WHERE LOWER(p.codigoDescuento) = LOWER(:codigo)
          AND p.eliminado = false
          AND (p.fechaInicioPromocion IS NULL OR p.fechaInicioPromocion <= :fechaReferencia)
          AND (p.fechaFinPromocion IS NULL OR p.fechaFinPromocion >= :fechaReferencia)
    """)
    Optional<Promocion> buscarVigentePorCodigo(
            @Param("codigo") String codigoDescuento,
            @Param("fechaReferencia") LocalDate fechaReferencia);

    @Query("""
        SELECT p FROM Promocion p
        WHERE p.eliminado = false
          AND (p.fechaInicioPromocion IS NULL OR p.fechaInicioPromocion <= :fechaReferencia)
          AND (p.fechaFinPromocion IS NULL OR p.fechaFinPromocion >= :fechaReferencia)
        ORDER BY p.fechaInicioPromocion DESC
    """)
    List<Promocion> findActivas(@Param("fechaReferencia") LocalDate fechaReferencia);
}
