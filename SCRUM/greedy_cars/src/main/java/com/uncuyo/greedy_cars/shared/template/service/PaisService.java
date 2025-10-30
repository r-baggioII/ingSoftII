package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.entity.Pais;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.PaisRepository;

@Service
public class PaisService extends BaseService<Pais, Long> {
    
	public PaisService(PaisRepository repository) {
        super(repository);
    }
    
	@Override
	protected void actualizarEntidad(Pais entidadExistente, Pais entidadNueva) {
		entidadExistente.setNombre(entidadNueva.getNombre());
	}
	
	@Override
	protected void validar(BaseUseCaseService useCase,  Pais pais) throws ErrorServiceException {
		
	   try {	
			 
		 if (useCase != BaseUseCaseService.BAJA) { 
			 
		   if (pais == null) {
			  throw new ErrorServiceException("Debe indicar el país");	   
	       }
		   
		   if (pais.getNombre() == null || pais.getNombre().trim().isEmpty()) {
			  throw new ErrorServiceException("Debe indicar el nombre del país");
		   }
		   
		   if (pais.getEliminado()) {
		      throw new ErrorServiceException("El país indicado se encuentra eliminado");	   
		   }
		   
		   Pais paisExsitente = ((PaisRepository) repository).buscarPaisPorNombre(pais.getNombre());
		   if ((paisExsitente != null && !paisExsitente.getEliminado() && useCase == BaseUseCaseService.ALTA) ||
			   (paisExsitente != null && !paisExsitente.getEliminado() && !paisExsitente.getId().equals(pais.getId()) && useCase == BaseUseCaseService.MODIFICACION)   
			  ) {
			   throw new ErrorServiceException("Existe un país con el nombre indicado");    
           }
		 } 
		   
	   }catch(ErrorServiceException e) {	 
		 throw e;  
	   }catch(Exception e) {
		 throw new ErrorServiceException("Error de Sistemas");  
	   }  
	}
    
}
