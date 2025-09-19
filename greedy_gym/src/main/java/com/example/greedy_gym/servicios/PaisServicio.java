package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Pais;
import com.example.greedy_gym.repositorios.PaisRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaisServicio {

    private final PaisRepositorio repository;

    @Autowired
    public PaisServicio(PaisRepositorio repository) {
        this.repository = repository;
    }

    @Transactional
    public void crearPais(@NotBlank String nombre) {
        validar(nombre);
        Pais pais = new Pais();
        pais.setNombre(nombre);
        pais.setEliminado(false);
        repository.save(pais);
    }

    public void validar(@NotBlank String nombre) {
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ValidationException("El pais ya existe: " + nombre);
        }
    }

    @Transactional(readOnly = true)
    public Pais buscarPais(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pais no encontrado: " + id));
    }

    @Transactional(readOnly = true)
    public Pais buscarPaisPorNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Pais no encontrado: " + nombre));
    }

    public void modificarPais(String id, String nombre) {
        Pais actual = buscarPais(id);
        if (!actual.getNombre().equalsIgnoreCase(nombre)) {
            validar(nombre);
            actual.setNombre(nombre);
            repository.save(actual);
        }
    }

    public void eliminarPais(String id) {
        Pais actual = buscarPais(id);
        actual.setEliminado(true);
        repository.save(actual);
    }

    @Transactional(readOnly = true)
    public List<Pais> listarPais() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pais> listarPaisActivo() {
        return repository.findAll().stream()
                .filter(e -> !e.isEliminado())
                .toList();
    }
}
