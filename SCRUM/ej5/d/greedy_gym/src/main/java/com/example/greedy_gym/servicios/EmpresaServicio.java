package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Empresa;
import com.example.greedy_gym.repositorios.EmpresaRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class EmpresaServicio {

    private final EmpresaRepositorio repository;

    public EmpresaServicio(EmpresaRepositorio repository) {
        this.repository = repository;
    }

    public Empresa crearEmpresa(@NotBlank String nombre,
                                @NotBlank String telefono,
                                @NotBlank String correoElectronico) {
        validar(nombre);
        Empresa empresa = new Empresa(nombre, telefono, correoElectronico);
        return repository.save(empresa);
    }

    public void validar(@NotBlank String nombre) {
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ValidationException("La empresa ya existe: " + nombre);
        }
    }

    @Transactional(readOnly = true)
    public Empresa buscarEmpresa(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Empresa buscarEmpresaPorNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + nombre));
    }

    public void modificarEmpresa(String id, String nombre) {
        Empresa actual = buscarEmpresa(id);
        if (!actual.getNombre().equalsIgnoreCase(nombre)) {
            validar(nombre);
            actual.setNombre(nombre);
            repository.save(actual);
        }
    }

    public void eliminarEmpresa(String id) {
        Empresa actual = buscarEmpresa(id);
        actual.setEliminado(true);
        repository.save(actual);
    }

    @Transactional(readOnly = true)
    public List<Empresa> listarEmpresa() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Empresa> listarEmpresaActiva() {
        return repository.findAll().stream()
                .filter(e -> !e.isEliminado())
                .toList();
    }
}
