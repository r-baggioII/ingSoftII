package com.uncuyo.greedy_cars.shared.template.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uncuyo.greedy_cars.shared.template.entity.Nacionalidad;

public interface NacionalidadRepository extends BaseRepository<Nacionalidad, String> {

    @Query("SELECT n FROM Nacionalidad n WHERE n.nombre = :nombre AND n.eliminado = FALSE")
    Nacionalidad buscarNacionalidadPorNombre(@Param("nombre") String nombre);

}
