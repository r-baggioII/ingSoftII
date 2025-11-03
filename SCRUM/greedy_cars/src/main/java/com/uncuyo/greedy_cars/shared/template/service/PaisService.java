package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.PaisDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Pais;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.PaisMapper;
import com.uncuyo.greedy_cars.shared.template.repository.PaisRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PaisService extends BaseService<Pais, Long> {
    
    private final PaisMapper paisMapper;
    
	public PaisService(PaisRepository repository, PaisMapper paisMapper) {
        super(repository);
        this.paisMapper = paisMapper;
    }
    
    // Métodos con DTOs
    public List<PaisDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Pais> paises = listarActivos();
            return paisMapper.toDTOList(paises);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar países: " + e.getMessage());
        }
    }
    
    public Optional<PaisDTO> obtenerDTO(Long id) throws ErrorServiceException {
        try {
            Optional<Pais> pais = obtener(id);
            return pais.map(paisMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener país: " + e.getMessage());
        }
    }
    
    public PaisDTO altaDTO(PaisDTO paisDTO) throws ErrorServiceException {
        try {
            Pais pais = paisMapper.toEntity(paisDTO);
            Pais paisGuardado = alta(pais);
            return paisMapper.toDTO(paisGuardado);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear país: " + e.getMessage());
        }
    }
    
    public Optional<PaisDTO> modificarDTO(Long id, PaisDTO paisDTO) throws ErrorServiceException {
        try {
            Pais pais = paisMapper.toEntity(paisDTO);
            Optional<Pais> paisModificado = modificar(id, pais);
            return paisModificado.map(paisMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar país: " + e.getMessage());
        }
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
