package com.uncuyo.greedy_cars.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.entity.Departamento;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.DepartamentoRepository;

@Service
public class DepartamentoService extends BaseService<Departamento, Long> {

	public DepartamentoService(DepartamentoRepository repository) {
		super(repository);
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

}
