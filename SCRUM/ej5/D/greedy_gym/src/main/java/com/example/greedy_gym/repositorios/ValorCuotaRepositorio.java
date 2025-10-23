package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.ValorCuota;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ValorCuotaRepositorio extends JpaRepository<ValorCuota, String> {

    Page<ValorCuota> findByEliminadoFalse(Pageable pageable);

    Optional<ValorCuota> findByIdAndEliminadoFalse(String id);

    @Query("""
            SELECT vc FROM ValorCuota vc
            WHERE vc.eliminado = false
              AND ((vc.fechaHasta IS NULL AND vc.fechaDesde <= :today)
                OR (:today BETWEEN vc.fechaDesde AND vc.fechaHasta))
            ORDER BY vc.fechaDesde DESC
            """)
    Optional<ValorCuota> findVigente(@Param("today") LocalDate today);

    @Query("""
            SELECT vc FROM ValorCuota vc
            WHERE vc.eliminado = false
              AND (:excludeId IS NULL OR vc.id <> :excludeId)
              AND vc.fechaDesde <= :fechaHasta
              AND (vc.fechaHasta IS NULL OR vc.fechaHasta >= :fechaDesde)
            """)
    List<ValorCuota> findOverlapping(@Param("fechaDesde") LocalDate fechaDesde,
                                     @Param("fechaHasta") LocalDate fechaHasta,
                                     @Param("excludeId") String excludeId);
}

