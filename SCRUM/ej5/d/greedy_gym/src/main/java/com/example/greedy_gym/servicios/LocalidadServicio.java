package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Departamento;
import com.example.greedy_gym.entidades.Localidad;
import com.example.greedy_gym.repositorios.LocalidadRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LocalidadServicio {

    private final LocalidadRepositorio repository;
    private final DepartamentoServicio departamentoServicio;

    @Autowired
    public LocalidadServicio(LocalidadRepositorio repository, DepartamentoServicio departamentoServicio) {
        this.repository = repository;
        this.departamentoServicio = departamentoServicio;
    }

    @Transactional
    public Localidad crearLocalidad(@NotBlank String nombre,
                                @NotBlank String codigoPostal,
                                @NotBlank String idDepartamento) {

        Departamento departamento = departamentoServicio.buscarDepartamento(idDepartamento);
        
        // Buscar si ya existe una localidad con este nombre en este departamento
        Localidad localidadExistente = repository.findByNombreIgnoreCaseAndDepartamentoId(nombre, idDepartamento).orElse(null);
        
        if (localidadExistente != null) {
            if (localidadExistente.isEliminado()) {
                // Si existe pero está eliminada, la reactivamos
                localidadExistente.setEliminado(false);
                localidadExistente.setCodigoPostal(codigoPostal); // Actualizar código postal
                localidadExistente.setDepartamento(departamento); // Asegurar que el departamento esté actualizado
                return repository.save(localidadExistente);
            } else {
                // Si existe y está activa, lanzamos error
                throw new ValidationException("La localidad ya existe: " + nombre);
            }
        }
        
        // Si no existe, creamos una nueva
        Localidad localidad = new Localidad();
        localidad.setNombre(nombre);
        localidad.setCodigoPostal(codigoPostal);
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);

        return repository.save(localidad);
    }

    public void validar(@NotBlank String nombre, String codigoPostal, Departamento departamento) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre es obligatorio");
        }
        if (repository.existsByNombreIgnoreCaseAndDepartamentoIdAndEliminadoFalse(nombre, departamento.getId())) {
            throw new ValidationException("La localidad ya existe: " + nombre);
        }
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidad(String id) {
        Localidad localidad = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Localidad no encontrada: " + id));
        if (localidad.isEliminado()) {
            throw new IllegalArgumentException("Localidad no disponible: " + id);
        }
        return localidad;
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidadPorNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Localidad no encontrada: " + nombre));
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidadPorCodigoPostal(String codigoPostal) {
        return repository.findByCodigoPostal(codigoPostal)
                .orElseThrow(() -> new IllegalArgumentException("Localidad no encontrada con código postal: " + codigoPostal));
    }

    @Transactional(readOnly = true)
    public Localidad buscarPorNombreYDepartamento(String nombre, String idDepartamento) {
        return repository.findByNombreIgnoreCaseAndDepartamentoId(nombre, idDepartamento).orElse(null);
    }

    public void modificarLocalidad(String id, String nombre, String codigoPostal, String idDepartamento) {
        Localidad actual = buscarLocalidad(id);
        Departamento departamento = departamentoServicio.buscarDepartamento(idDepartamento);
        
        if (!actual.getNombre().equalsIgnoreCase(nombre) || !actual.getDepartamento().getId().equals(idDepartamento)) {
            // Verificar que no exista otra localidad activa con el mismo nombre en el mismo departamento
            Localidad localidadConMismoNombre = repository.findByNombreIgnoreCaseAndDepartamentoId(nombre, idDepartamento).orElse(null);
            if (localidadConMismoNombre != null && !localidadConMismoNombre.getId().equals(id) && !localidadConMismoNombre.isEliminado()) {
                throw new ValidationException("La localidad ya existe: " + nombre);
            }
        }
        
        actual.setNombre(nombre);
        actual.setCodigoPostal(codigoPostal);
        actual.setDepartamento(departamento);
        repository.save(actual);
    }

    public void eliminarLocalidad(String id) {
        Localidad actual = buscarLocalidad(id);
        actual.setEliminado(true);
        repository.save(actual);
    }

    @Transactional(readOnly = true)
    public List<Localidad> listarLocalidad(String idDepartamento) {
        return repository.findByDepartamentoId(idDepartamento);
    }

    @Transactional(readOnly = true)
    public List<Localidad> listarLocalidadActivo(String idDepartamento) {
        return repository.findByDepartamentoId(idDepartamento).stream()
            .filter(e -> !e.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Localidad> listarTodasLasLocalidades() {
        return repository.findAllWithFullHierarchy();
    }
}
