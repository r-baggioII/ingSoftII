package org.contactoEmpresa.repository;

import org.springframework.stereotype.Repository;
import org.contactoEmpresa.entity.ContactoCorreoElectronico;

@Repository
public interface ContactoCorreoElectronicoRepository extends BaseRepository<ContactoCorreoElectronico, String> {
    // Puedes agregar métodos de consulta personalizados aquí si es necesario
}
