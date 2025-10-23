package com.is.biblioteca.business.logic.service;

import com.is.biblioteca.business.domain.entity.HistorialArreglo;
import com.is.biblioteca.business.persistence.repository.HistorialArregloRepository;
import org.springframework.stereotype.Service;

@Service
public class HistorialArregloService extends BaseService<HistorialArreglo, String> {
    
    public HistorialArregloService(HistorialArregloRepository repository) {
        super(repository);
    }
}
