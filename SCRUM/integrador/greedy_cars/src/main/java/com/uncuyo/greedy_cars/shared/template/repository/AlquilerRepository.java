package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import java.util.List;
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

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
           "FROM Alquiler a " +
           "WHERE a.vehiculo.id = :vehiculoId " +
           "  AND a.eliminado = false " +
           "  AND a.fechaDesde <= :today " +
           "  AND a.fechaHasta >= :today")
    boolean tieneAlquilerActivo(@Param("vehiculoId") String vehiculoId,
                                @Param("today") java.time.LocalDate today);

    Optional<Alquiler> findFirstByVehiculoIdAndEliminadoIsFalseOrderByFechaHastaDesc(String vehiculoId);

    List<Alquiler> findAllByClienteIdAndEliminadoIsFalse(String clienteId);

    @Query("""
        SELECT a FROM Alquiler a
        WHERE a.cliente.id = :clienteId
          AND a.eliminado = false
          AND NOT EXISTS (
              SELECT 1 FROM DetalleFactura df
              WHERE df.alquiler.id = a.id
                AND df.eliminado = false
          )
    """)
    List<Alquiler> findPendientesFacturaPorCliente(@Param("clienteId") String clienteId);

    List<Alquiler> findByFechaDesdeBetweenAndEliminadoIsFalse(
        java.time.LocalDate inicio, java.time.LocalDate fin);

    @Query("SELECT a FROM Alquiler a WHERE " +
           "a.vehiculo.id = :vehiculoId AND " +
           "a.fechaDesde BETWEEN :inicio AND :fin AND " +
           "a.eliminado = false")
    List<Alquiler> findByVehiculoIdAndFechaDesdeBetweenAndEliminadoIsFalse(
        @Param("vehiculoId") String vehiculoId,
        @Param("inicio") java.time.LocalDate inicio,
        @Param("fin") java.time.LocalDate fin);
}
