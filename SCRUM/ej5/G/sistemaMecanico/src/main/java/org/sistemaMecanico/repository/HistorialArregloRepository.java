package org.sistemaMecanico.repository;

import org.springframework.stereotype.Repository;
import org.sistemaMecanico.entity.HistorialArreglo;

@Repository
public interface HistorialArregloRepository extends BaseRepository<HistorialArreglo, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
    // Por ejemplo:
    // List<HistorialArreglo> findByVehiculoIdAndEliminadoIsFalse(String vehiculoId);
}
