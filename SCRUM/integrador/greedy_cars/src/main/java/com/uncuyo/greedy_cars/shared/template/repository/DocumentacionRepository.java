package com.uncuyo.greedy_cars.shared.template.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uncuyo.greedy_cars.shared.template.entity.Documentacion;
import com.uncuyo.greedy_cars.shared.template.entity.TipoDocumentacion;

import java.util.List;

public interface DocumentacionRepository extends BaseRepository<Documentacion, String> {
    
    @Query("SELECT d FROM Documentacion d WHERE d.tipoDocumentacion = :tipo AND d.eliminado = FALSE")
    List<Documentacion> buscarPorTipo(@Param("tipo") TipoDocumentacion tipo);
    
    @Query("SELECT d FROM Documentacion d WHERE d.nombreArchivo = :nombreArchivo AND d.eliminado = FALSE")
    Documentacion buscarPorNombreArchivo(@Param("nombreArchivo") String nombreArchivo);
    
    @Query("SELECT d FROM Documentacion d WHERE d.pathArchivo = :pathArchivo AND d.eliminado = FALSE")
    Documentacion buscarPorPath(@Param("pathArchivo") String pathArchivo);
}
