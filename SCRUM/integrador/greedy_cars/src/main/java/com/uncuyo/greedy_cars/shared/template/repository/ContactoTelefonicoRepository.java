package com.uncuyo.greedy_cars.shared.template.repository;

import org.springframework.stereotype.Repository;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoTelefonico;

@Repository
public interface ContactoTelefonicoRepository extends BaseRepository<ContactoTelefonico, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
}
