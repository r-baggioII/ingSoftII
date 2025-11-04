package com.uncuyo.greedy_cars.shared.template.repository;


import org.springframework.stereotype.Repository;
import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;

import java.util.Optional;

@Repository
public interface VehiculoRepository extends BaseRepository<Vehiculo, String> {
    // Método personalizado para buscar vehículo por patente
    Optional<Vehiculo> findByPatenteAndEliminadoIsFalse(String patente);
}
