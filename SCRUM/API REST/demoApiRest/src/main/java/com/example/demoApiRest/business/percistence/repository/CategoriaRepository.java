package com.example.demoApiRest.business.percistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demoApiRest.business.domain.entity.Categoria;



public interface CategoriaRepository extends JpaRepository<Categoria, String> {

	@Query("SELECT c FROM Categoria c WHERE c.nombre = :nombre AND c.eliminado = FALSE")
	public Categoria buscarCategoriaPorNombre(@Param("nombre")String nombre);
	
	@Query("SELECT c FROM Categoria c WHERE c.eliminado = FALSE")
	public List<Categoria> listarCategoriaActiva();
	
}
