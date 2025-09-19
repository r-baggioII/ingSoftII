package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Direccion;
import com.example.greedy_gym.entidades.Localidad;
import com.example.greedy_gym.repositorios.DireccionRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DireccionServicio {

    private final DireccionRepositorio direccionRepositorio;
    private final LocalidadServicio localidadServicio;

    @Autowired
    public DireccionServicio(DireccionRepositorio direccionRepositorio, LocalidadServicio localidadServicio) {
        this.direccionRepositorio = direccionRepositorio;
        this.localidadServicio = localidadServicio;
    }

    @Transactional
    public Direccion crearDireccion(@NotBlank String calle,
                                   @NotBlank String numeracion,
                                   String barrio,
                                   String manzanaPiso,
                                   String casaDepartamento,
                                   String referencia,
                                   @NotBlank String idLocalidad) {

        validar(calle, numeracion, barrio, manzanaPiso, casaDepartamento, referencia, idLocalidad);

        // Buscar la localidad
        Localidad localidad = localidadServicio.buscarLocalidad(idLocalidad);

        // Crear nueva dirección
        Direccion direccion = new Direccion();
        direccion.setCalle(calle);
        direccion.setNumero(numeracion);
        direccion.setBarrio(barrio);
        direccion.setManzanaPiso(manzanaPiso);
        direccion.setCasaDepartamento(casaDepartamento);
        direccion.setReferencia(referencia);
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);

        return direccionRepositorio.save(direccion);
    }

    public void validar(@NotBlank String calle,
                       @NotBlank String numeracion,
                       String barrio,
                       String manzanaPiso,
                       String casaDepartamento,
                       String referencia,
                       @NotBlank String idLocalidad) {
        
        // Validar que los campos obligatorios no estén vacíos
        if (calle == null || calle.trim().isEmpty()) {
            throw new ValidationException("La calle es obligatoria");
        }
        
        if (numeracion == null || numeracion.trim().isEmpty()) {
            throw new ValidationException("La numeración es obligatoria");
        }
        
        if (idLocalidad == null || idLocalidad.trim().isEmpty()) {
            throw new ValidationException("La localidad es obligatoria");
        }

        // Validar que no exista una dirección con la misma calle y numeración
        if (direccionRepositorio.existsByCalleAndNumeroAndEliminadoFalse(calle, numeracion)) {
            throw new ValidationException("Ya existe una dirección con la calle '" + calle + "' y numeración '" + numeracion + "'");
        }
    }

    @Transactional(readOnly = true)
    public Direccion buscarDireccionPorCalleNumeracion(@NotBlank String calle, @NotBlank String numeracion) {
        return direccionRepositorio.findByCalleAndNumeroAndEliminadoFalse(calle, numeracion)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada con calle: " + calle + " y numeración: " + numeracion));
    }

    @Transactional
    public void modificarDireccion(@NotBlank String id,
                                  @NotBlank String calle,
                                  @NotBlank String numeracion,
                                  String barrio,
                                  String manzanaPiso,
                                  String casaDepartamento,
                                  String referencia,
                                  @NotBlank String idLocalidad) {

        // Buscar la dirección actual
        Direccion direccionActual = direccionRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada: " + id));

        // Validar solo si la calle y numeración han cambiado
        if (!direccionActual.getCalle().equals(calle) || !direccionActual.getNumero().equals(numeracion)) {
            if (direccionRepositorio.existsByCalleAndNumeroAndEliminadoFalse(calle, numeracion)) {
                throw new ValidationException("Ya existe una dirección con la calle '" + calle + "' y numeración '" + numeracion + "'");
            }
        }

        // Buscar la nueva localidad si cambió
        Localidad localidad = localidadServicio.buscarLocalidad(idLocalidad);

        // Actualizar los campos
        direccionActual.setCalle(calle);
        direccionActual.setNumero(numeracion);
        direccionActual.setBarrio(barrio);
        direccionActual.setManzanaPiso(manzanaPiso);
        direccionActual.setCasaDepartamento(casaDepartamento);
        direccionActual.setReferencia(referencia);
        direccionActual.setLocalidad(localidad);

        direccionRepositorio.save(direccionActual);
    }

    @Transactional
    public void eliminarDireccion(@NotBlank String id) {
        Direccion direccion = direccionRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada: " + id));
        
        direccion.setEliminado(true);
        direccionRepositorio.save(direccion);
    }

    @Transactional(readOnly = true)
    public Direccion buscarDireccion(String id) {
        return direccionRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada: " + id));
    }
}
