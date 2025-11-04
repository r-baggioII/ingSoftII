package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.ClienteDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.ClienteMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ClienteRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService extends BaseService<Cliente, String> {

    private final ClienteMapper clienteMapper;

    public ClienteService(ClienteRepository repository, ClienteMapper clienteMapper) {
        super(repository);
        this.clienteMapper = clienteMapper;
    }

    public List<ClienteDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Cliente> clientes = listarActivos();
            return clienteMapper.toDTOList(clientes);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar clientes: " + e.getMessage());
        }
    }

    public Optional<ClienteDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<Cliente> c = obtener(id);
            return c.map(clienteMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener cliente: " + e.getMessage());
        }
    }

    public ClienteDTO altaDTO(ClienteDTO dto) throws ErrorServiceException {
        try {
            Cliente entidad = clienteMapper.toEntity(dto);
            Cliente guardado = alta(entidad);
            return clienteMapper.toDTO(guardado);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear cliente: " + e.getMessage());
        }
    }

    public Optional<ClienteDTO> modificarDTO(String id, ClienteDTO dto) throws ErrorServiceException {
        try {
            Cliente entidad = clienteMapper.toEntity(dto);
            Optional<Cliente> mod = modificar(id, entidad);
            return mod.map(clienteMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar cliente: " + e.getMessage());
        }
    }

    // Métodos solicitados
    public ClienteDTO crearCliente(String nombre, String apellido, java.time.LocalDate fechaNacimiento,
                                    com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento tipoDocumento,
                                    String numeroDocumento, String telefono, String correoElectronico,
                                    String nacionalidadId) throws ErrorServiceException {
        validarParametros(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico);

        ClienteDTO dto = new ClienteDTO();
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setFechaNacimiento(fechaNacimiento);
        dto.setTipoDocumento(tipoDocumento);
        dto.setNumeroDocumento(numeroDocumento);
        dto.setTelefono(telefono);
        dto.setCorreoElectronico(correoElectronico);
        dto.setNacionalidadId(nacionalidadId);

        return altaDTO(dto);
    }

    public void validar(String nombre, String apellido, java.time.LocalDate fechaNacimiento,
                        com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento tipoDocumento,
                        String numeroDocumento, String telefono, String correoElectronico) throws ErrorServiceException {
        validarParametros(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, telefono, correoElectronico);
    }

    public Optional<ClienteDTO> modificarCliente(String id, String nombre, String apellido, java.time.LocalDate fechaNacimiento,
                                                 com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento tipoDocumento,
                                                 String numeroDocumento, String direccionEstadia, String nacionalidadId) throws ErrorServiceException {
        validarParametros(nombre, apellido, fechaNacimiento, tipoDocumento, numeroDocumento, null, null);

        ClienteDTO dto = new ClienteDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setApellido(apellido);
        dto.setFechaNacimiento(fechaNacimiento);
        dto.setTipoDocumento(tipoDocumento);
        dto.setNumeroDocumento(numeroDocumento);
        dto.setDireccionEstadia(direccionEstadia);
        dto.setNacionalidadId(nacionalidadId);

        return modificarDTO(id, dto);
    }

    public java.util.Collection<Cliente> listarCliente() throws ErrorServiceException {
        return listarActivos();
    }

    public java.util.Collection<Cliente> listarClienteActivo() throws ErrorServiceException {
        return listarActivos();
    }

    public void asociarClienteUsuario(Cliente cliente, Object usuario) {
        throw new UnsupportedOperationException("asociarClienteUsuario no implementado: depende de la entidad Usuario del módulo de seguridad.");
    }

    private void validarParametros(String nombre, String apellido, java.time.LocalDate fechaNacimiento,
                                    com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento tipoDocumento,
                                    String numeroDocumento, String telefono, String correoElectronico) throws ErrorServiceException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el nombre del cliente");
        }
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new ErrorServiceException("Debe indicar el apellido del cliente");
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
    }

    @Override
    protected void actualizarEntidad(Cliente existente, Cliente nueva) {
        if (nueva.getNombre() != null) existente.setNombre(nueva.getNombre());
        if (nueva.getApellido() != null) existente.setApellido(nueva.getApellido());
        if (nueva.getDireccionEstadia() != null) existente.setDireccionEstadia(nueva.getDireccionEstadia());
        if (nueva.getNacionalidad() != null) existente.setNacionalidad(nueva.getNacionalidad());
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Cliente cliente) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {
                if (cliente == null) {
                    throw new ErrorServiceException("Debe indicar el cliente");
                }

                if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el nombre del cliente");
                }

                if (cliente.getApellido() == null || cliente.getApellido().trim().isEmpty()) {
                    throw new ErrorServiceException("Debe indicar el apellido del cliente");
                }

                if (cliente.getEliminado() != null && cliente.getEliminado()) {
                    throw new ErrorServiceException("El cliente indicado se encuentra eliminado");
                }

                Cliente existente = ((ClienteRepository) repository).findByNombreAndApellido(cliente.getNombre(), cliente.getApellido());

                if (existente != null && !existente.getEliminado() && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("Existe un cliente con el nombre y apellido indicados");
                }

                if (existente != null && !existente.getEliminado() && !existente.getId().equals(cliente.getId()) && useCase == BaseUseCaseService.MODIFICACION) {
                    throw new ErrorServiceException("Existe un cliente con el nombre y apellido indicados");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

}
