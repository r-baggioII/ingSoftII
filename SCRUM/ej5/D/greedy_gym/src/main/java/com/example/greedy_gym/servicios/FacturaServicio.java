package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.CuotaMensual;
import com.example.greedy_gym.entidades.DetalleFactura;
import com.example.greedy_gym.entidades.EstadoFactura;
import com.example.greedy_gym.entidades.Factura;
import com.example.greedy_gym.entidades.FormaDePago;
import com.example.greedy_gym.repositorios.CuotaMensualRepositorio;
import com.example.greedy_gym.repositorios.DetalleFacturaRepositorio;
import com.example.greedy_gym.repositorios.FacturaRepositorio;
import com.example.greedy_gym.repositorios.FormaDePagoRepositorio;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FacturaServicio {

    private final FacturaRepositorio facturaRepositorio;
    private final DetalleFacturaRepositorio detalleFacturaRepositorio;
    private final FormaDePagoRepositorio formaDePagoRepositorio;
    private final CuotaMensualRepositorio cuotaMensualRepositorio;

    public FacturaServicio(FacturaRepositorio facturaRepositorio,
                           DetalleFacturaRepositorio detalleFacturaRepositorio,
                           FormaDePagoRepositorio formaDePagoRepositorio,
                           CuotaMensualRepositorio cuotaMensualRepositorio) {
        this.facturaRepositorio = facturaRepositorio;
        this.detalleFacturaRepositorio = detalleFacturaRepositorio;
        this.formaDePagoRepositorio = formaDePagoRepositorio;
        this.cuotaMensualRepositorio = cuotaMensualRepositorio;
    }

    public Factura crear(Factura factura) {
        validarFacturaParaCrear(factura);

        if (factura.getNumeroFactura() == null || factura.getNumeroFactura() <= 0) {
            factura.setNumeroFactura(siguienteNumeroFactura());
        }

        final Long numeroFactura = factura.getNumeroFactura();

        facturaRepositorio.findByNumeroFacturaAndEliminadoFalse(numeroFactura)
                .ifPresent(existing -> {
                    throw new DataIntegrityViolationException("Ya existe una factura con ese número");
                });

        FormaDePago formaDePago = formaDePagoRepositorio.findByIdAndEliminadoFalse(factura.getFormaDePago().getId())
                .orElseThrow(() -> new EntityNotFoundException("Forma de pago no encontrada"));

        Factura nueva = new Factura();
        nueva.setNumeroFactura(numeroFactura);
        nueva.setFechaFactura(factura.getFechaFactura());
        nueva.setTotalPagado(factura.getTotalPagado());
        nueva.setEstado(factura.getEstado() != null ? factura.getEstado() : EstadoFactura.SIN_DEFINIR);
        nueva.setFormaDePago(formaDePago);
        nueva.setEliminado(false);

        List<DetalleFactura> detalles = construirDetalles(factura.getDetalles(), nueva);
        nueva.setDetalles(new ArrayList<>(detalles));

        return facturaRepositorio.save(nueva);
    }

    public Factura actualizar(String id, Factura datosFactura) {
        if (id == null || id.isBlank()) {
            throw new ValidationException("El id es obligatorio");
        }
        Factura factura = facturaRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));

        if (datosFactura.getFechaFactura() != null) {
            factura.setFechaFactura(datosFactura.getFechaFactura());
        }
        if (datosFactura.getTotalPagado() != null) {
            if (datosFactura.getTotalPagado() < 0) {
                throw new ValidationException("El total pagado no puede ser negativo");
            }
            factura.setTotalPagado(datosFactura.getTotalPagado());
        }
        if (datosFactura.getEstado() != null) {
            factura.setEstado(datosFactura.getEstado());
        }
        if (datosFactura.getFormaDePago() != null && datosFactura.getFormaDePago().getId() != null) {
            FormaDePago formaDePago = formaDePagoRepositorio.findByIdAndEliminadoFalse(datosFactura.getFormaDePago().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Forma de pago no encontrada"));
            factura.setFormaDePago(formaDePago);
        }

        if (datosFactura.getDetalles() != null) {
            factura.getDetalles().forEach(detalle -> detalle.setEliminado(true));
            List<DetalleFactura> nuevosDetalles = construirDetalles(datosFactura.getDetalles(), factura);
            factura.getDetalles().addAll(nuevosDetalles);
        }

        return facturaRepositorio.save(factura);
    }

    public void eliminar(String id) {
        Factura factura = facturaRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));
        factura.setEliminado(true);
        factura.getDetalles().forEach(detalle -> detalle.setEliminado(true));
        facturaRepositorio.save(factura);
    }

    @Transactional(readOnly = true)
    public Page<Factura> listar(EstadoFactura estado, int page, int size, String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        return facturaRepositorio.search(estado, pageable);
    }

    @Transactional(readOnly = true)
    public Factura buscar(String id) {
        return facturaRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));
    }

    @Transactional(readOnly = true)
    public List<DetalleFactura> listarDetalles(String facturaId) {
        verificarFacturaActiva(facturaId);
        return detalleFacturaRepositorio.findByFacturaIdAndEliminadoFalse(facturaId);
    }

    public DetalleFactura agregarDetalle(String facturaId, DetalleFactura detalleFactura) {
        validarDetalle(detalleFactura);
        Factura factura = facturaRepositorio.findByIdAndEliminadoFalse(facturaId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));
        CuotaMensual cuotaMensual = cuotaMensualRepositorio.findByIdAndEliminadoFalse(detalleFactura.getCuotaMensual().getId())
                .orElseThrow(() -> new EntityNotFoundException("Cuota mensual no encontrada"));

        DetalleFactura detalle = new DetalleFactura();
        detalle.setFactura(factura);
        detalle.setCuotaMensual(cuotaMensual);
        detalle.setEliminado(false);

        DetalleFactura guardado = detalleFacturaRepositorio.save(detalle);
        factura.getDetalles().add(guardado);
        return guardado;
    }

    public void eliminarDetalle(String detalleId) {
        DetalleFactura detalle = detalleFacturaRepositorio.findByIdAndEliminadoFalse(detalleId)
                .orElseThrow(() -> new EntityNotFoundException("Detalle de factura no encontrado"));
        detalle.setEliminado(true);
        detalleFacturaRepositorio.save(detalle);
    }

    private List<DetalleFactura> construirDetalles(List<DetalleFactura> detalles, Factura factura) {
        if (detalles == null || detalles.isEmpty()) {
            throw new ValidationException("La factura debe contener al menos un detalle");
        }
        return detalles.stream()
                .filter(Objects::nonNull)
                .map(detalle -> {
                    if (detalle.getCuotaMensual() == null || detalle.getCuotaMensual().getId() == null
                            || detalle.getCuotaMensual().getId().isBlank()) {
                        throw new ValidationException("Cada detalle debe tener una cuota mensual válida");
                    }
                    CuotaMensual cuotaMensual = cuotaMensualRepositorio.findByIdAndEliminadoFalse(detalle.getCuotaMensual().getId())
                            .orElseThrow(() -> new EntityNotFoundException("Cuota mensual no encontrada"));
                    DetalleFactura nuevo = new DetalleFactura();
                    nuevo.setFactura(factura);
                    nuevo.setCuotaMensual(cuotaMensual);
                    nuevo.setEliminado(false);
                    return nuevo;
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void verificarFacturaActiva(String facturaId) {
        facturaRepositorio.findByIdAndEliminadoFalse(facturaId)
                .orElseThrow(() -> new EntityNotFoundException("Factura no encontrada"));
    }

    private Long siguienteNumeroFactura() {
        Long max = facturaRepositorio.obtenerMaxNumeroFactura();
        if (max == null) {
            max = 0L;
        }
        return max + 1L;
    }

    private void validarFacturaParaCrear(Factura factura) {
        if (factura == null) {
            throw new ValidationException("Los datos de la factura son obligatorios");
        }
        if (factura.getNumeroFactura() != null && factura.getNumeroFactura() <= 0) {
            throw new ValidationException("El número de factura debe ser positivo");
        }
        if (factura.getFechaFactura() == null) {
            throw new ValidationException("La fecha de la factura es obligatoria");
        }
        if (factura.getTotalPagado() == null || factura.getTotalPagado() < 0) {
            throw new ValidationException("El total pagado no puede ser negativo");
        }
        if (factura.getFormaDePago() == null || factura.getFormaDePago().getId() == null
                || factura.getFormaDePago().getId().isBlank()) {
            throw new ValidationException("Debe indicar una forma de pago válida");
        }
        if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
            throw new ValidationException("La factura debe contener al menos un detalle");
        }
    }

    private void validarDetalle(DetalleFactura detalle) {
        if (detalle == null || detalle.getCuotaMensual() == null || detalle.getCuotaMensual().getId() == null
                || detalle.getCuotaMensual().getId().isBlank()) {
            throw new ValidationException("El detalle debe contener un id de cuota mensual");
        }
    }

    private Pageable buildPageable(int page, int size, String sort) {
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}
