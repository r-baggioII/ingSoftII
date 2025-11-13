package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.Recordatorio;
import com.uncuyo.greedy_cars.shared.template.enums.TipoRecordatorio;
import java.util.Optional;

public interface RecordatorioRepository extends BaseRepository<Recordatorio, String> {

    boolean existsByAlquilerIdAndTipoRecordatorioAndEliminadoIsFalse(String alquilerId, TipoRecordatorio tipo);

    Optional<Recordatorio> findTopByAlquilerIdAndTipoRecordatorioAndEliminadoIsFalseOrderByFechaEnvioDesc(
        String alquilerId,
        TipoRecordatorio tipoRecordatorio
    );
}
