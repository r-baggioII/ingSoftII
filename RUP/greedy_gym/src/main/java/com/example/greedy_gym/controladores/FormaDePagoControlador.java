package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.FormaDePago;
import com.example.greedy_gym.servicios.FormaDePagoServicio;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/formas-pago")
@CrossOrigin(origins = "*")
@Validated
public class FormaDePagoControlador {

    private final FormaDePagoServicio formaDePagoServicio;

    public FormaDePagoControlador(FormaDePagoServicio formaDePagoServicio) {
        this.formaDePagoServicio = formaDePagoServicio;
    }

    @PostMapping
    public ResponseEntity<FormaDePago> crear(@Valid @RequestBody FormaDePago formaDePago) {
        FormaDePago creada = formaDePagoServicio.crear(formaDePago);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormaDePago> actualizar(@PathVariable String id,
                                                  @RequestBody FormaDePago formaDePago) {
        FormaDePago actualizada = formaDePagoServicio.actualizar(id, formaDePago);
        return ResponseEntity.ok(actualizada);
    }

    @GetMapping
    public ResponseEntity<List<FormaDePago>> listar() {
        return ResponseEntity.ok(formaDePagoServicio.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormaDePago> buscar(@PathVariable String id) {
        return ResponseEntity.ok(formaDePagoServicio.buscar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        formaDePagoServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
