package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uncuyo.greedy_cars.shared.template.dto.EmpresaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Empresa;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.EmpresaMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoCorreoElectronicoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoTelefonicoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import com.uncuyo.greedy_cars.shared.template.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService extends BaseService<Empresa, String> {

    private final EmpresaMapper empresaMapper;
    private final DireccionRepository direccionRepository;
    private final ContactoCorreoElectronicoRepository contactoCorreoRepository;
    private final ContactoTelefonicoRepository contactoTelefonicoRepository;

    @Autowired
    public EmpresaService(EmpresaRepository repository, 
                         EmpresaMapper empresaMapper,
                         DireccionRepository direccionRepository,
                         ContactoCorreoElectronicoRepository contactoCorreoRepository,
                         ContactoTelefonicoRepository contactoTelefonicoRepository) {
        super(repository);
        this.empresaMapper = empresaMapper;
        this.direccionRepository = direccionRepository;
        this.contactoCorreoRepository = contactoCorreoRepository;
        this.contactoTelefonicoRepository = contactoTelefonicoRepository;
    }

    // Métodos con DTOs
    public List<EmpresaDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Empresa> empresas = listarActivos();
            return empresaMapper.toDTOList(empresas);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar empresas: " + e.getMessage());
        }
    }

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
            
            // Buscar y asociar las direcciones existentes
            if (empresaDTO.getDireccionIds() != null && !empresaDTO.getDireccionIds().isEmpty()) {
                List<Direccion> direccionesExistentes = empresaDTO.getDireccionIds().stream()
                    .map(id -> direccionRepository.findById(id)
                        .orElseThrow(() -> new ErrorServiceException("Dirección no encontrada con ID: " + id)))
                    .collect(Collectors.toList());
                
                empresa.setDirecciones(direccionesExistentes);
            }
            
            // Buscar y asociar los contactos existentes (si se proporcionan)
            if (empresaDTO.getContactoIds() != null && !empresaDTO.getContactoIds().isEmpty()) {
                List<Contacto> contactosExistentes = empresaDTO.getContactoIds().stream()
                    .map(id -> {
                        // Buscar en ambos repositorios de contacto
                        Optional<Contacto> contacto = contactoCorreoRepository.findById(id)
                            .map(c -> (Contacto) c);
                        if (contacto.isEmpty()) {
                            contacto = contactoTelefonicoRepository.findById(id)
                                .map(c -> (Contacto) c);
                        }
                        return contacto.orElseThrow(() -> 
                            new ErrorServiceException("Contacto no encontrado con ID: " + id));
                    })
                    .collect(Collectors.toList());
                
                empresa.setContactos(contactosExistentes);
                // Establecer la relación bidireccional
                contactosExistentes.forEach(contacto -> contacto.setEmpresa(empresa));
            }
            
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
            
            // Buscar y asociar las direcciones existentes
            if (empresaDTO.getDireccionIds() != null && !empresaDTO.getDireccionIds().isEmpty()) {
                List<Direccion> direccionesExistentes = empresaDTO.getDireccionIds().stream()
                    .map(direccionId -> direccionRepository.findById(direccionId)
                        .orElseThrow(() -> new ErrorServiceException("Dirección no encontrada con ID: " + direccionId)))
                    .collect(Collectors.toList());
                
                empresa.setDirecciones(direccionesExistentes);
            }
            
            // Buscar y asociar los contactos existentes (si se proporcionan)
            if (empresaDTO.getContactoIds() != null && !empresaDTO.getContactoIds().isEmpty()) {
                List<Contacto> contactosExistentes = empresaDTO.getContactoIds().stream()
                    .map(contactoId -> {
                        // Buscar en ambos repositorios de contacto
                        Optional<Contacto> contacto = contactoCorreoRepository.findById(contactoId)
                            .map(c -> (Contacto) c);
                        if (contacto.isEmpty()) {
                            contacto = contactoTelefonicoRepository.findById(contactoId)
                                .map(c -> (Contacto) c);
                        }
                        return contacto.orElseThrow(() -> 
                            new ErrorServiceException("Contacto no encontrado con ID: " + contactoId));
                    })
                    .collect(Collectors.toList());
                
                empresa.setContactos(contactosExistentes);
                // Establecer la relación bidireccional
                contactosExistentes.forEach(contacto -> contacto.setEmpresa(empresa));
            }
            
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
        
        // Actualizar direcciones si se proporcionan
        if (entidadNueva.getDirecciones() != null) {
            entidadExistente.getDirecciones().clear();
            entidadExistente.getDirecciones().addAll(entidadNueva.getDirecciones());
        }
        
        // Actualizar contactos si se proporcionan
        if (entidadNueva.getContactos() != null) {
            entidadExistente.getContactos().clear();
            entidadExistente.getContactos().addAll(entidadNueva.getContactos());
        }
    }
}
