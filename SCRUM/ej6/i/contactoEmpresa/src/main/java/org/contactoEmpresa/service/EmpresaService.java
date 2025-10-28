package org.contactoEmpresa.service;

import org.contactoEmpresa.entity.Empresa;
import org.contactoEmpresa.repository.BaseRepository;
import org.contactoEmpresa.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService extends BaseService<Empresa, String> {

    @Autowired
    public EmpresaService(BaseRepository<Empresa, String> repository) {
        super(repository);
    }

    @Override
    protected void actualizarEntidad( entidadExistente,  entidadNueva) {
        if (entidadNueva.getNombre() != null) {
            entidadExistente.setNombre(entidadNueva.getNombre());
        }
    }
}
