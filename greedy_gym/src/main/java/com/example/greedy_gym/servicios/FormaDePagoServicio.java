package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.FormaDePago;
import com.example.greedy_gym.entidades.TipoPago;
import com.example.greedy_gym.repositorios.FormaDePagoRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FormaDePagoServicio {

    private final FormaDePagoRepositorio formaDePagoRepositorio;

    public FormaDePagoServicio(FormaDePagoRepositorio formaDePagoRepositorio) {
        this.formaDePagoRepositorio = formaDePagoRepositorio;
    }

    public FormaDePago crear(FormaDePago formaDePago) {
        validarCrear(formaDePago);
        formaDePago.setId(null); // aseguramos nuevo registro
        formaDePago.setEliminado(false);
        return formaDePagoRepositorio.save(formaDePago);
    }

    public FormaDePago actualizar(String id, FormaDePago formaDePago) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("El id es obligatorio");
        }
        FormaDePago existente = formaDePagoRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Forma de pago no encontrada"));

        if (formaDePago.getTipoPago() != null) {
            existente.setTipoPago(formaDePago.getTipoPago());
        }
        if (formaDePago.getObservacion() != null) {
            existente.setObservacion(formaDePago.getObservacion());
        }
        return formaDePagoRepositorio.save(existente);
    }

    public void eliminar(String id) {
        FormaDePago existente = formaDePagoRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Forma de pago no encontrada"));
        existente.setEliminado(true);
        formaDePagoRepositorio.save(existente);
    }

    @Transactional(readOnly = true)
    public List<FormaDePago> listar() {
        return formaDePagoRepositorio.findByEliminadoFalse();
    }

    @Transactional(readOnly = true)
    public FormaDePago buscar(String id) {
        return formaDePagoRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Forma de pago no encontrada"));
    }

    private void validarCrear(FormaDePago formaDePago) {
        if (formaDePago == null) {
            throw new ValidationException("Los datos de forma de pago son obligatorios");
        }
        if (formaDePago.getTipoPago() == null) {
            throw new ValidationException("El tipo de pago es obligatorio");
        }
        if (!esTipoPagoValido(formaDePago.getTipoPago())) {
            throw new ValidationException("Tipo de pago inválido");
        }
    }

    private boolean esTipoPagoValido(TipoPago tipoPago) {
        for (TipoPago value : TipoPago.values()) {
            if (value == tipoPago) {
                return true;
            }
        }
        return false;
    }
}
