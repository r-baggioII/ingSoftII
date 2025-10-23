package com.is.biblioteca.business.logic.service;

import com.is.biblioteca.business.domain.entity.Mecanico;
import com.is.biblioteca.business.persistence.repository.MecanicoRepository;
import org.springframework.stereotype.Service;

@Service
public class MecanicoService extends BaseService<Mecanico, String> {
    
    public MecanicoService(MecanicoRepository repository) {
        super(repository);
    }
}
