package com.uncuyo.greedy_cars.shared.template.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;

@Repository
public interface VehiculoRepository extends BaseRepository<Vehiculo, String> {

    // Método personalizado para buscar vehículo por patente
    Optional<Vehiculo> findByPatenteAndEliminadoIsFalse(String patente);

    @Override
    @EntityGraph(attributePaths = "caracteristicaVehiculo")
    Optional<Vehiculo> findByIdAndEliminadoIsFalse(String id);

    @Override
    @EntityGraph(attributePaths = "caracteristicaVehiculo")
    List<Vehiculo> findAllByEliminadoIsFalse();
}
