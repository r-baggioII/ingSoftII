package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.entity.Localidad;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.LocalidadRepository;

@Service
public class LocalidadService extends BaseService<Localidad, Long> {
    
	public LocalidadService(LocalidadRepository repository) {
        super(repository);
    }
    
	@Override
	protected void actualizarEntidad(Localidad entidadExistente, Localidad entidadNueva) {
		entidadExistente.setNombre(entidadNueva.getNombre());
		entidadExistente.setCodigoPostal(entidadNueva.getCodigoPostal());
		entidadExistente.setDepartamento(entidadNueva.getDepartamento());
	}
	
	@Override
	protected void validar(BaseUseCaseService useCase, Localidad localidad) throws ErrorServiceException {
		
	   try {	
			 
		  if (useCase != BaseUseCaseService.BAJA) { 
			  
		   if (localidad == null) {
			   throw new ErrorServiceException("Debe indicar la localidad");  
		   }
		   
		   if (localidad.getNombre() == null || localidad.getNombre().trim().isEmpty()) {
			  throw new ErrorServiceException("Debe indicar el nombre de la localidad");
		   }
		   
		   if (localidad.getCodigoPostal() == null || localidad.getCodigoPostal().trim().isEmpty()) {
			  throw new ErrorServiceException("Debe indicar el nombre código postal");
		   }
		   
		   if (localidad.getDepartamento() == null || localidad.getDepartamento().getEliminado()) {
			  throw new ErrorServiceException("El departamento de la localidad indicada es incorrecto");
		   }
		   
		   Localidad localidadExsitente = ((LocalidadRepository) repository).buscarLocalidadPorDepartamentoYNombre(localidad.getDepartamento().getId(), localidad.getNombre());
		   if ((localidadExsitente != null && !localidadExsitente.getEliminado() && useCase == BaseUseCaseService.ALTA) ||
		       (localidadExsitente != null && !localidadExsitente.getEliminado() && !localidadExsitente.getId().equals(localidad.getId()) && useCase == BaseUseCaseService.MODIFICACION)   
		      ) {
		       throw new ErrorServiceException("Existe una localidad con el nombre indicado");    
		   }
		  } 
		   
	   }catch(ErrorServiceException e) {	 
		 throw e;  
	   }catch(Exception e) {
		 throw new ErrorServiceException("Error de Sistemas");  
	   }  
	}
    
}
