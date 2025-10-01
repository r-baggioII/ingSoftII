package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.FormaDePago;
import com.example.greedy_gym.entidades.TipoPago;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormaDePagoRepositorio extends JpaRepository<FormaDePago, String> {

    List<FormaDePago> findByEliminadoFalse();

    Optional<FormaDePago> findByIdAndEliminadoFalse(String id);

    Optional<FormaDePago> findFirstByTipoPagoAndEliminadoFalse(TipoPago tipoPago);
}
