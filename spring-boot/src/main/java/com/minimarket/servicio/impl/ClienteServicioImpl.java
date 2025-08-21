package com.minimarket.servicio.impl;

import com.minimarket.modelo.Cliente;
import com.minimarket.repositorio.ClienteRepositorio;
import com.minimarket.servicio.ClienteServicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServicioImpl implements ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;

    public ClienteServicioImpl(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    @Override
    public Page<Cliente> listarClientes(Pageable pageable) {
        return clienteRepositorio.findAll(pageable);
    }

    @Override
    public Page<Cliente> buscarPorApellido(String texto, Pageable pageable) {
        return clienteRepositorio.findByApellidoContainingIgnoreCase(texto, pageable);
    }

    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepositorio.findById(id);
    }

    @Override
    public Cliente guardarCliente(Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    @Override
    public void eliminarCliente(Long id) {
        clienteRepositorio.deleteById(id);
    }
}
