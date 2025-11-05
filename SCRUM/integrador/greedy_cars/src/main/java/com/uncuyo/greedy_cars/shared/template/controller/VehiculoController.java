package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController extends BaseRestController<Vehiculo, String> {

    @Autowired
    public VehiculoController(VehiculoService vehiculoService) {
        super(vehiculoService);
        initController(new Vehiculo());
    }

    // Hereda:
    // GET /api/vehiculos -> listar
    // GET /api/vehiculos/{id} -> obtener
    // POST /api/vehiculos -> crear
    // PUT /api/vehiculos/{id} -> actualizar
    // DELETE /api/vehiculos/{id} -> eliminar
}
