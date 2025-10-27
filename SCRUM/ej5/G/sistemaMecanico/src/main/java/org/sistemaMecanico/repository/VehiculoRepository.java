package org.sistemaMecanico.repository;

import org.springframework.stereotype.Repository;
import org.sistemaMecanico.entity.Vehiculo;

import java.util.Optional;

@Repository
public interface VehiculoRepository extends BaseRepository<Vehiculo, String> {
    // Método personalizado para buscar vehículo por patente
    Optional<Vehiculo> findByPatenteAndEliminadoIsFalse(String patente);
}
