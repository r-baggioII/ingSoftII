package com.gredy_cars_client.gredy_cars_client.shared.template.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.gredy_cars_client.gredy_cars_client.shared.template.dto.CostoVehiculoDTO;

/**
 * Disabled smoke-test kept as documentation to verify the remote API wiring when needed.
 * Uncomment @Disabled to run manually when debugging connectivity issues.
 */
@SpringBootTest
@Disabled("Manual smoke test, enable to validate remote costos API wiring")
class GestionCostosControllerIT {

    @Autowired
    private GestionCostosController controller;

    @Test
    void shouldLoadCostosFromRemoteApi() {
        Model model = new ExtendedModelMap();
        String view = controller.gestionarCostos(null, null, model);

        assertThat(view).isEqualTo("gestion/gestion-costos");
        assertThat(model.containsAttribute("costos")).isTrue();

        Object costosAttr = model.getAttribute("costos");
        assertThat(costosAttr).isInstanceOf(List.class);
        List<?> costos = (List<?>) costosAttr;
        assertThat(costos).isNotEmpty();
        assertThat(costos.get(0)).isInstanceOf(CostoVehiculoDTO.class);
    }
}
