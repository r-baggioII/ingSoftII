package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.EmpresaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Empresa;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.EmpresaMapper;
import com.uncuyo.greedy_cars.shared.template.repository.BaseRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmpresaService extends BaseService<Empresa, String> {

    private final EmpresaMapper empresaMapper;
    private final DireccionRepository direccionRepository;
    private final ContactoRepository contactoRepository;

    @Autowired
    public EmpresaService(
            BaseRepository<Empresa, String> repository, 
            EmpresaMapper empresaMapper,
            DireccionRepository direccionRepository,
            ContactoRepository contactoRepository) {
        super(repository);
        this.empresaMapper = empresaMapper;
        this.direccionRepository = direccionRepository;
        this.contactoRepository = contactoRepository;
    }

    // Métodos con DTOs
    @Transactional(readOnly = true)
    public List<EmpresaDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Empresa> empresas = listarActivos();
            return empresaMapper.toDTOList(empresas);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar empresas: " + e.getMessage());
        }
    }
    
    @Transactional(readOnly = true)
    public Optional<EmpresaDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<Empresa> empresa = obtener(id);
            return empresa.map(empresaMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener empresa: " + e.getMessage());
        }
    }
    
    public EmpresaDTO altaDTO(EmpresaDTO empresaDTO) throws ErrorServiceException {
        try {
            Empresa empresa = empresaMapper.toEntity(empresaDTO);
            
            // Las listas de direcciones y contactos ya están mapeadas por el mapper
            // El mapper utiliza los repositorios para resolver las relaciones
            
            Empresa empresaGuardada = alta(empresa);
            return empresaMapper.toDTO(empresaGuardada);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear empresa: " + e.getMessage());
        }
    }
    
    public Optional<EmpresaDTO> modificarDTO(String id, EmpresaDTO empresaDTO) throws ErrorServiceException {
        try {
            Empresa empresa = empresaMapper.toEntity(empresaDTO);
            
            // Las listas de direcciones y contactos ya están mapeadas por el mapper
            // El mapper utiliza los repositorios para resolver las relaciones
            
            Optional<Empresa> empresaModificada = modificar(id, empresa);
            return empresaModificada.map(empresaMapper::toDTO);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar empresa: " + e.getMessage());
        }
    }

    @Override
    protected void actualizarEntidad(Empresa entidadExistente, Empresa entidadNueva) {
        if (entidadNueva.getNombre() != null) {
            entidadExistente.setNombre(entidadNueva.getNombre());
        }
        
        if (entidadNueva.getDirecciones() != null && !entidadNueva.getDirecciones().isEmpty()) {
            entidadExistente.setDirecciones(entidadNueva.getDirecciones());
        }
        
        if (entidadNueva.getContactos() != null && !entidadNueva.getContactos().isEmpty()) {
            entidadExistente.setContactos(entidadNueva.getContactos());
        }
    }
}
