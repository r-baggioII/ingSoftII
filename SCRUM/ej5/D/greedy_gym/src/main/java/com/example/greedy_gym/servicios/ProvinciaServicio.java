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
    public Provincia crearProvincia(@NotBlank String nombre, @NotBlank String idPais) {
        Pais pais = paisServicio.buscarPais(idPais);
        
        // Buscar si ya existe una provincia con este nombre en este país
        Provincia provinciaExistente = repository.findByNombreIgnoreCaseAndPaisId(nombre, idPais).orElse(null);
        
        if (provinciaExistente != null) {
            if (provinciaExistente.isEliminado()) {
                // Si existe pero está eliminada, la reactivamos
                provinciaExistente.setEliminado(false);
                provinciaExistente.setPais(pais); // Asegurar que el país esté actualizado
                return repository.save(provinciaExistente);
            } else {
                // Si existe y está activa, lanzamos error
                throw new ValidationException("La provincia ya existe: " + nombre);
            }
        }
        
        // Si no existe, creamos una nueva
        Provincia provincia = new Provincia();
        provincia.setNombre(nombre);
        provincia.setPais(pais);
        provincia.setEliminado(false);
        
        return repository.save(provincia);
    }

    public void validar(@NotBlank String nombre, Pais pais) {
        if (repository.existsByNombreIgnoreCaseAndPaisIdAndEliminadoFalse(nombre, pais.getId())) {
            throw new ValidationException("La provincia ya existe: " + nombre);
        }
    }

    @Transactional(readOnly = true)
    public Provincia buscarProvincia(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Provincia no encontrada: " + id));
    }

    @Transactional(readOnly = true)
    public Provincia buscarPorNombreYPais(String nombre, String idPais) {
        return repository.findByNombreIgnoreCaseAndPaisId(nombre, idPais).orElse(null);
    }

    @Transactional(readOnly = true)
    public Provincia buscarProvinciaPorNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Provincia no encontrada: " + nombre));
    }

    public void modificarProvincia(String id, String nombre, String idPais) {
        Provincia actual = buscarProvincia(id);
        Pais pais = paisServicio.buscarPais(idPais);
        
        if (!actual.getNombre().equalsIgnoreCase(nombre) || !actual.getPais().getId().equals(idPais)) {
            // Verificar que no exista otra provincia activa con el mismo nombre en el mismo país
            Provincia provinciaConMismoNombre = repository.findByNombreIgnoreCaseAndPaisId(nombre, idPais).orElse(null);
            if (provinciaConMismoNombre != null && !provinciaConMismoNombre.getId().equals(id) && !provinciaConMismoNombre.isEliminado()) {
                throw new ValidationException("La provincia ya existe: " + nombre);
            }
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
        return repository.findAllWithPais();
    }
}
