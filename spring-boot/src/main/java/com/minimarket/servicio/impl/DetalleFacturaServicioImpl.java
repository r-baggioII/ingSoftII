package com.minimarket.servicio.impl;

import com.minimarket.modelo.DetalleFactura;
import com.minimarket.repositorio.DetalleFacturaRepositorio;
import com.minimarket.servicio.DetalleFacturaServicio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DetalleFacturaServicioImpl implements DetalleFacturaServicio {

    private final DetalleFacturaRepositorio detalleFacturaRepositorio;

    public DetalleFacturaServicioImpl(DetalleFacturaRepositorio detalleFacturaRepositorio) {
        this.detalleFacturaRepositorio = detalleFacturaRepositorio;
    }

    @Override
    public List<DetalleFactura> listarDetalles() {
        return detalleFacturaRepositorio.findAll();
    }

    @Override
    public Optional<DetalleFactura> buscarPorId(Long id) {
        return detalleFacturaRepositorio.findById(id);
    }

    @Override
    public DetalleFactura guardarDetalle(DetalleFactura detalle) {
        return detalleFacturaRepositorio.save(detalle);
    }

    @Override
    public void eliminarDetalle(Long id) {
        detalleFacturaRepositorio.deleteById(id);
    }
}
