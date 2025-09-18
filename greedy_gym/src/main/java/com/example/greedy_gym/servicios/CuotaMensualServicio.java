package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.CuotaMensual;
import com.example.greedy_gym.entidades.EstadoCuota;
import com.example.greedy_gym.entidades.Mes;
import com.example.greedy_gym.entidades.ValorCuota;
import com.example.greedy_gym.repositorios.CuotaMensualRepositorio;
import com.example.greedy_gym.repositorios.ValorCuotaRepositorio;
import java.time.LocalDate;
import java.time.YearMonth;
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
public class CuotaMensualServicio {

    private final CuotaMensualRepositorio cuotaMensualRepositorio;
    private final ValorCuotaRepositorio valorCuotaRepositorio;

    public CuotaMensual crear(CuotaMensual cuotaMensual) {
        if (cuotaMensual.getValorCuota() == null || cuotaMensual.getValorCuota().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el valor de cuota");
        }
        if (cuotaMensualRepositorio.existsByIdSocioAndMesAndAnioAndEliminadoFalse(
                cuotaMensual.getIdSocio(), cuotaMensual.getMes(), cuotaMensual.getAnio())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuota para el socio en ese período");
        }
        ValorCuota valorCuota = valorCuotaRepositorio.findByIdAndEliminadoFalse(cuotaMensual.getValorCuota().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor de cuota inválido"));
        validarFechaVencimiento(cuotaMensual.getFechaVencimiento(), cuotaMensual.getMes(), cuotaMensual.getAnio());
        cuotaMensual.setValorCuota(valorCuota);
        cuotaMensual.setEstado(EstadoCuota.PENDIENTE);
        cuotaMensual.setEliminado(false);
        return cuotaMensualRepositorio.save(cuotaMensual);
    }

    public CuotaMensual actualizar(String id, CuotaMensual cambios) {
        CuotaMensual existente = cuotaMensualRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuota mensual no encontrada"));
        if (cambios.getFechaVencimiento() != null) {
            validarFechaVencimiento(cambios.getFechaVencimiento(), existente.getMes(), existente.getAnio());
            existente.setFechaVencimiento(cambios.getFechaVencimiento());
        }
        if (cambios.getEstado() != null) {
            aplicarTransicion(existente, cambios.getEstado());
        }
        marcarComoVencidaSiCorresponde(existente);
        return cuotaMensualRepositorio.save(existente);
    }

    public void eliminar(String id) {
        CuotaMensual existente = cuotaMensualRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuota mensual no encontrada"));
        existente.setEliminado(true);
        cuotaMensualRepositorio.save(existente);
    }

    @Transactional(readOnly = true)
    public CuotaMensual buscarPorId(String id) {
        return cuotaMensualRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuota mensual no encontrada"));
    }

    @Transactional(readOnly = true)
    public Page<CuotaMensual> listar(Pageable pageable) {
        return cuotaMensualRepositorio.findByEliminadoFalse(pageable);
    }

    @Transactional(readOnly = true)
    public Page<CuotaMensual> listarActivos(Pageable pageable) {
        return listar(pageable);
    }

    @Transactional(readOnly = true)
    public Page<CuotaMensual> listarPorEstado(EstadoCuota estado, Pageable pageable) {
        return cuotaMensualRepositorio.findByEstadoAndEliminadoFalse(estado, pageable);
    }

    private void validarFechaVencimiento(LocalDate fechaVencimiento, Mes mes, Long anio) {
        if (fechaVencimiento == null || mes == null || anio == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar mes, año y fecha de vencimiento");
        }
        YearMonth yearMonth = YearMonth.of(Math.toIntExact(anio), mes.ordinal() + 1);
        if (fechaVencimiento.isBefore(yearMonth.atDay(1)) || fechaVencimiento.isAfter(yearMonth.atEndOfMonth())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de vencimiento no pertenece al mes indicado");
        }
    }

    private void aplicarTransicion(CuotaMensual cuota, EstadoCuota nuevoEstado) {
        if (cuota.getEstado() == nuevoEstado) {
            return;
        }
        if (cuota.getEstado() != EstadoCuota.PENDIENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo las cuotas pendientes pueden cambiar de estado");
        }
        switch (nuevoEstado) {
            case PAGADA, CANCELADA -> cuota.setEstado(nuevoEstado);
            case VENCIDA -> {
                if (cuota.getFechaVencimiento() != null && cuota.getFechaVencimiento().isBefore(LocalDate.now())) {
                    cuota.setEstado(EstadoCuota.VENCIDA);
                } else {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo puede marcarse como vencida después del vencimiento");
                }
            }
            case PENDIENTE -> cuota.setEstado(EstadoCuota.PENDIENTE);
        }
    }

    private void marcarComoVencidaSiCorresponde(CuotaMensual cuota) {
        if (cuota.getEstado() == EstadoCuota.PENDIENTE
                && cuota.getFechaVencimiento() != null
                && cuota.getFechaVencimiento().isBefore(LocalDate.now())) {
            cuota.setEstado(EstadoCuota.VENCIDA);
        }
    }
}

