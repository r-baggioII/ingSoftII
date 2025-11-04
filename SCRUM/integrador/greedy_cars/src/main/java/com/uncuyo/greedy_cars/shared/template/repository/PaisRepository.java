package com.uncuyo.greedy_cars.shared.template.repository;


import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uncuyo.greedy_cars.shared.template.entity.Pais;

public interface PaisRepository extends BaseRepository<Pais, Long> {
	
	@Query("SELECT p "
		 + "  FROM Pais p "
		 + " WHERE p.nombre = :nombre "
		 + "   AND p.eliminado = FALSE")
	public Pais buscarPaisPorNombre(@Param("nombre")String nombre);
}







