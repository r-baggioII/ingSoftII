package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.ReporteAlquilerDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.DetalleFactura;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.AlquilerRepository;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Chunk;
import com.lowagie.text.Phrase;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReporteAlquilerService {

    @Autowired
    private AlquilerRepository alquilerRepository;

    @Autowired
    private DetalleFacturaService detalleFacturaService;

    @Transactional(readOnly = true)
    public List<ReporteAlquilerDTO> generarReporteAlquileres(
            LocalDate fechaInicio, LocalDate fechaFin, String vehiculoId) throws ErrorServiceException {

        if (fechaInicio == null) {
            throw new ErrorServiceException("La fecha de inicio es obligatoria");
        }
        if (fechaFin == null) {
            throw new ErrorServiceException("La fecha de fin es obligatoria");
        }
        if (fechaFin.isBefore(fechaInicio)) {
            throw new ErrorServiceException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        List<Alquiler> alquileres;
        if (vehiculoId != null && !vehiculoId.trim().isEmpty()) {
            alquileres = alquilerRepository.findByVehiculoIdAndFechaDesdeBetweenAndEliminadoIsFalse(
                    vehiculoId, fechaInicio, fechaFin);
        } else {
            alquileres = alquilerRepository.findByFechaDesdeBetweenAndEliminadoIsFalse(fechaInicio, fechaFin);
        }

        return alquileres.stream()
                .map(this::convertirAlquilerADTO)
                .collect(Collectors.toList());
    }

    public byte[] generarReporteAlquileresPdf(
            LocalDate fechaInicio, LocalDate fechaFin, String vehiculoId) throws ErrorServiceException {

        List<ReporteAlquilerDTO> alquileres = generarReporteAlquileres(fechaInicio, fechaFin, vehiculoId);

        if (alquileres.isEmpty()) {
            throw new ErrorServiceException("No se encontraron alquileres en el período seleccionado");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Document document = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(document, baos);
            document.open();
            try {
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
                Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);

                Paragraph tituloPrincipal = new Paragraph("Reporte de Alquileres", titleFont);
                tituloPrincipal.setSpacingAfter(8f);
                document.add(tituloPrincipal);

                Paragraph infoPeriodo = new Paragraph("Período: " + fechaInicio + " al " + fechaFin, regularFont);
                infoPeriodo.setSpacingAfter(4f);
                document.add(infoPeriodo);

                Paragraph infoGeneracion = new Paragraph("Fecha de generación: " + LocalDate.now(), regularFont);
                infoGeneracion.setSpacingAfter(4f);
                document.add(infoGeneracion);
                if (vehiculoId != null && !vehiculoId.trim().isEmpty()) {
                    Paragraph infoVehiculo = new Paragraph("Filtro por vehículo: " + vehiculoId, regularFont);
                    infoVehiculo.setSpacingAfter(6f);
                    document.add(infoVehiculo);
                }
                document.add(Chunk.NEWLINE);

                // Resumen estadístico
                Paragraph tituloResumen = new Paragraph("Resumen Estadístico", sectionFont);
                tituloResumen.setSpacingAfter(8f);
                document.add(tituloResumen);

                PdfPTable tablaResumen = new PdfPTable(new float[]{2f, 1f});
                tablaResumen.setWidthPercentage(100f);
                tablaResumen.setSpacingBefore(4f);
                tablaResumen.setSpacingAfter(14f);

                Map<String, Object> estadisticas = calcularEstadisticas(alquileres);

                agregarCeldaTabla(tablaResumen, "Total de alquileres:", true, PdfPCell.ALIGN_LEFT);
                agregarCeldaTabla(tablaResumen, estadisticas.get("totalAlquileres").toString(), false, PdfPCell.ALIGN_RIGHT);

                agregarCeldaTabla(tablaResumen, "Ingresos totales:", true, PdfPCell.ALIGN_LEFT);
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));
                agregarCeldaTabla(tablaResumen, currencyFormat.format(estadisticas.get("ingresosTotales")), false, PdfPCell.ALIGN_RIGHT);

                agregarCeldaTabla(tablaResumen, "Días promedio:", true, PdfPCell.ALIGN_LEFT);
                agregarCeldaTabla(tablaResumen, String.format("%.1f", (Double) estadisticas.get("diasPromedio")), false, PdfPCell.ALIGN_RIGHT);

                agregarCeldaTabla(tablaResumen, "Monto promedio:", true, PdfPCell.ALIGN_LEFT);
                agregarCeldaTabla(tablaResumen, currencyFormat.format(estadisticas.get("montoPromedio")), false, PdfPCell.ALIGN_RIGHT);

                document.add(tablaResumen);
                document.add(Chunk.NEWLINE);

                // Detalle de alquileres
                Paragraph tituloDetalle = new Paragraph("Detalle de Alquileres", sectionFont);
                tituloDetalle.setSpacingAfter(8f);
                document.add(tituloDetalle);

                PdfPTable tablaDetalle = new PdfPTable(new float[]{1.2f, 2.2f, 2.8f, 1.1f, 1.1f, 0.9f, 1f});
                tablaDetalle.setWidthPercentage(100f);
                tablaDetalle.setSpacingBefore(4f);
                tablaDetalle.setSpacingAfter(12f);

                // Encabezados
                agregarCeldaTabla(tablaDetalle, "ID Alquiler", true, PdfPCell.ALIGN_CENTER, headerFont);
                agregarCeldaTabla(tablaDetalle, "Cliente", true, PdfPCell.ALIGN_LEFT, headerFont);
                agregarCeldaTabla(tablaDetalle, "Vehículo (detalle)", true, PdfPCell.ALIGN_LEFT, headerFont);
                agregarCeldaTabla(tablaDetalle, "Desde", true, PdfPCell.ALIGN_CENTER, headerFont);
                agregarCeldaTabla(tablaDetalle, "Hasta", true, PdfPCell.ALIGN_CENTER, headerFont);
                agregarCeldaTabla(tablaDetalle, "Días", true, PdfPCell.ALIGN_CENTER, headerFont);
                agregarCeldaTabla(tablaDetalle, "Monto", true, PdfPCell.ALIGN_RIGHT, headerFont);

                // Datos
                for (ReporteAlquilerDTO alquiler : alquileres) {
                    agregarCeldaTabla(tablaDetalle, alquiler.getAlquilerId().substring(0, 8) + "...", false, PdfPCell.ALIGN_LEFT);
                    agregarCeldaTabla(tablaDetalle, alquiler.getClienteNombre(), false, PdfPCell.ALIGN_LEFT);

                    StringBuilder detalleVehiculo = new StringBuilder();
                    detalleVehiculo.append(Optional.ofNullable(alquiler.getVehiculoPatente()).orElse("Sin patente"))
                            .append(" - ")
                            .append(Optional.ofNullable(alquiler.getVehiculoMarca()).orElse("Sin marca"))
                            .append(" ")
                            .append(Optional.ofNullable(alquiler.getVehiculoModelo()).orElse("Sin modelo"));
                    if (alquiler.getVehiculoAnio() != null) {
                        detalleVehiculo.append("\nAño: ").append(alquiler.getVehiculoAnio());
                    }
                    if (alquiler.getVehiculoCantidadPuertas() != null || alquiler.getVehiculoCantidadAsientos() != null) {
                        detalleVehiculo.append("\n");
                        if (alquiler.getVehiculoCantidadPuertas() != null) {
                            detalleVehiculo.append("Puertas: ").append(alquiler.getVehiculoCantidadPuertas());
                        }
                        if (alquiler.getVehiculoCantidadAsientos() != null) {
                            if (alquiler.getVehiculoCantidadPuertas() != null) {
                                detalleVehiculo.append(" | ");
                            }
                            detalleVehiculo.append("Asientos: ").append(alquiler.getVehiculoCantidadAsientos());
                        }
                    }
                    if (alquiler.getVehiculoEstado() != null) {
                        detalleVehiculo.append("\nEstado: ").append(alquiler.getVehiculoEstado());
                    }
                    agregarCeldaTabla(tablaDetalle, detalleVehiculo.toString(), false, PdfPCell.ALIGN_LEFT);
                    agregarCeldaTabla(tablaDetalle, alquiler.getFechaDesde().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), false, PdfPCell.ALIGN_CENTER);
                    agregarCeldaTabla(tablaDetalle, alquiler.getFechaHasta().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), false, PdfPCell.ALIGN_CENTER);
                    agregarCeldaTabla(tablaDetalle, alquiler.getCantidadDias().toString(), false, PdfPCell.ALIGN_CENTER);
                    agregarCeldaTabla(tablaDetalle, alquiler.getMontoTotal() != null ? currencyFormat.format(alquiler.getMontoTotal()) : "-", false, PdfPCell.ALIGN_RIGHT);
                }

                document.add(tablaDetalle);
                document.add(Chunk.NEWLINE);

                // Vehículo más alquilado
                if (estadisticas.containsKey("vehiculoMasAlquilado")) {
                    document.add(new Paragraph("Vehículo más alquilado: " + estadisticas.get("vehiculoMasAlquilado"), sectionFont));
                }

            } finally {
                document.close();
            }
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new ErrorServiceException("No se pudo generar el reporte en PDF", e);
        }
    }

    private ReporteAlquilerDTO convertirAlquilerADTO(Alquiler alquiler) {
        Double montoTotal = calcularMontoTotalAlquiler(alquiler);

        CaracteristicaVehiculo caracteristica = null;
        if (alquiler.getVehiculo() != null) {
            caracteristica = alquiler.getVehiculo().getCaracteristicaVehiculo();
        }

        return new ReporteAlquilerDTO(
                alquiler.getId(),
                alquiler.getCliente() != null ?
                        alquiler.getCliente().getNombre() + " " + alquiler.getCliente().getApellido() : "Sin cliente",
                alquiler.getCliente() != null ? alquiler.getCliente().getNumeroDocumento() : "Sin documento",
                alquiler.getVehiculo() != null ? alquiler.getVehiculo().getPatente() : "Sin patente",
                caracteristica != null ? caracteristica.getMarca() : "Sin marca",
                caracteristica != null ? caracteristica.getModelo() : "Sin modelo",
                alquiler.getFechaDesde(),
                alquiler.getFechaHasta(),
                montoTotal,
                Boolean.TRUE.equals(alquiler.getEliminado()) ? "ELIMINADO" : "ACTIVO",
                caracteristica != null ? caracteristica.getAnio() : null,
                caracteristica != null ? caracteristica.getCantidadPuerta() : null,
                caracteristica != null ? caracteristica.getCantidadAsiento() : null,
                caracteristica != null ? caracteristica.getCantidadTotalVehiculo() : null,
                caracteristica != null ? caracteristica.getCantidadVehiculoAlquilado() : null,
                alquiler.getVehiculo() != null && alquiler.getVehiculo().getEstadoVehiculo() != null ?
                        alquiler.getVehiculo().getEstadoVehiculo().name() : "DESCONOCIDO"
        );
    }

    private Double calcularMontoTotalAlquiler(Alquiler alquiler) {
        try {
            List<DetalleFactura> detalles = detalleFacturaService.findByAlquilerId(alquiler.getId());
            return detalles.stream()
                    .filter(d -> d.getSubtotal() != null && !d.getEliminado())
                    .mapToDouble(DetalleFactura::getSubtotal)
                    .sum();
        } catch (ErrorServiceException e) {
            return 0.0;
        }
    }

    private Map<String, Object> calcularEstadisticas(List<ReporteAlquilerDTO> alquileres) {
        Map<String, Object> estadisticas = new HashMap<>();

        estadisticas.put("totalAlquileres", alquileres.size());

        double ingresosTotales = alquileres.stream()
                .filter(a -> a.getMontoTotal() != null)
                .mapToDouble(ReporteAlquilerDTO::getMontoTotal)
                .sum();
        estadisticas.put("ingresosTotales", ingresosTotales);

        double diasPromedio = alquileres.stream()
                .filter(a -> a.getCantidadDias() != null && a.getCantidadDias() > 0)
                .mapToInt(ReporteAlquilerDTO::getCantidadDias)
                .average()
                .orElse(0.0);
        estadisticas.put("diasPromedio", diasPromedio);

        double montoPromedio = alquileres.stream()
                .filter(a -> a.getMontoTotal() != null && a.getMontoTotal() > 0)
                .mapToDouble(ReporteAlquilerDTO::getMontoTotal)
                .average()
                .orElse(0.0);
        estadisticas.put("montoPromedio", montoPromedio);

        // Vehículo más alquilado
        Optional<Map.Entry<String, Long>> vehiculoMasAlquilado = alquileres.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getVehiculoPatente() + " - " + a.getVehiculoModelo(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue());

        if (vehiculoMasAlquilado.isPresent()) {
            estadisticas.put("vehiculoMasAlquilado",
                    vehiculoMasAlquilado.get().getKey() + " (" + vehiculoMasAlquilado.get().getValue() + " alquileres)");
        }

        return estadisticas;
    }

    private void agregarCeldaTabla(PdfPTable table, String texto, boolean esHeader, int alineacion) {
        agregarCeldaTabla(table, texto, esHeader, alineacion, null);
    }

    private void agregarCeldaTabla(PdfPTable table, String texto, boolean esHeader, int alineacion, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, font != null ? font :
                (esHeader ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10) : FontFactory.getFont(FontFactory.HELVETICA, 10))));
        cell.setHorizontalAlignment(alineacion);
        cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
