package com.is.biblioteca.business.persistence.repository;

import com.is.biblioteca.business.domain.entity.Cliente;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends BaseRepository<Cliente, String> {
}
