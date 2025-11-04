package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.EmpleadoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Empleado;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.EmpleadoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.EmpleadoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService extends BaseService<Empleado, String> {

    private final EmpleadoMapper empleadoMapper;

    public EmpleadoService(EmpleadoRepository repository, EmpleadoMapper empleadoMapper) {
        super(repository);
        this.empleadoMapper = empleadoMapper;
    }

    // Métodos con DTOs
    public List<EmpleadoDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Empleado> empleados = listarActivos();
            return empleadoMapper.toDTOList(empleados);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar empleados: " + e.getMessage());
        }
    }

    public Optional<EmpleadoDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<Empleado> empleado = obtener(id);
            return empleado.map(empleadoMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener empleado: " + e.getMessage());
        }
    }

    public EmpleadoDTO altaDTO(EmpleadoDTO empleadoDTO) throws ErrorServiceException {
        try {
            Empleado empleado = empleadoMapper.toEntity(empleadoDTO);
            Empleado guardado = alta(empleado);
            return empleadoMapper.toDTO(guardado);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear empleado: " + e.getMessage());
        }
    }

    public Optional<EmpleadoDTO> modificarDTO(String id, EmpleadoDTO empleadoDTO) throws ErrorServiceException {
        try {
            Empleado empleado = empleadoMapper.toEntity(empleadoDTO);
            Optional<Empleado> modificado = modificar(id, empleado);
            return modificado.map(empleadoMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar empleado: " + e.getMessage());
        }
    }

    // Métodos adicionales solicitados
    public EmpleadoDTO crearEmpleado(String nombre, String apellido, java.time.LocalDate fechaNacimiento,
                                     com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento tipoDocumento,
                                     String numeroDocumento, String telefono, String correoElectronico,
                                     com.uncuyo.greedy_cars.shared.template.enums.TipoEmpleado tipoEmpleado) throws ErrorServiceException {
        // validar parámetros
        validarParametros(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico, tipoEmpleado);

        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setFechaNacimiento(fechaNacimiento);
        dto.setTipoDocumento(tipoDocumento);
        dto.setNumeroDocumento(numeroDocumento);
        dto.setTelefono(telefono);
        dto.setCorreoElectronico(correoElectronico);
        dto.setTipoEmpleado(tipoEmpleado);

        return altaDTO(dto);
    }

    public void validar(String nombre, String apellido, java.time.LocalDate fechaNacimiento,
                        com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento tipoDocumento,
                        String numeroDocumento, String telefono, String correoElectronico,
                        com.uncuyo.greedy_cars.shared.template.enums.TipoEmpleado tipoEmpleado) throws ErrorServiceException {
        validarParametros(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico, tipoEmpleado);
    }

    public Optional<EmpleadoDTO> modificarEmpleado(String id, String nombre, String apellido, java.time.LocalDate fechaNacimiento,
                                                   com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento tipoDocumento,
                                                   String numeroDocumento, com.uncuyo.greedy_cars.shared.template.enums.TipoEmpleado tipoEmpleado) throws ErrorServiceException {
        validarParametros(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, null, null, tipoEmpleado);

        EmpleadoDTO dto = new EmpleadoDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setFechaNacimiento(fechaNacimiento);
        dto.setTipoDocumento(tipoDocumento);
        dto.setNumeroDocumento(numeroDocumento);
        dto.setTipoEmpleado(tipoEmpleado);

        return modificarDTO(id, dto);
    }

    public java.util.Collection<Empleado> listarEmpleado() throws ErrorServiceException {
        return listarActivos();
    }

    public java.util.Collection<Empleado> listarEmpleadoActivo() throws ErrorServiceException {
        return listarActivos();
    }

    public void asociarEmpleadoUsuario(Empleado empleado, Object usuario) {
        // No hay definición de Usuario en este módulo; dejar hook para implementar la asociación.
        throw new UnsupportedOperationException("asociarEmpleadoUsuario no implementado: depende de la entidad Usuario del módulo de seguridad.");
    }

    // helper para validar parámetros primitivos (mismo comportamiento que validar())
    private void validarParametros(String nombre, String apellido, java.time.LocalDate fechaNacimiento,
                                    com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento tipoDocumento,
                                    String numeroDocumento, String telefono, String correoElectronico,
                                    com.uncuyo.greedy_cars.shared.template.enums.TipoEmpleado tipoEmpleado) throws ErrorServiceException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el nombre del empleado");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el apellido del empleado");
        }
        if (fechaNacimiento == null) {
            throw new ErrorServiceException("Debe indicar la fecha de nacimiento");
        }
        if (tipoDocumento == null) {
            throw new ErrorServiceException("Debe indicar el tipo de documento");
        }
        if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el número de documento");
        }
        if (tipoEmpleado == null) {
            throw new ErrorServiceException("Debe indicar el tipo de empleado");
        }
    }

    @Override
    protected void actualizarEntidad(Empleado entidadExistente, Empleado entidadNueva) {
        if (entidadNueva.getNombre() != null) {
            entidadExistente.setNombre(entidadNueva.getNombre());
        }
        if (entidadNueva.getApellido() != null) {
            entidadExistente.setApellido(entidadNueva.getApellido());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Empleado empleado) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {
                if (empleado == null) {
                    throw new ErrorServiceException("Debe indicar el empleado");
                }

                if (empleado.getNombre() == null || empleado.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre del empleado");
                }

                if (empleado.getApellido() == null || empleado.getApellido().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el apellido del empleado");
                }

                if (empleado.getEliminado() != null && empleado.getEliminado()) {
                    throw new ErrorServiceException("El empleado indicado se encuentra eliminado");
                }

                Empleado existente = ((EmpleadoRepository) repository).findByNombreAndApellido(empleado.getNombre(), empleado.getApellido());

                if (existente != null && !existente.getEliminado() && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe un empleado con el nombre y apellido indicados");
                }

                if (existente != null && !existente.getEliminado() && !existente.getId().equals(empleado.getId()) && useCase == BaseUseCaseService.MODIFICACION) {
                    throw new ErrorServiceException("Existe un empleado con el nombre y apellido indicados");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

}
