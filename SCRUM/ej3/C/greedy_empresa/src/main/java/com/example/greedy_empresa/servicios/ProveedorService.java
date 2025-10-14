package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Proveedor;
import com.example.greedy_empresa.entidades.ProveedorPersona;
import com.example.greedy_empresa.repositorios.ProveedorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Proveedor que implementa el patrón Template Method.
 * Hereda la estructura común de BaseService y sobrescribe los hooks
 * para implementar lógica específica de Proveedor.
 */
@Service
public class ProveedorService extends BaseService<Proveedor, ProveedorRepository> {

    public ProveedorService(ProveedorRepository proveedorRepository) {
        super(proveedorRepository);
    }

    // ========== Implementación de métodos abstractos ==========

    @Override
    public Class<Proveedor> getEntityClass() {
        return Proveedor.class;
    }

    @Override
    protected String getEntityName() {
        return "Proveedor";
    }

    // ========== Sobrescritura de hooks para lógica específica ==========

    /**
     * Hook sobrescrito: Búsqueda por CUIT
     */
    @Override
    protected Page<Proveedor> buscarConFiltro(String filtro, Pageable pageable) {
        return repositorio.findByCuitContainingIgnoreCaseAndEliminadoFalse(filtro, pageable);
    }

    /**
     * Hook sobrescrito: Validar que el CUIT sea obligatorio
     */
    @Override
    protected void validarEntidad(Proveedor proveedor) {
        super.validarEntidad(proveedor);
        if (proveedor.getCuit() == null || proveedor.getCuit().isBlank()) {
            throw new IllegalArgumentException("El CUIT es obligatorio");
        }
        if (proveedor.getPersona() == null) {
            throw new IllegalArgumentException("Los datos de persona son obligatorios");
        }
    }

    /**
     * Hook sobrescrito: Normalizar CUIT (trim)
     */
    @Override
    protected void normalizarDatos(Proveedor proveedor) {
        String cuitNormalizado = proveedor.getCuit().trim();
        proveedor.setCuit(cuitNormalizado);
    }

    /**
     * Hook sobrescrito: Validar que no exista otro proveedor con el mismo CUIT
     */
    @Override
    protected void validarUnicidad(Proveedor proveedor) {
        repositorio.findByCuitIgnoreCaseAndEliminadoFalse(proveedor.getCuit())
                .filter(existente -> !existente.getId().equals(proveedor.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un proveedor con ese CUIT");
                });
    }

    /**
     * Hook sobrescrito: Procesar persona y direcciones asociadas
     */
    @Override
    protected void procesarRelaciones(Proveedor proveedor) {
        // Crear o actualizar persona
        if (proveedor.getPersona() != null) {
            if (proveedor.getPersona().getId() == null) {
                // Crear nueva persona concreta
                ProveedorPersona persona = new ProveedorPersona();
                persona.setNombre(proveedor.getPersona().getNombre());
                persona.setApellido(proveedor.getPersona().getApellido());
                persona.setCorreoElectronico(proveedor.getPersona().getCorreoElectronico());
                persona.setTelefono(proveedor.getPersona().getTelefono());
                persona.setEliminado(false);
                proveedor.setPersona(persona);
            }
        }

        // Procesar direcciones
        if (proveedor.getDirecciones() != null) {
            proveedor.getDirecciones().forEach(direccion -> {
                if (direccion != null) {
                    direccion.setProveedor(proveedor);
                    direccion.setPersona(proveedor.getPersona());
                    direccion.setEliminado(false);
                }
            });
            // Remover direcciones nulas o vacías
            proveedor.getDirecciones().removeIf(direccion -> 
                direccion == null || 
                (direccion.getCalle() == null || direccion.getCalle().isBlank()) ||
                (direccion.getNumero() == null || direccion.getNumero().isBlank()) ||
                direccion.getLocalidad() == null
            );
        }
    }

    /**
     * Hook sobrescrito: Actualizar campos de proveedor existente
     */
    @Override
    protected void actualizarCampos(Proveedor existente, Proveedor nueva) {
        existente.setCuit(nueva.getCuit());
        existente.setPersona(nueva.getPersona());
        existente.getDirecciones().clear();
        if (nueva.getDirecciones() != null) {
            existente.getDirecciones().addAll(nueva.getDirecciones());
        }
    }

    /**
     * Hook sobrescrito: Configurar persona antes de crear
     */
    @Override
    protected Proveedor crearNuevaEntidad(Proveedor proveedor) {
        if (proveedor.getPersona() != null) {
            proveedor.getPersona().setEliminado(false);
        }
        return super.crearNuevaEntidad(proveedor);
    }

    // ========== Métodos adicionales específicos de Proveedor ==========

    public long contarActivos() {
        return repositorio.countByEliminadoFalse();
    }

    public List<Proveedor> obtenerTodosParaPdf() {
        return repositorio.findByEliminadoFalseOrderByCuit();
    }
}
