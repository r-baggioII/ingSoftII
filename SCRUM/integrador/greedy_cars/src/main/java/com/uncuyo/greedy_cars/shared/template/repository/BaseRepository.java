package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.BaseEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Base repository exposing queries tailored for entities that implement the
 * soft-delete contract defined in {@link BaseEntity}.
 *
 * @param <T> concrete entity type
 * @param <ID> identifier type
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity<ID>, ID> extends JpaRepository<T, ID> {

    Optional<T> findByIdAndEliminadoIsFalse(ID id);

    List<T> findAllByEliminadoIsFalse();
}
