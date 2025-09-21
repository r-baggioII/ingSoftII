package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Departamento;
import com.example.greedy_gym.entidades.Provincia;
import com.example.greedy_gym.repositorios.DepartamentoRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DepartamentoServicio {

    private final DepartamentoRepositorio repository;
    private final ProvinciaServicio provinciaServicio;

    @Autowired
    public DepartamentoServicio(DepartamentoRepositorio repository, ProvinciaServicio provinciaServicio) {
        this.repository = repository;
        this.provinciaServicio = provinciaServicio;
    }

    @Transactional
    public Departamento crearDepartamento(@NotBlank String nombre, @NotBlank String idProvincia) {
        Provincia provincia = provinciaServicio.buscarProvincia(idProvincia);
        validar(nombre, provincia);
        
        Departamento departamento = new Departamento();
        departamento.setNombre(nombre);
        departamento.setProvincia(provincia);
        departamento.setEliminado(false);
        
        return repository.save(departamento);
    }

    public void validar(@NotBlank String nombre, Provincia provincia) {
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ValidationException("El departamento ya existe: " + nombre);
        }
    }

    @Transactional(readOnly = true)
    public Departamento buscarDepartamento(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Departamento buscarPorNombreYProvincia(String nombre, String idProvincia) {
        return repository.findByNombreIgnoreCaseAndProvinciaId(nombre, idProvincia).orElse(null);
    }

    @Transactional(readOnly = true)
    public Departamento buscarDepartamentoPorNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Departamento no encontrado: " + nombre));
    }

    public void modificarDepartamento(String id, String nombre, String idProvincia) {
        Departamento actual = buscarDepartamento(id);
        Provincia provincia = provinciaServicio.buscarProvincia(idProvincia);
        
        if (!actual.getNombre().equalsIgnoreCase(nombre)) {
            validar(nombre, provincia);
            actual.setNombre(nombre);
        }
        actual.setProvincia(provincia);
        repository.save(actual);
    }

    public void eliminarDepartamento(String id) {
        Departamento actual = buscarDepartamento(id);
        actual.setEliminado(true);
        repository.save(actual);
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarDepartamento(String idProvincia) {
        return repository.findByProvinciaId(idProvincia);
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarDepartamentoActivo(String idProvincia) {
        return repository.findByProvinciaId(idProvincia).stream()
            .filter(e -> !e.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Departamento> listarTodosLosDepartamentos() {
        return repository.findAllWithProvinciasAndPaises();
    }
}
