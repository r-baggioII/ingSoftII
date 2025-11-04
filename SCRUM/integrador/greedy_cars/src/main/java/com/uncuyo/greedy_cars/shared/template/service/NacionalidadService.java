package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.NacionalidadDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Nacionalidad;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.NacionalidadMapper;
import com.uncuyo.greedy_cars.shared.template.repository.NacionalidadRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NacionalidadService extends BaseService<Nacionalidad, String> {

    private final NacionalidadMapper mapper;

    public NacionalidadService(NacionalidadRepository repository, NacionalidadMapper mapper) {
        super(repository);
        this.mapper = mapper;
    }

    // DTO methods
    public List<NacionalidadDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Nacionalidad> items = listarActivos();
            return mapper.toDTOList(items);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar nacionalidades: " + e.getMessage());
        }
    }

    public Optional<NacionalidadDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<Nacionalidad> opt = obtener(id);
            return opt.map(mapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener nacionalidad: " + e.getMessage());
        }
    }

    public NacionalidadDTO altaDTO(NacionalidadDTO dto) throws ErrorServiceException {
        try {
            Nacionalidad ent = mapper.toEntity(dto);
            Nacionalidad creado = alta(ent);
            return mapper.toDTO(creado);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear nacionalidad: " + e.getMessage());
        }
    }

    public Optional<NacionalidadDTO> modificarDTO(String id, NacionalidadDTO dto) throws ErrorServiceException {
        try {
            Nacionalidad ent = mapper.toEntity(dto);
            Optional<Nacionalidad> mod = modificar(id, ent);
            return mod.map(mapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar nacionalidad: " + e.getMessage());
        }
    }

    @Override
    protected void actualizarEntidad(Nacionalidad existente, Nacionalidad nueva) {
        if (nueva.getNombre() != null) {
            existente.setNombre(nueva.getNombre());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Nacionalidad nacionalidad) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {
                if (nacionalidad == null) {
                    throw new ErrorServiceException("Debe indicar la nacionalidad");
                }
                if (nacionalidad.getNombre() == null || nacionalidad.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre de la nacionalidad");
                }
                if (Boolean.TRUE.equals(nacionalidad.getEliminado())) {
                    throw new ErrorServiceException("La nacionalidad indicada se encuentra eliminada");
                }

                Nacionalidad existente = ((NacionalidadRepository) repository).buscarNacionalidadPorNombre(nacionalidad.getNombre());
                if ((existente != null && !existente.getEliminado() && useCase == BaseUseCaseService.ALTA) ||
                        (existente != null && !existente.getEliminado() && !existente.getId().equals(nacionalidad.getId()) && useCase == BaseUseCaseService.MODIFICACION)) {
                    throw new ErrorServiceException("Existe una nacionalidad con el nombre indicado");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    // Convenience helper methods matching requested API
    public Nacionalidad buscarNacionalidad(String id) throws ErrorServiceException {
        return obtenerEntidad(id);
    }

    public Nacionalidad buscarNacionalidadPorNombre(String nombre) {
        return ((NacionalidadRepository) repository).buscarNacionalidadPorNombre(nombre);
    }

    public java.util.Collection<Nacionalidad> listarNacionalidad() throws ErrorServiceException {
        return listarActivos();
    }

    public java.util.Collection<Nacionalidad> listarNacionalidadActiva() throws ErrorServiceException {
        return listarActivos();
    }

}
