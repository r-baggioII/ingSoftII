package com.uncuyo.greedy_cars.shared.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteAlquilerDTO {
    private String alquilerId;
    private String clienteNombre;
    private String clienteDocumento;
    private String vehiculoPatente;
    private String vehiculoMarca;
    private String vehiculoModelo;
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
    private Integer cantidadDias;
    private Double montoTotal;
    private String estado;
    private Long vehiculoAnio;
    private Integer vehiculoCantidadPuertas;
    private Integer vehiculoCantidadAsientos;
    private Integer vehiculoCantidadTotal;
    private Integer vehiculoCantidadAlquilado;
    private String vehiculoEstado;

    public ReporteAlquilerDTO(String alquilerId, String clienteNombre, String clienteDocumento,
                             String vehiculoPatente, String vehiculoMarca, String vehiculoModelo,
                             LocalDate fechaDesde, LocalDate fechaHasta, Double montoTotal, String estado,
                             Long vehiculoAnio, Integer vehiculoCantidadPuertas, Integer vehiculoCantidadAsientos,
                             Integer vehiculoCantidadTotal, Integer vehiculoCantidadAlquilado, String vehiculoEstado) {
        this.alquilerId = alquilerId;
        this.clienteNombre = clienteNombre;
        this.clienteDocumento = clienteDocumento;
        this.vehiculoPatente = vehiculoPatente;
        this.vehiculoMarca = vehiculoMarca;
        this.vehiculoModelo = vehiculoModelo;
        this.fechaDesde = fechaDesde;
        this.fechaHasta = fechaHasta;
        this.cantidadDias = calcularCantidadDias(fechaDesde, fechaHasta);
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.vehiculoAnio = vehiculoAnio;
        this.vehiculoCantidadPuertas = vehiculoCantidadPuertas;
        this.vehiculoCantidadAsientos = vehiculoCantidadAsientos;
        this.vehiculoCantidadTotal = vehiculoCantidadTotal;
        this.vehiculoCantidadAlquilado = vehiculoCantidadAlquilado;
        this.vehiculoEstado = vehiculoEstado;
    }

    private Integer calcularCantidadDias(LocalDate fechaDesde, LocalDate fechaHasta) {
        if (fechaDesde != null && fechaHasta != null) {
            return (int) java.time.temporal.ChronoUnit.DAYS.between(fechaDesde, fechaHasta) + 1;
        }
        return 0;
    }
}
