package com.uncuyo.greedy_cars.shared.template.repository;

import org.springframework.stereotype.Repository;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoCorreoElectronico;

@Repository
public interface ContactoCorreoElectronicoRepository extends BaseRepository<ContactoCorreoElectronico, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
}
