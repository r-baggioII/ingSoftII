package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.DireccionDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.DireccionMapper;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;

@Service
public class DireccionService extends BaseService<Direccion, Long> {
    
	private final DireccionMapper direccionMapper;

	public DireccionService(DireccionRepository repository, DireccionMapper direccionMapper) {
        super(repository);
        this.direccionMapper = direccionMapper;
    }
    
	// Métodos con DTOs
	public List<DireccionDTO> listarActivosDTO() throws ErrorServiceException {
		try {
			List<Direccion> direcciones = listarActivos();
			return direccionMapper.toDTOList(direcciones);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al listar direcciones: " + e.getMessage());
		}
	}

	public Optional<DireccionDTO> obtenerDTO(Long id) throws ErrorServiceException {
		try {
			Optional<Direccion> direccion = obtener(id);
			return direccion.map(direccionMapper::toDTO);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al obtener dirección: " + e.getMessage());
		}
	}

	public DireccionDTO altaDTO(DireccionDTO direccionDTO) throws ErrorServiceException {
		try {
			Direccion direccion = direccionMapper.toEntity(direccionDTO);
			Direccion direccionGuardada = alta(direccion);
			return direccionMapper.toDTO(direccionGuardada);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al crear dirección: " + e.getMessage());
		}
	}

	public Optional<DireccionDTO> modificarDTO(Long id, DireccionDTO direccionDTO) throws ErrorServiceException {
		try {
			Direccion direccion = direccionMapper.toEntity(direccionDTO);
			Optional<Direccion> direccionModificada = modificar(id, direccion);
			return direccionModificada.map(direccionMapper::toDTO);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al modificar dirección: " + e.getMessage());
		}
	}
    
	@Override
	protected void actualizarEntidad(Direccion entidadExistente, Direccion entidadNueva) {
		entidadExistente.setCalle(entidadNueva.getCalle());
		entidadExistente.setNumeracion(entidadNueva.getNumeracion());
		entidadExistente.setBarrio(entidadNueva.getBarrio());
		entidadExistente.setPisoCasa(entidadNueva.getPisoCasa());
		entidadExistente.setPuertaManzana(entidadNueva.getPuertaManzana());
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
