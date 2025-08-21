package com.minimarket.servicio;

import com.minimarket.modelo.Domicilio;
import java.util.List;
import java.util.Optional;

public interface DomicilioServicio {
    List<Domicilio> listarDomicilios();
    Optional<Domicilio> buscarPorId(Long id);
    Domicilio guardarDomicilio(Domicilio domicilio);
    void eliminarDomicilio(Long id);
}
