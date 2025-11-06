package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlquilerRepository extends BaseRepository<Alquiler, String> {

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Alquiler a " +
           "WHERE a.vehiculo.id = :vehiculoId " +
           "  AND a.eliminado = false " +
           "  AND (a.fechaDesde <= :fechaHasta AND a.fechaHasta >= :fechaDesde)")
    boolean existeTraslapeParaVehiculo(@Param("vehiculoId") String vehiculoId,
                                       @Param("fechaDesde") java.time.LocalDate fechaDesde,
                                       @Param("fechaHasta") java.time.LocalDate fechaHasta);

    Optional<Alquiler> findFirstByVehiculoIdAndEliminadoIsFalseOrderByFechaHastaDesc(String vehiculoId);
}
