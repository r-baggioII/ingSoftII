package org.sistemaMecanico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.sistemaMecanico.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>, ID> extends JpaRepository<T, ID> {
    Optional<T> findByIdAndEliminadoIsFalse(ID id);

    List<T> findAllByEliminadoIsFalse();
}

