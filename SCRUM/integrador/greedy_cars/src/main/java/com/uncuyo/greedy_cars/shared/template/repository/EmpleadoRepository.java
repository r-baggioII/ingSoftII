package com.uncuyo.greedy_cars.shared.template.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uncuyo.greedy_cars.shared.template.entity.Empleado;

public interface EmpleadoRepository extends BaseRepository<Empleado, String> {

	@Query("SELECT e "
		+ "  FROM Empleado e "
		+ " WHERE e.nombre = :nombre "
		+ "   AND e.apellido = :apellido "
		+ "   AND e.eliminado = FALSE")
	Empleado findByNombreAndApellido(@Param("nombre") String nombre, @Param("apellido") String apellido);

}
