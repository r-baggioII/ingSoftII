package org.contactoEmpresa.repository;



import org.springframework.stereotype.Repository;
import org.contactoEmpresa.entity.Empresa;

@Repository
public interface EmpresaRepository extends BaseRepository<Empresa, String> {
    // List<Persona> findByNombreContainingAndEliminadoIsFalse(String nombre);
}
