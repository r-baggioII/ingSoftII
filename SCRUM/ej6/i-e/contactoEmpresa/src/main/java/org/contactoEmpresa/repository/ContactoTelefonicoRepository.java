package org.contactoEmpresa.repository;

import org.springframework.stereotype.Repository;
import org.contactoEmpresa.entity.ContactoTelefonico;

@Repository
public interface ContactoTelefonicoRepository extends BaseRepository<ContactoTelefonico, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
}
