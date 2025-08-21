package com.minimarket.servicio;

import com.minimarket.modelo.DetalleFactura;
import java.util.List;
import java.util.Optional;

public interface DetalleFacturaServicio {
    List<DetalleFactura> listarDetalles();
    Optional<DetalleFactura> buscarPorId(Long id);
    DetalleFactura guardarDetalle(DetalleFactura detalle);
    void eliminarDetalle(Long id);
}
