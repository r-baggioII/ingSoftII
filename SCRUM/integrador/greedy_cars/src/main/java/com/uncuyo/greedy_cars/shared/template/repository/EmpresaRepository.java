package com.uncuyo.greedy_cars.shared.template.repository;



import org.springframework.stereotype.Repository;

import com.uncuyo.greedy_cars.shared.template.entity.Empresa;


@Repository
public interface EmpresaRepository extends BaseRepository<Empresa, String> {
    // List<Persona> findByNombreContainingAndEliminadoIsFalse(String nombre);
}
