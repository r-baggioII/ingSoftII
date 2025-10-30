package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.entity.Provincia;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.ProvinciaRepository;

@Service
public class ProvinciaService extends BaseService<Provincia, Long> {
    
	public ProvinciaService(ProvinciaRepository repository) {
        super(repository);
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
    
}
