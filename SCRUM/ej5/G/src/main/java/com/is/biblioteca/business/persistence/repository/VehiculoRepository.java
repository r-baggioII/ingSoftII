package com.is.biblioteca.business.persistence.repository;

import com.is.biblioteca.business.domain.entity.Vehiculo;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepository extends BaseRepository<Vehiculo, String> {
}
