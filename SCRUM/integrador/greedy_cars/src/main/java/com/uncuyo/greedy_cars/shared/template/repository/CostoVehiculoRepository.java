package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.CostoVehiculo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CostoVehiculoRepository extends BaseRepository<CostoVehiculo, String> {

    List<CostoVehiculo> findByCaracteristicaVehiculoIdAndEliminadoIsFalse(String caracteristicaId);

    @Query("SELECT c FROM CostoVehiculo c WHERE c.caracteristicaVehiculo.id = :caracId AND c.eliminado = false " +
            "AND :hoy BETWEEN c.fechaDesde AND c.fechaHasta")
    Optional<CostoVehiculo> findVigenteByCaracteristicaAndDate(@Param("caracId") String caracteristicaId, @Param("hoy") LocalDate hoy);
}
