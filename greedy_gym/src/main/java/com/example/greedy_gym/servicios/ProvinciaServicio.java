package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Pais;
import com.example.greedy_gym.entidades.Provincia;
import com.example.greedy_gym.repositorios.ProvinciaRepositorio;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProvinciaServicio {

    private final ProvinciaRepositorio repository;
    private final PaisServicio paisServicio;

    @Autowired
    public ProvinciaServicio(ProvinciaRepositorio repository, PaisServicio paisServicio) {
        this.repository = repository;
        this.paisServicio = paisServicio;
    }

    @Transactional
    public void crearProvincia(@NotBlank String nombre, @NotBlank String idPais) {
        Pais pais = paisServicio.buscarPais(idPais);
        validar(nombre, pais);
        
        Provincia provincia = new Provincia();
        provincia.setNombre(nombre);
        provincia.setPais(pais);
        provincia.setEliminado(false);
        
        repository.save(provincia);
    }

    public void validar(@NotBlank String nombre, Pais pais) {
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new ValidationException("La provincia ya existe: " + nombre);
        }
    }

    @Transactional(readOnly = true)
    public Provincia buscarProvincia(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provincia no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Provincia buscarProvinciaPorNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Provincia no encontrada: " + nombre));
    }

    public void modificarProvincia(String id, String nombre, String idPais) {
        Provincia actual = buscarProvincia(id);
        Pais pais = paisServicio.buscarPais(idPais);
        
        if (!actual.getNombre().equalsIgnoreCase(nombre)) {
            validar(nombre, pais);
            actual.setNombre(nombre);
        }
        actual.setPais(pais);
        repository.save(actual);
    }

    public void eliminarProvincia(String id) {
        Provincia actual = buscarProvincia(id);
        actual.setEliminado(true);
        repository.save(actual);
    }

    @Transactional(readOnly = true)
    public List<Provincia> listarProvicnia(String idPais) {
        return repository.findByPaisId(idPais);
    }

    @Transactional(readOnly = true)
    public List<Provincia> listarProvicniaActiva(String idPais) {
        return repository.findByPaisId(idPais).stream()
            .filter(e -> !e.isEliminado())
            .toList();
    }

    @Transactional(readOnly = true)
    public List<Provincia> listarTodasLasProvincias() {
        return repository.findAll();
    }
}
