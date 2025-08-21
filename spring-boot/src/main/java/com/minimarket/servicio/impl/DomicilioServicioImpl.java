package com.minimarket.servicio.impl;

import com.minimarket.modelo.Domicilio;
import com.minimarket.repositorio.DomicilioRepositorio;
import com.minimarket.servicio.DomicilioServicio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DomicilioServicioImpl implements DomicilioServicio {

    private final DomicilioRepositorio domicilioRepositorio;

    public DomicilioServicioImpl(DomicilioRepositorio domicilioRepositorio) {
        this.domicilioRepositorio = domicilioRepositorio;
    }

    @Override
    public List<Domicilio> listarDomicilios() {
        return domicilioRepositorio.findAll();
    }

    @Override
    public Optional<Domicilio> buscarPorId(Long id) {
        return domicilioRepositorio.findById(id);
    }

    @Override
    public Domicilio guardarDomicilio(Domicilio domicilio) {
        return domicilioRepositorio.save(domicilio);
    }

    @Override
    public void eliminarDomicilio(Long id) {
        domicilioRepositorio.deleteById(id);
    }
}
