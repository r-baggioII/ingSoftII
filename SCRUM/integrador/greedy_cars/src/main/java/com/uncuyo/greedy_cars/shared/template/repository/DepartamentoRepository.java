package com.uncuyo.greedy_cars.shared.template.repository;


import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uncuyo.greedy_cars.shared.template.entity.Departamento;

public interface DepartamentoRepository extends BaseRepository<Departamento, Long> {
	
	@Query("SELECT p "
		 + "  FROM Departamento p"
		 + " WHERE p.nombre = :nombre"
		 + "   AND p.eliminado = FALSE")
	public Departamento buscarDepartamentoPorNombre (@Param("nombre")String nombre);
		
    @Query("SELECT p "
	     + "  FROM Departamento p"
	     + " WHERE p.provincia.id = :idProvincia"
	     + "   AND p.nombre = :nombre"
	     + "   AND p.eliminado = FALSE")
    public Departamento buscarDepartamentoPorProvinciaYNombre (@Param("idProvincia")Long idProvincia, @Param("nombre")String nombre);

    @Query("SELECT p "
	     + "  FROM Departamento p"
	     + " WHERE p.eliminado = FALSE"
	     + "   AND p.provincia.id = :idProvincia")		
    public List<Departamento> listarDepartamentoActivo(@Param("idProvincia") Long idProvincia);
}







