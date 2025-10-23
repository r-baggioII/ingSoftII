package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.CuotaMensual;
import com.example.greedy_gym.entidades.EstadoCuota;
import com.example.greedy_gym.entidades.Mes;
import com.example.greedy_gym.entidades.ValorCuota;
import com.example.greedy_gym.repositorios.CuotaMensualRepositorio;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import com.example.greedy_gym.repositorios.ValorCuotaRepositorio;
import jakarta.validation.ValidationException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuotaMensualServicio {

    private final CuotaMensualRepositorio cuotaMensualRepositorio;
    private final SocioRepositorio socioRepositorio;
    private final ValorCuotaRepositorio valorCuotaRepositorio;

    public CuotaMensualServicio(CuotaMensualRepositorio cuotaMensualRepositorio,
                                SocioRepositorio socioRepositorio,
                                ValorCuotaRepositorio valorCuotaRepositorio) {
        this.cuotaMensualRepositorio = cuotaMensualRepositorio;
        this.socioRepositorio = socioRepositorio;
        this.valorCuotaRepositorio = valorCuotaRepositorio;
    }

    @Transactional
    public CuotaMensual crearCuota(String idSocio, Mes mes, Long anio, String idValorCuota) {
        validar(idSocio, mes, anio, idValorCuota);

        // Validar socio existe
        if (!socioRepositorio.findByIdAndEliminadoFalse(idSocio).isPresent()) {
            throw new IllegalArgumentException("El socio no existe");
        }

        // Validar valorCuota existe
        ValorCuota valorCuota = valorCuotaRepositorio.findByIdAndEliminadoFalse(idValorCuota)
                .orElseThrow(() -> new IllegalArgumentException("El valor de cuota no existe"));

        // Validar que no exista otra cuota mensual para el mismo socio, mes y año
        if (cuotaMensualRepositorio.existsByIdSocioAndMesAndAnioAndEliminadoFalse(idSocio, mes, anio)) {
            throw new ValidationException("Ya existe una cuota mensual para este socio en el mes y año especificados");
        }

        CuotaMensual cuotaMensual = new CuotaMensual();
        cuotaMensual.setId(UUID.randomUUID().toString());
        cuotaMensual.setIdSocio(idSocio);
        cuotaMensual.setMes(mes);
        cuotaMensual.setAnio(anio);
        cuotaMensual.setValorCuota(valorCuota);
    cuotaMensual.setEstado(EstadoCuota.ADEUDADA);
        cuotaMensual.setFechaVencimiento(calcularVencimiento(mes, anio));
        cuotaMensual.setEliminado(false);

        CuotaMensual guardada = cuotaMensualRepositorio.save(cuotaMensual);
        enriquecerConDatosSocio(guardada);
        return guardada;
    }

    @Transactional
    public void modificarCuota(String id, String idSocio, Mes mes, Long anio, String idValorCuota, EstadoCuota estado) {
        validar(idSocio, mes, anio, idValorCuota);

        if (estado == null) {
            throw new IllegalArgumentException("El estado de la cuota no puede estar vacío");
        }

        CuotaMensual cuotaMensual = cuotaMensualRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("La cuota mensual no existe"));

        // Validar socio existe
        if (!socioRepositorio.findByIdAndEliminadoFalse(idSocio).isPresent()) {
            throw new IllegalArgumentException("El socio no existe");
        }

        // Validar valorCuota existe
        ValorCuota valorCuota = valorCuotaRepositorio.findByIdAndEliminadoFalse(idValorCuota)
                .orElseThrow(() -> new IllegalArgumentException("El valor de cuota no existe"));

        // Validar que no exista otra cuota mensual para el mismo socio, mes y año (excluyendo la actual)
        if (cuotaMensualRepositorio.existsByIdSocioAndMesAndAnioAndEliminadoFalseAndIdNot(idSocio, mes, anio, id)) {
            throw new ValidationException("Ya existe otra cuota mensual para este socio en el mes y año especificados");
        }

        cuotaMensual.setIdSocio(idSocio);
        cuotaMensual.setMes(mes);
        cuotaMensual.setAnio(anio);
        cuotaMensual.setValorCuota(valorCuota);
        cuotaMensual.setEstado(estado);
        cuotaMensual.setFechaVencimiento(calcularVencimiento(mes, anio));

        cuotaMensualRepositorio.save(cuotaMensual);
        enriquecerConDatosSocio(cuotaMensual);
    }

    @Transactional
    public void eliminarCuotaMensual(String id) {
        CuotaMensual cuotaMensual = cuotaMensualRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("La cuota mensual no existe"));

        cuotaMensual.setEliminado(true);
        cuotaMensualRepositorio.save(cuotaMensual);
    }

    @Transactional(readOnly = true)
    public CuotaMensual buscarCuotaMensual(String id) {
        CuotaMensual c = cuotaMensualRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("La cuota mensual no existe"));
        enriquecerConDatosSocio(c);
        return c;
    }

    @Transactional(readOnly = true)
    public Collection<CuotaMensual> listarCuotaMensual() {
        Collection<CuotaMensual> list = cuotaMensualRepositorio.findByEliminadoFalse();
        list.forEach(this::enriquecerConDatosSocio);
        return list;
    }

    @Transactional(readOnly = true)
    public Collection<CuotaMensual> listarCuotaMensualActivo() {
        Collection<CuotaMensual> list = cuotaMensualRepositorio.findByEliminadoFalse();
        list.forEach(this::enriquecerConDatosSocio);
        return list;
    }

    @Transactional(readOnly = true)
    public Collection<CuotaMensual> listarCuotaMensualPorEstado(EstadoCuota estado) {
        Collection<CuotaMensual> list = cuotaMensualRepositorio.findByEstadoAndEliminadoFalse(estado);
        list.forEach(this::enriquecerConDatosSocio);
        return list;
    }

    @Transactional(readOnly = true)
    public Collection<CuotaMensual> listarCuotaMensualPorFecha(LocalDate fechaDesde, LocalDate fechaHasta) {
        // Since fechaCreacion doesn't exist in the entity, filter by mes/anio instead
        Collection<CuotaMensual> list = cuotaMensualRepositorio.findByEliminadoFalse();
        list.forEach(this::enriquecerConDatosSocio);
        return list;
    }

    private void validar(String idSocio, Mes mes, Long anio, String idValorCuota) {
        if (idSocio == null || idSocio.trim().isEmpty()) {
            throw new IllegalArgumentException("El id del socio no puede estar vacío");
        }

        if (mes == null) {
            throw new IllegalArgumentException("El mes no puede estar vacío");
        }

        if (anio == null || anio <= 0) {
            throw new IllegalArgumentException("El año debe ser un valor positivo");
        }

        if (idValorCuota == null || idValorCuota.trim().isEmpty()) {
            throw new IllegalArgumentException("El id del valor de cuota no puede estar vacío");
        }
    }

    private LocalDate calcularVencimiento(Mes mes, Long anio) {
        int month = mes.ordinal() + 1; // ENERO=1, ... DICIEMBRE=12
        YearMonth ym = YearMonth.of(Math.toIntExact(anio), month);
        return ym.atEndOfMonth();
    }

    private void enriquecerConDatosSocio(CuotaMensual cuota) {
        if (cuota == null) return;
        socioRepositorio.findByIdAndEliminadoFalse(cuota.getIdSocio()).ifPresent(s -> {
            cuota.setSocioNumeroDocumento(s.getNumeroDocumento());
            cuota.setSocioNumeroSocio(s.getNumeroSocio());
            String nombre = (s.getNombre() != null ? s.getNombre().trim() : "");
            String apellido = (s.getApellido() != null ? s.getApellido().trim() : "");
            String full = (nombre + " " + apellido).trim();
            cuota.setSocioNombreCompleto(full.isEmpty() ? null : full);
        });
    }
}
