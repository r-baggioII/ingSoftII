package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, String> {

    Cliente findByNombreAndApellido(String nombre, String apellido);

}
