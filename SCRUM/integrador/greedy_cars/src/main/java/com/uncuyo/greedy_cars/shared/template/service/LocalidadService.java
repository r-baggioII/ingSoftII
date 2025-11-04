package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.LocalidadDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Localidad;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.LocalidadMapper;
import com.uncuyo.greedy_cars.shared.template.repository.LocalidadRepository;

@Service
public class LocalidadService extends BaseService<Localidad, Long> {
    
	private final LocalidadMapper localidadMapper;

	public LocalidadService(LocalidadRepository repository, LocalidadMapper localidadMapper) {
        super(repository);
        this.localidadMapper = localidadMapper;
    }
    
	// Métodos con DTOs
	public List<LocalidadDTO> listarActivosDTO() throws ErrorServiceException {
		try {
			List<Localidad> localidades = listarActivos();
			return localidadMapper.toDTOList(localidades);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al listar localidades: " + e.getMessage());
		}
	}

	public Optional<LocalidadDTO> obtenerDTO(Long id) throws ErrorServiceException {
		try {
			Optional<Localidad> localidad = obtener(id);
			return localidad.map(localidadMapper::toDTO);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al obtener localidad: " + e.getMessage());
		}
	}

	public LocalidadDTO altaDTO(LocalidadDTO localidadDTO) throws ErrorServiceException {
		try {
			Localidad localidad = localidadMapper.toEntity(localidadDTO);
			Localidad localidadGuardada = alta(localidad);
			return localidadMapper.toDTO(localidadGuardada);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al crear localidad: " + e.getMessage());
		}
	}

	public Optional<LocalidadDTO> modificarDTO(Long id, LocalidadDTO localidadDTO) throws ErrorServiceException {
		try {
			Localidad localidad = localidadMapper.toEntity(localidadDTO);
			Optional<Localidad> localidadModificada = modificar(id, localidad);
			return localidadModificada.map(localidadMapper::toDTO);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al modificar localidad: " + e.getMessage());
		}
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

	public List<Localidad> listarLocalidadPorDepartamentoActivo(Long id) throws ErrorServiceException {
		try {

			if (id == null) {
				throw new ErrorServiceException("Debe indicar el departamento");
			}

			return ((LocalidadRepository) repository).listarLocalidadActiva(id);

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ErrorServiceException("Error de Sistemas");
		}
	}

	public List<LocalidadDTO> listarLocalidadPorDepartamentoActivoDTO(Long id) throws ErrorServiceException {
		try {

			if (id == null) {
				throw new ErrorServiceException("Debe indicar el departamento");
			}

			List<Localidad> localidades = ((LocalidadRepository) repository).listarLocalidadActiva(id);
			return localidadMapper.toDTOList(localidades);

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ErrorServiceException("Error de Sistemas");
		}
	}
    
}
