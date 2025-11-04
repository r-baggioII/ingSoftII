package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.DocumentacionDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Documentacion;
import com.uncuyo.greedy_cars.shared.template.entity.TipoDocumentacion;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.DocumentacionMapper;
import com.uncuyo.greedy_cars.shared.template.repository.DocumentacionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DocumentacionService extends BaseService<Documentacion, String> {
    
    private final DocumentacionMapper documentacionMapper;
    
    public DocumentacionService(DocumentacionRepository repository, DocumentacionMapper documentacionMapper) {
        super(repository);
        this.documentacionMapper = documentacionMapper;
    }
    
    // Métodos con DTOs
    public List<DocumentacionDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Documentacion> documentaciones = listarActivos();
            return documentacionMapper.toDTOList(documentaciones);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar documentaciones: " + e.getMessage());
        }
    }
    
    public Optional<DocumentacionDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<Documentacion> documentacion = obtener(id);
            return documentacion.map(documentacionMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener documentación: " + e.getMessage());
        }
    }
    
    public DocumentacionDTO altaDTO(DocumentacionDTO documentacionDTO) throws ErrorServiceException {
        try {
            Documentacion documentacion = documentacionMapper.toEntity(documentacionDTO);
            // Generar ID si no existe
            if (documentacion.getId() == null || documentacion.getId().trim().isEmpty()) {
                documentacion.setId(UUID.randomUUID().toString());
            }
            Documentacion documentacionGuardada = alta(documentacion);
            return documentacionMapper.toDTO(documentacionGuardada);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear documentación: " + e.getMessage());
        }
    }
    
    public Optional<DocumentacionDTO> modificarDTO(String id, DocumentacionDTO documentacionDTO) throws ErrorServiceException {
        try {
            Documentacion documentacion = documentacionMapper.toEntity(documentacionDTO);
            Optional<Documentacion> documentacionModificada = modificar(id, documentacion);
            return documentacionModificada.map(documentacionMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar documentación: " + e.getMessage());
        }
    }
    
    // Métodos personalizados
    public List<DocumentacionDTO> buscarPorTipoDTO(TipoDocumentacion tipo) throws ErrorServiceException {
        try {
            List<Documentacion> documentaciones = ((DocumentacionRepository) repository).buscarPorTipo(tipo);
            return documentacionMapper.toDTOList(documentaciones);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al buscar documentaciones por tipo: " + e.getMessage());
        }
    }
    
    @Override
    protected void actualizarEntidad(Documentacion entidadExistente, Documentacion entidadNueva) {
        entidadExistente.setTipoDocumentacion(entidadNueva.getTipoDocumentacion());
        entidadExistente.setObservacion(entidadNueva.getObservacion());
        entidadExistente.setPathArchivo(entidadNueva.getPathArchivo());
        entidadExistente.setNombreArchivo(entidadNueva.getNombreArchivo());
    }
    
    @Override
    protected void validar(BaseUseCaseService useCase, Documentacion documentacion) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {
                
                if (documentacion == null) {
                    throw new ErrorServiceException("Debe indicar la documentación");
                }
                
                if (documentacion.getTipoDocumentacion() == null) {
                    throw new ErrorServiceException("Debe indicar el tipo de documentación");
                }
                
                if (documentacion.getPathArchivo() == null || documentacion.getPathArchivo().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el path del archivo");
                }
                
                if (documentacion.getNombreArchivo() == null || documentacion.getNombreArchivo().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre del archivo");
                }
                
                if (documentacion.getPathArchivo().length() > 500) {
                    throw new ErrorServiceException("El path del archivo no puede exceder 500 caracteres");
                }
                
                if (documentacion.getNombreArchivo().length() > 255) {
                    throw new ErrorServiceException("El nombre del archivo no puede exceder 255 caracteres");
                }
                
                if (documentacion.getObservacion() != null && documentacion.getObservacion().length() > 500) {
                    throw new ErrorServiceException("La observación no puede exceder 500 caracteres");
                }
                
                if (documentacion.getEliminado()) {
                    throw new ErrorServiceException("La documentación indicada se encuentra eliminada");
                }
                
                // Validar que no exista otra documentación con el mismo nombre de archivo (solo en ALTA)
                Documentacion documentacionExistente = ((DocumentacionRepository) repository)
                    .buscarPorNombreArchivo(documentacion.getNombreArchivo());
                    
                if ((documentacionExistente != null && !documentacionExistente.getEliminado() && useCase == BaseUseCaseService.ALTA) ||
                    (documentacionExistente != null && !documentacionExistente.getEliminado() && 
                     !documentacionExistente.getId().equals(documentacion.getId()) && useCase == BaseUseCaseService.MODIFICACION)) {
                    throw new ErrorServiceException("Existe una documentación con el mismo nombre de archivo");
                }
            }
            
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }
}
