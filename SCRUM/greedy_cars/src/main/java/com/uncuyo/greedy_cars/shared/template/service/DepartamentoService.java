package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.DepartamentoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Departamento;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.DepartamentoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.DepartamentoRepository;

@Service
public class DepartamentoService extends BaseService<Departamento, Long> {

	private final DepartamentoMapper departamentoMapper;

	public DepartamentoService(DepartamentoRepository repository, DepartamentoMapper departamentoMapper) {
		super(repository);
		this.departamentoMapper = departamentoMapper;
	}

	// Métodos con DTOs
	public List<DepartamentoDTO> listarActivosDTO() throws ErrorServiceException {
		try {
			List<Departamento> departamentos = listarActivos();
			return departamentoMapper.toDTOList(departamentos);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al listar departamentos: " + e.getMessage());
		}
	}

	public Optional<DepartamentoDTO> obtenerDTO(Long id) throws ErrorServiceException {
		try {
			Optional<Departamento> departamento = obtener(id);
			return departamento.map(departamentoMapper::toDTO);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al obtener departamento: " + e.getMessage());
		}
	}

	public DepartamentoDTO altaDTO(DepartamentoDTO departamentoDTO) throws ErrorServiceException {
		try {
			Departamento departamento = departamentoMapper.toEntity(departamentoDTO);
			Departamento departamentoGuardado = alta(departamento);
			return departamentoMapper.toDTO(departamentoGuardado);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al crear departamento: " + e.getMessage());
		}
	}

	public Optional<DepartamentoDTO> modificarDTO(Long id, DepartamentoDTO departamentoDTO) throws ErrorServiceException {
		try {
			Departamento departamento = departamentoMapper.toEntity(departamentoDTO);
			Optional<Departamento> departamentoModificado = modificar(id, departamento);
			return departamentoModificado.map(departamentoMapper::toDTO);
		} catch (Exception e) {
			throw new ErrorServiceException("Error al modificar departamento: " + e.getMessage());
		}
	}

	@Override
	protected void actualizarEntidad(Departamento entidadExistente, Departamento entidadNueva) {
		entidadExistente.setNombre(entidadNueva.getNombre());
		entidadExistente.setProvincia(entidadNueva.getProvincia());
	}

	@Override
	protected void validar(BaseUseCaseService useCase, Departamento departamento) throws ErrorServiceException {

		try {

			if (useCase != BaseUseCaseService.BAJA) {

				if (departamento == null) {
					throw new ErrorServiceException("Debe indicar el departamento");
				}

				if (departamento.getNombre() == null || departamento.getNombre().trim().isEmpty()) {
					throw new ErrorServiceException("Debe indicar el nombre del departamento");
				}

				if (departamento.getEliminado()) {
					throw new ErrorServiceException("El departamento indicado se encuentra eliminado");
				}

				if (departamento.getProvincia() == null) {
					throw new ErrorServiceException("Debe indicar la provincia");
				}

				Departamento departamentoExsitente = ((DepartamentoRepository) repository)
						.buscarDepartamentoPorProvinciaYNombre(departamento.getProvincia().getId(),
								departamento.getNombre());
				if ((departamentoExsitente != null && !departamentoExsitente.getEliminado()
						&& useCase == BaseUseCaseService.ALTA)
						|| (departamentoExsitente != null && !departamentoExsitente.getEliminado()
								&& !departamentoExsitente.getId().equals(departamento.getId())
								&& useCase == BaseUseCaseService.MODIFICACION)) {
					throw new ErrorServiceException("Existe un departamento con el nombre indicado");
				}
			}

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ErrorServiceException("Error de Sistemas");
		}
	}

	public List<Departamento> listarDepartamentoPorProvinciaActivo(Long id) throws ErrorServiceException {
		try {

			if (id == null) {
				throw new ErrorServiceException("Debe indicar la provincia");
			}

			return ((DepartamentoRepository) repository).listarDepartamentoActivo(id);

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ErrorServiceException("Error de Sistemas");
		}
	}

	public List<DepartamentoDTO> listarDepartamentoPorProvinciaActivoDTO(Long id) throws ErrorServiceException {
		try {

			if (id == null) {
				throw new ErrorServiceException("Debe indicar la provincia");
			}

			List<Departamento> departamentos = ((DepartamentoRepository) repository).listarDepartamentoActivo(id);
			return departamentoMapper.toDTOList(departamentos);

		} catch (ErrorServiceException e) {
			throw e;
		} catch (Exception e) {
			throw new ErrorServiceException("Error de Sistemas");
		}
	}

}
