package com.example.demoApiRest.business.percistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demoApiRest.business.domain.entity.Producto;



public interface ProductoRepository extends JpaRepository<Producto, String> {

	@Query("SELECT p FROM Producto p WHERE p.nombre = :nombre AND p.eliminado = FALSE")
	public Producto buscarProductoPorNombre(@Param("nombre")String nombre);
	
	@Query("SELECT p FROM Producto p WHERE p.eliminado = FALSE")
	public List<Producto> listarProductoActivo();
	
	@Query("SELECT p FROM Producto p WHERE p.marca.id = :idMarca AND p.eliminado = FALSE")
	public List<Producto> listarProductoPorMarcaActivo(@Param("idMarca")String idMarca);
	
	@Query("SELECT p FROM Producto p WHERE p.categoria.id = :idCategoria AND p.eliminado = FALSE")
	public List<Producto> listarProductoPorCategoriaActivo(@Param("idCategoria")String idCategoria);
	
	@Query("SELECT p FROM Producto p WHERE p.categoria.id = :idCategoria AND p.marca.id = :idMarca AND p.eliminado = FALSE")
	public List<Producto> listarProductoPorCategoriaYMarcaActivo(@Param("idCategoria")String idCategoria, @Param("idMarca")String idMarca);
}
