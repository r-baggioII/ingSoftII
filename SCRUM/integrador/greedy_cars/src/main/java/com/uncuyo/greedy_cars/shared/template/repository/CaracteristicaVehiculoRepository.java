package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaracteristicaVehiculoRepository extends BaseRepository<CaracteristicaVehiculo, String> {

    List<CaracteristicaVehiculo> findByMarcaContainingAndEliminadoIsFalse(String marca);

    List<CaracteristicaVehiculo> findByModeloContainingAndEliminadoIsFalse(String modelo);
}
