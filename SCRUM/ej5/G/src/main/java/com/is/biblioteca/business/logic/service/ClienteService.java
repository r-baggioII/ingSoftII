package com.is.biblioteca.business.logic.service;

import com.is.biblioteca.business.domain.entity.Cliente;
import com.is.biblioteca.business.persistence.repository.ClienteRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService extends BaseService<Cliente, String> {
    
    public ClienteService(ClienteRepository repository) {
        super(repository);
    }
}
