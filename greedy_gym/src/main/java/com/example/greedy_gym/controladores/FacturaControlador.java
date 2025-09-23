package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.DetalleFactura;
import com.example.greedy_gym.entidades.EstadoFactura;
import com.example.greedy_gym.entidades.Factura;
import com.example.greedy_gym.servicios.FacturaServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
@Validated
public class FacturaControlador {

    private final FacturaServicio facturaServicio;

    public FacturaControlador(FacturaServicio facturaServicio) {
        this.facturaServicio = facturaServicio;
    }

    @PostMapping
    public ResponseEntity<Factura> crear(@Valid @RequestBody Factura factura) {
        Factura creada = facturaServicio.crear(factura);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Factura> actualizar(@PathVariable String id,
                                              @RequestBody Factura factura) {
        Factura actualizada = facturaServicio.actualizar(id, factura);
        return ResponseEntity.ok(actualizada);
    }

    @GetMapping
    public ResponseEntity<Page<Factura>> listar(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int size,
                                                @RequestParam(required = false) String sort,
                                                @RequestParam(required = false) EstadoFactura estado) {
        Page<Factura> result = facturaServicio.listar(estado, page, size, sort);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> buscar(@PathVariable String id) {
        return ResponseEntity.ok(facturaServicio.buscar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        facturaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetalleFactura>> listarDetalles(@PathVariable String id) {
        return ResponseEntity.ok(facturaServicio.listarDetalles(id));
    }

    @PostMapping("/{id}/detalles")
    public ResponseEntity<DetalleFactura> agregarDetalle(@PathVariable String id,
                                                          @RequestBody DetalleFactura detalle) {
        DetalleFactura creado = facturaServicio.agregarDetalle(id, detalle);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @DeleteMapping("/detalles/{detalleId}")
    public ResponseEntity<Void> eliminarDetalle(@PathVariable String detalleId) {
        facturaServicio.eliminarDetalle(detalleId);
        return ResponseEntity.noContent().build();
    }
}
