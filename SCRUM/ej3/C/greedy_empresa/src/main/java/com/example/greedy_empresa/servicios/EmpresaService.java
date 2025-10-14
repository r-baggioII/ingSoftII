package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Empresa;
import com.example.greedy_empresa.repositorios.EmpresaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Empresa que implementa el patrón Template Method.
 * Hereda la estructura común de BaseService y sobrescribe los hooks
 * para implementar lógica específica de Empresa.
 */
@Service
public class EmpresaService extends BaseService<Empresa, EmpresaRepository> {

    public EmpresaService(EmpresaRepository empresaRepository) {
        super(empresaRepository);
    }

    // ========== Implementación de métodos abstractos ==========

    @Override
    public Class<Empresa> getEntityClass() {
        return Empresa.class;
    }

    @Override
    protected String getEntityName() {
        return "Empresa";
    }

    // ========== Sobrescritura de hooks para lógica específica ==========

    /**
     * Hook sobrescrito: Búsqueda por razón social
     */
    @Override
    protected Page<Empresa> buscarConFiltro(String filtro, Pageable pageable) {
        return repositorio.findByRazonSocialContainingIgnoreCaseAndEliminadoFalse(filtro, pageable);
    }

    /**
     * Hook sobrescrito: Validar que la razón social sea obligatoria
     */
    @Override
    protected void validarEntidad(Empresa empresa) {
        super.validarEntidad(empresa);
        if (empresa.getRazonSocial() == null || empresa.getRazonSocial().isBlank()) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }
    }

    /**
     * Hook sobrescrito: Normalizar razón social (trim)
     */
    @Override
    protected void normalizarDatos(Empresa empresa) {
        String razonNormalizada = empresa.getRazonSocial().trim();
        empresa.setRazonSocial(razonNormalizada);
    }

    /**
     * Hook sobrescrito: Validar que no exista otra empresa con la misma razón social
     */
    @Override
    protected void validarUnicidad(Empresa empresa) {
        repositorio.findByRazonSocialIgnoreCaseAndEliminadoFalse(empresa.getRazonSocial())
                .filter(existente -> !existente.getId().equals(empresa.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe una empresa con esa razón social");
                });
    }

    /**
     * Hook sobrescrito: Procesar direcciones asociadas
     */
    @Override
    protected void procesarRelaciones(Empresa empresa) {
        if (empresa.getDirecciones() != null) {
            empresa.getDirecciones().forEach(direccion -> {
                if (direccion != null) {
                    direccion.setEmpresa(empresa);
                    direccion.setEliminado(false);
                }
            });
            // Remover direcciones nulas o incompletas
            empresa.getDirecciones().removeIf(direccion -> 
                direccion == null || 
                (direccion.getCalle() == null || direccion.getCalle().isBlank()) ||
                (direccion.getNumero() == null || direccion.getNumero().isBlank()) ||
                direccion.getLocalidad() == null
            );
        }
    }

    /**
     * Hook sobrescrito: Actualizar campos de empresa existente
     */
    @Override
    protected void actualizarCampos(Empresa existente, Empresa nueva) {
        existente.setRazonSocial(nueva.getRazonSocial());
        existente.getDirecciones().clear();
        if (nueva.getDirecciones() != null) {
            existente.getDirecciones().addAll(nueva.getDirecciones());
        }
    }

    // ========== Métodos adicionales específicos de Empresa ==========

    public long contarActivas() {
        return repositorio.countByEliminadoFalse();
    }

    public List<Empresa> obtenerTodasParaExcel() {
        return repositorio.findByEliminadoFalseOrderByRazonSocial();
    }
}
