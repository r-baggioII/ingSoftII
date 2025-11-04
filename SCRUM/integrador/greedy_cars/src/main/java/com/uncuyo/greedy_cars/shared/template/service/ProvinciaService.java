package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.ProvinciaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Provincia;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.ProvinciaMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ProvinciaRepository;

@Service
public class ProvinciaService extends BaseService<Provincia, Long> {
    
    private final ProvinciaMapper provinciaMapper;
    
	public ProvinciaService(ProvinciaRepository repository, ProvinciaMapper provinciaMapper) {
        super(repository);
        this.provinciaMapper = provinciaMapper;
    }
    
    // Métodos con DTOs
    public List<ProvinciaDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Provincia> provincias = listarActivos();
            return provinciaMapper.toDTOList(provincias);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar provincias: " + e.getMessage());
        }
    }
    
    public Optional<ProvinciaDTO> obtenerDTO(Long id) throws ErrorServiceException {
        try {
            Optional<Provincia> provincia = obtener(id);
            return provincia.map(provinciaMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener provincia: " + e.getMessage());
        }
    }
    
    public ProvinciaDTO altaDTO(ProvinciaDTO provinciaDTO) throws ErrorServiceException {
        try {
            Provincia provincia = provinciaMapper.toEntity(provinciaDTO);
            Provincia provinciaGuardada = alta(provincia);
            return provinciaMapper.toDTO(provinciaGuardada);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear provincia: " + e.getMessage());
        }
    }
    
    public Optional<ProvinciaDTO> modificarDTO(Long id, ProvinciaDTO provinciaDTO) throws ErrorServiceException {
        try {
            Provincia provincia = provinciaMapper.toEntity(provinciaDTO);
            Optional<Provincia> provinciaModificada = modificar(id, provincia);
            return provinciaModificada.map(provinciaMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar provincia: " + e.getMessage());
        }
    }
    
	
	@Override
	protected void actualizarEntidad(Provincia entidadExistente, Provincia entidadNueva) {
		entidadExistente.setNombre(entidadNueva.getNombre());
		entidadExistente.setPais(entidadNueva.getPais());
	}
	
	@Override
	protected void validar(BaseUseCaseService useCase, Provincia provincia) throws ErrorServiceException {
		
	   try {	
		
		  if (useCase != BaseUseCaseService.BAJA) { 
			  
		   if (provincia == null) {
			   throw new ErrorServiceException("Debe indicar la provincia");  
		   }
		   
		   if (provincia.getNombre() == null || provincia.getNombre().trim().isEmpty()) {
			  throw new ErrorServiceException("Debe indicar el nombre de la provincia");
		   }
		   
		   if (provincia.getEliminado()) {
			  throw new ErrorServiceException("La provincia indicada se encuentra eliminada");	   
		   }
		   
		   if (provincia.getPais() == null || provincia.getPais().getEliminado()) {
			  throw new ErrorServiceException("El país de la provincia indicado es incorrecto");
		   }
		   
		   Provincia provinciaExsitente = ((ProvinciaRepository) repository).buscarProvinciaPorPaisYNombre(provincia.getPais().getId(), provincia.getNombre());
		   if ((provinciaExsitente != null && !provinciaExsitente.getEliminado() && useCase == BaseUseCaseService.ALTA) ||
		       (provinciaExsitente != null && !provinciaExsitente.getEliminado() && !provinciaExsitente.getId().equals(provincia.getId()) && useCase == BaseUseCaseService.MODIFICACION)   
		      ) {
		       throw new ErrorServiceException("Existe una provincia con el nombre indicado");    
		   }
		  } 
		   
	   }catch(ErrorServiceException e) {	 
		 throw e;  
	   }catch(Exception e) {
		 throw new ErrorServiceException("Error de Sistemas");  
	   }  
	}

    public List<Provincia> listarProvinciaPorPaisActivo(Long id) throws ErrorServiceException {
      try {	
    	  
    	if (id == null) {
		   throw new ErrorServiceException("Debe indicar el país");  
		}
    	
        return ((ProvinciaRepository)repository).listarProvinciaActiva(id);
        
      }catch(ErrorServiceException e) {	 
 		 throw e;  
 	  }catch(Exception e) {
 		 throw new ErrorServiceException("Error de Sistemas");  
 	  }  
    }
    
    public List<ProvinciaDTO> listarProvinciaPorPaisActivoDTO(Long id) throws ErrorServiceException {
      try {	
    	  
    	if (id == null) {
		   throw new ErrorServiceException("Debe indicar el país");  
		}
    	
        List<Provincia> provincias = ((ProvinciaRepository)repository).listarProvinciaActiva(id);
        return provinciaMapper.toDTOList(provincias);
        
      }catch(ErrorServiceException e) {	 
 		 throw e;  
 	  }catch(Exception e) {
 		 throw new ErrorServiceException("Error de Sistemas");  
 	  }  
    }
    
}