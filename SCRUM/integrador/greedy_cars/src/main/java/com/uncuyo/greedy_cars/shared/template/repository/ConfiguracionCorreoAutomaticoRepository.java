package com.uncuyo.greedy_cars.shared.template.repository;

import com.uncuyo.greedy_cars.shared.template.entity.ConfiguracionCorreoAutomatico;
import java.util.List;
import java.util.Optional;

public interface ConfiguracionCorreoAutomaticoRepository
        extends BaseRepository<ConfiguracionCorreoAutomatico, String> {

    List<ConfiguracionCorreoAutomatico> findAllByEmpresaIdAndEliminadoIsFalse(String empresaId);

    Optional<ConfiguracionCorreoAutomatico> findFirstByEmpresaIdAndEliminadoIsFalse(String empresaId);
}
