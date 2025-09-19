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
    public void crearLocalidad(@NotBlank String nombre,
                                @NotBlank String codigoPostal,
                                @NotBlank String idDepartamento) {

        Departamento departamento = departamentoServicio.buscarDepartamento(idDepartamento);
        validar(nombre, codigoPostal, departamento);

        Localidad localidad = new Localidad();
        localidad.setNombre(nombre);
        localidad.setCodigoPostal(codigoPostal);
        localidad.setDepartamento(departamento);
        localidad.setEliminado(false);

        repository.save(localidad);
    }

    public void validar(@NotBlank String nombre, String codigoPostal, Departamento departamento) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidationException("El nombre es obligatorio");
        }
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ValidationException("La localidad ya existe: " + nombre);
        }
    }

    @Transactional(readOnly = true)
    public Localidad buscarLocalidad(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Localidad no encontrada: " + id));
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

    public void modificarLocalidad(String id, String nombre, String codigoPostal, String idDepartamento) {
        Localidad actual = buscarLocalidad(id);
        Departamento departamento = departamentoServicio.buscarDepartamento(idDepartamento);
        
        if (!actual.getNombre().equalsIgnoreCase(nombre)) {
            validar(nombre, codigoPostal, departamento);
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
        return repository.findAll();
    }
}
