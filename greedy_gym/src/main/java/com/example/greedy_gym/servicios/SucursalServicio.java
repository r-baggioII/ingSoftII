package com.example.greedy_gym.servicios;

import com.example.greedy_gym.entidades.Direccion;
import com.example.greedy_gym.entidades.Empresa;
import com.example.greedy_gym.entidades.Sucursal;
import com.example.greedy_gym.repositorios.EmpresaRepositorio;
import com.example.greedy_gym.repositorios.SucursalRepositorio;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class SucursalServicio {

    private final SucursalRepositorio sucursalRepositorio;
    private final EmpresaRepositorio empresaRepositorio;

    public Sucursal crearSucursal(String nombre, String idEmpresa, Direccion direccion) {
        validar(nombre, direccion);
        String nombreNormalizado = nombre.trim();
        Empresa empresa = obtenerEmpresa(idEmpresa);
        verificarDuplicados(nombreNormalizado, empresa, null);
        Sucursal sucursal = new Sucursal();
        aplicarDatos(sucursal, nombreNormalizado, empresa, direccion);
        sucursal.setEliminado(false);
        return sucursalRepositorio.save(sucursal);
    }

    public Sucursal crearSucursal(Sucursal sucursal, String idEmpresa) {
        if (sucursal == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos de la sucursal son obligatorios");
        }
        return crearSucursal(sucursal.getNombre(), idEmpresa, sucursal.getDireccion());
    }

    public Sucursal modificarSucursal(String id, String nombre, String idEmpresa, Direccion direccion) {
        validar(nombre, direccion);
        String nombreNormalizado = nombre.trim();
        Empresa empresa = obtenerEmpresa(idEmpresa);
        Sucursal existente = sucursalRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
        verificarDuplicados(nombreNormalizado, empresa, id);
        aplicarDatos(existente, nombreNormalizado, empresa, direccion);
        return sucursalRepositorio.save(existente);
    }

    public Sucursal modificarSucursal(String id, Sucursal cambios, String idEmpresa) {
        if (cambios == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Los datos de la sucursal son obligatorios");
        }
        return modificarSucursal(id, cambios.getNombre(), idEmpresa, cambios.getDireccion());
    }

    @Transactional(readOnly = true)
    public Sucursal buscarSucursal(String id) {
        return sucursalRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
    }

    @Transactional(readOnly = true)
    public Sucursal buscarSucursalPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        return sucursalRepositorio.findByNombreIgnoreCaseAndEliminadoFalse(nombre.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
    }

    @Transactional(readOnly = true)
    public Sucursal buscarSucursalPorNombre(String nombre, String idEmpresa) {
        if (idEmpresa == null || idEmpresa.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idEmpresa es obligatorio");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        Empresa empresa = obtenerEmpresa(idEmpresa);
        return sucursalRepositorio.findByNombreIgnoreCaseAndEmpresaAndEliminadoFalse(nombre.trim(), empresa)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
    }

    public void eliminarSucursal(String id) {
        Sucursal sucursal = sucursalRepositorio.findByIdAndEliminadoFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sucursal no encontrada"));
        sucursal.setEliminado(true);
        sucursalRepositorio.save(sucursal);
    }

    @Transactional(readOnly = true)
    public List<Sucursal> listarSucursal() {
        return sucursalRepositorio.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Sucursal> listarSucursalActiva() {
        return sucursalRepositorio.findByEliminadoFalseOrderByNombreAsc();
    }

    public void validar(String nombre, Direccion direccion) {
        if (nombre == null || nombre.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }
        if (direccion == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La dirección es obligatoria");
        }
        if (direccion.getCalle() == null || direccion.getCalle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La calle es obligatoria");
        }
        if (direccion.getNumero() == null || direccion.getNumero().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El número es obligatorio");
        }
        if (direccion.getLocalidad() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La localidad es obligatoria");
        }
        if (direccion.getLocalidad().getDepartamento() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El departamento es obligatorio");
        }
        if (direccion.getLocalidad().getDepartamento().getProvincia() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La provincia es obligatoria");
        }
        if (direccion.getLocalidad().getDepartamento().getProvincia().getPais() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El país es obligatorio");
        }
    }

    private Empresa obtenerEmpresa(String idEmpresa) {
        if (idEmpresa == null || idEmpresa.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idEmpresa es obligatorio");
        }
        return empresaRepositorio.findByIdAndEliminadoFalse(idEmpresa.trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empresa no encontrada"));
    }

    private void verificarDuplicados(String nombre, Empresa empresa, String idActual) {
        sucursalRepositorio.findByNombreIgnoreCaseAndEmpresaAndEliminadoFalse(nombre, empresa)
                .filter(s -> !Objects.equals(s.getId(), idActual))
                .ifPresent(s -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "La empresa ya tiene una sucursal con ese nombre");
                });
        sucursalRepositorio.findByNombreIgnoreCaseAndEliminadoFalse(nombre)
                .filter(s -> !Objects.equals(s.getId(), idActual))
                .ifPresent(s -> {
                    if (Objects.equals(s.getEmpresa().getId(), empresa.getId())) {
                        return;
                    }
                    throw new ResponseStatusException(HttpStatus.CONFLICT,
                            "Ya existe una sucursal activa con ese nombre");
                });
    }

    private void aplicarDatos(Sucursal sucursal, String nombre, Empresa empresa, Direccion direccion) {
        sucursal.setNombre(nombre.trim());
        sucursal.setEmpresa(empresa);
        if (sucursal.getDireccion() == null) {
            sucursal.setDireccion(clonarDireccion(direccion));
        } else {
            copiarDireccion(sucursal.getDireccion(), direccion);
        }
    }

    private Direccion clonarDireccion(Direccion origen) {
        Direccion direccion = new Direccion();
        copiarDireccion(direccion, origen);
        return direccion;
    }

    private void copiarDireccion(Direccion destino, Direccion origen) {
        destino.setCalle(origen.getCalle().trim());
        destino.setNumero(origen.getNumero().trim());
        destino.setLocalidad(origen.getLocalidad());
        destino.setCodigoPostal(origen.getCodigoPostal() != null ? origen.getCodigoPostal().trim() : null);
        destino.setBarrio(origen.getBarrio() != null ? origen.getBarrio().trim() : null);
        destino.setManzanaPiso(origen.getManzanaPiso() != null ? origen.getManzanaPiso().trim() : null);
        destino.setCasaDepartamento(origen.getCasaDepartamento() != null ? origen.getCasaDepartamento().trim() : null);
        destino.setReferencia(origen.getReferencia() != null ? origen.getReferencia().trim() : null);
    }
}
