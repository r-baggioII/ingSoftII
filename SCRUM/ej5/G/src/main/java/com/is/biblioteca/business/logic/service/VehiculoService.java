package com.is.biblioteca.business.logic.service;

import com.is.biblioteca.business.domain.entity.Vehiculo;
import com.is.biblioteca.business.persistence.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService extends BaseService<Vehiculo, String> {
    
    public VehiculoService(VehiculoRepository repository) {
        super(repository);
    }
}
