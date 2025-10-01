package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Promocion;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromocionRepositorio extends JpaRepository<Promocion, String> {

    List<Promocion> findByEliminadoFalseOrderByCreadoEnDesc();

    Optional<Promocion> findByIdAndEliminadoFalse(String id);

    List<Promocion> findByEliminadoFalseAndEnviadaFalseAndFechaEnvioPromocionLessThanEqual(LocalDateTime fecha);
}
