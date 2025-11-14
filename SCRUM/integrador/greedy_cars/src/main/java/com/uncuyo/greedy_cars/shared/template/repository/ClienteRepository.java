package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, String> {

    Cliente findByNombreAndApellido(String nombre, String apellido);

    @Query("SELECT c FROM Cliente c LEFT JOIN c.usuario u WHERE c.eliminado = false AND " +
           "(LOWER(c.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.apellido) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "(u IS NOT NULL AND LOWER(u.nombreUsuario) LIKE LOWER(CONCAT('%', :query, '%'))) OR " +
           "LOWER(c.numeroDocumento) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Cliente> searchByQuery(@Param("query") String query);

    @Query("SELECT c FROM Cliente c WHERE c.eliminado = false AND c.usuario.id = :usuarioId")
    Optional<Cliente> findActivoByUsuarioId(@Param("usuarioId") String usuarioId);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cliente c WHERE c.numeroDocumento = :numeroDocumento AND c.eliminado = false")
    boolean existsByNumeroDocumentoAndEliminadoIsFalse(@Param("numeroDocumento") String numeroDocumento);

    List<Cliente> findAllByEliminadoIsFalseAndRecibirPromocionesIsTrue();
}
