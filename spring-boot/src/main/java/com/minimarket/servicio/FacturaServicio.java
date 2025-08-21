package com.minimarket.servicio;

import com.minimarket.modelo.Factura;
import java.util.List;
import java.util.Optional;

public interface FacturaServicio {
    List<Factura> listarFacturas();
    Optional<Factura> buscarPorId(Long id);
    Factura guardarFactura(Factura factura);
    void eliminarFactura(Long id);
}
