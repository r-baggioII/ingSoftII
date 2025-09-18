package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.ValorCuota;
import com.example.greedy_gym.repositorios.ValorCuotaRepositorio;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class ValorCuotaServicio {

    private static final LocalDate MAX_DATE = LocalDate.of(9999, 12, 31);

    private final ValorCuotaRepositorio valorCuotaRepositorio;

    public ValorCuota crear(ValorCuota valorCuota) {
        validarValor(valorCuota.getValorCuota());
        validarTraslape(valorCuota.getFechaDesde(), valorCuota.getFechaHasta(), null);
        valorCuota.setEliminado(false);
        return valorCuotaRepositorio.save(valorCuota);
    }

    public ValorCuota actualizar(String id, ValorCuota cambios) {
        ValorCuota existente = valorCuotaRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ValorCuota no encontrado"));
        validarValor(cambios.getValorCuota());
        validarTraslape(cambios.getFechaDesde(), cambios.getFechaHasta(), id);
        existente.setFechaDesde(cambios.getFechaDesde());
        existente.setFechaHasta(cambios.getFechaHasta());
        existente.setValorCuota(cambios.getValorCuota());
        return valorCuotaRepositorio.save(existente);
    }

    public void eliminar(String id) {
        ValorCuota existente = valorCuotaRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ValorCuota no encontrado"));
        existente.setEliminado(true);
        valorCuotaRepositorio.save(existente);
    }

    @Transactional(readOnly = true)
    public ValorCuota buscarPorId(String id) {
        return valorCuotaRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ValorCuota no encontrado"));
    }

    @Transactional(readOnly = true)
    public Page<ValorCuota> listar(Pageable pageable) {
        return valorCuotaRepositorio.findByEliminadoFalse(pageable);
    }

    @Transactional(readOnly = true)
    public ValorCuota buscarVigente() {
        return valorCuotaRepositorio.findVigente(LocalDate.now())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No hay valor vigente"));
    }

    private void validarValor(Double valor) {
        if (valor == null || valor <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor de la cuota debe ser positivo");
        }
    }

    private void validarTraslape(LocalDate fechaDesde, LocalDate fechaHasta, String excludeId) {
        if (fechaDesde == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fechaDesde es obligatoria");
        }
        LocalDate hasta = fechaHasta != null ? fechaHasta : MAX_DATE;
        List<ValorCuota> traslapes = valorCuotaRepositorio.findOverlapping(fechaDesde, hasta, excludeId);
        if (!traslapes.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El rango de fechas se traslapa con otro registro");
        }
    }
}

