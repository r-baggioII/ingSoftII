package com.minimarket.servicio;

import com.minimarket.modelo.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClienteServicio {

    Page<Cliente> listarClientes(Pageable pageable);
    Page<Cliente> buscarPorApellido(String texto, Pageable pageable);
    Optional<Cliente> buscarPorId(Long id);
    Cliente guardarCliente(Cliente cliente);
    void eliminarCliente(Long id);
}
