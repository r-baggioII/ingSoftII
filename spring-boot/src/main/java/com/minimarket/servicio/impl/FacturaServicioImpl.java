package com.minimarket.servicio.impl;

import com.minimarket.modelo.Factura;
import com.minimarket.repositorio.FacturaRepositorio;
import com.minimarket.servicio.FacturaServicio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaServicioImpl implements FacturaServicio {

    private final FacturaRepositorio facturaRepositorio;

    public FacturaServicioImpl(FacturaRepositorio facturaRepositorio) {
        this.facturaRepositorio = facturaRepositorio;
    }

    @Override
    public List<Factura> listarFacturas() {
        return facturaRepositorio.findAll();
    }

    @Override
    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepositorio.findById(id);
    }

    @Override
    public Factura guardarFactura(Factura factura) {
        return facturaRepositorio.save(factura);
    }

    @Override
    public void eliminarFactura(Long id) {
        facturaRepositorio.deleteById(id);
    }
}
