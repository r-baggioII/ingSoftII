package com.example.greedy_gym.repositorios;

import com.example.greedy_gym.entidades.Mensaje;
import com.example.greedy_gym.entidades.TipoMensaje;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepositorio extends JpaRepository<Mensaje, String> {

    List<Mensaje> findByEliminadoFalseOrderByCreadoEnDesc();

    Optional<Mensaje> findByIdAndEliminadoFalse(String id);

    Optional<Mensaje> findFirstByTipoMensajeAndEliminadoFalseOrderByActualizadoEnDesc(TipoMensaje tipoMensaje);
}
