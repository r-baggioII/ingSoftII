package com.example.greedy_empresa.repositorios;

import com.example.greedy_empresa.entidades.BaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {
    Page<T> findByEliminadoFalse(Pageable pageable);
}