package com.example.demoApiRest.business.percistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demoApiRest.business.domain.entity.Marca;
import com.example.demoApiRest.business.domain.entity.Producto;



public interface MarcaRepository extends JpaRepository<Marca, String> {

	@Query("SELECT m FROM Marca m WHERE m.nombre = :nombre AND m.eliminado = FALSE")
	public Marca buscarMarcaPorNombre(@Param("nombre")String nombre);
	
	@Query("SELECT m FROM Marca m WHERE m.eliminado = FALSE")
	public List<Marca> listarMarcaActiva();
	
}
