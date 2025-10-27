package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.ValorCuota;
import com.example.greedy_gym.repositorios.ValorCuotaRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Collection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ValorCuotaServicio {

    private final ValorCuotaRepositorio repository;

    public ValorCuotaServicio(ValorCuotaRepositorio repository) {
        this.repository = repository;
    }

    public ValorCuota crearValorCuota(@NotNull LocalDate fechaDesde,
                                      LocalDate fechaHasta,
                                      double valorCuota) {
        validar(fechaDesde, fechaHasta, valorCuota);
        ValorCuota nuevo = new ValorCuota(fechaDesde, fechaHasta, valorCuota);
        return repository.save(nuevo);
    }

    public void validar(@NotNull LocalDate fechaDesde,
                        LocalDate fechaHasta,
                        double valorCuota) {
        if (fechaDesde == null) {
            throw new ValidationException("La fecha desde es obligatoria");
        }
        if (valorCuota <= 0) {
            throw new ValidationException("El valor de la cuota debe ser mayor a 0");
        }
        if (fechaHasta != null && fechaHasta.isBefore(fechaDesde)) {
            throw new ValidationException("La fecha hasta no puede ser anterior a la fecha desde");
        }
    }

    @Transactional(readOnly = true)
    public ValorCuota buscarValorCuota(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ValorCuota no encontrado: " + id));
    }

    public void modificarValorCuota(String id,
                                    LocalDate fechaDesde,
                                    LocalDate fechaHasta,
                                    double valorCuota) {
        ValorCuota actual = buscarValorCuota(id);
        validar(fechaDesde, fechaHasta, valorCuota);
        actual.setFechaDesde(fechaDesde);
        actual.setFechaHasta(fechaHasta);
        actual.setValorCuota(valorCuota);
        repository.save(actual);
    }

    public void eliminarValorCuota(String id) {
        ValorCuota actual = buscarValorCuota(id);
        actual.setEliminado(true);
        repository.save(actual);
    }

    @Transactional(readOnly = true)
    public Collection<ValorCuota> listarValorCuota() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Collection<ValorCuota> listarValorCuotaActivo() {
        return repository.findAll().stream()
                .filter(v -> !v.isEliminado())
                .toList();
    }

    @Transactional(readOnly = true)
    public ValorCuota buscarValorCuotaVigente() {
        return repository.findVigente(LocalDate.now())
                .orElseThrow(() -> new IllegalArgumentException("No hay valor de cuota vigente"));
    }
}

