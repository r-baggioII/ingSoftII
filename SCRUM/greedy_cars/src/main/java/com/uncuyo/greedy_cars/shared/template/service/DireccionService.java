package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;

@Service
public class DireccionService extends BaseService<Direccion, Long> {
    
	public DireccionService(DireccionRepository repository) {
        super(repository);
    }
    
	@Override
	protected void actualizarEntidad(Direccion entidadExistente, Direccion entidadNueva) {
		entidadExistente.setCalle(entidadNueva.getCalle());
		entidadExistente.setNumeracion(entidadNueva.getNumeracion());
		entidadExistente.setBarrio(entidadNueva.getBarrio());
		entidadExistente.setPisoCasa(entidadNueva.getPisoCasa());
		entidadExistente.setPuertaManzana(entidadNueva.getPuertaManzana());
		entidadExistente.setUbicacionCoordenadaX(entidadNueva.getUbicacionCoordenadaX());
		entidadExistente.setUbicacionCoordenadaY(entidadNueva.getUbicacionCoordenadaY());
		entidadExistente.setObservacion(entidadNueva.getObservacion());
		entidadExistente.setLocalidad(entidadNueva.getLocalidad());
	}
	
	@Override
	protected void validar(BaseUseCaseService useCase, Direccion direccion) throws ErrorServiceException {
		
	   try {	
			 
		 if (useCase != BaseUseCaseService.BAJA) { 
			 
		   if (direccion == null) {
			  throw new ErrorServiceException("Debe indicar la dirección");	   
	       }
		   
		   if (direccion.getCalle() == null || direccion.getCalle().trim().isEmpty()){
               throw new ErrorServiceException("Debe indicar la calle");  
            }
            
            if (direccion.getNumeracion()== null || direccion.getNumeracion().trim().isEmpty()){
               throw new ErrorServiceException("Debe indicar la numeración");  
            }
            
            if (direccion.getBarrio() == null || direccion.getBarrio().trim().isEmpty()){
               throw new ErrorServiceException("Debe indicar el barrio");  
            }
            
            if (direccion.getPisoCasa() == null || direccion.getPisoCasa().trim().isEmpty()){
               throw new ErrorServiceException("Debe indicar el piso / casa");  
            }
           
            if (direccion.getPuertaManzana() == null || direccion.getPuertaManzana().trim().isEmpty()){
               throw new ErrorServiceException("Debe indicar el puerta / manzana");  
            }
            
            if (direccion.getUbicacionCoordenadaX() == null || direccion.getUbicacionCoordenadaX().trim().isEmpty()){
               throw new ErrorServiceException("Debe indicar la ubicaciónCoordenadaX");  
            }
            
            if (direccion.getUbicacionCoordenadaY() == null || direccion.getUbicacionCoordenadaY().trim().isEmpty()){
               throw new ErrorServiceException("Debe indicar la ubicaciónCoordenadaY");  
            }
            
            if (direccion.getLocalidad() == null) {
               throw new ErrorServiceException("Debe indicar la localidad");  
            }
		 } 
		   
	   }catch(ErrorServiceException e) {	 
		 throw e;  
	   }catch(Exception e) {
		 throw new ErrorServiceException("Error de Sistemas");  
	   }  
	}
    
}
