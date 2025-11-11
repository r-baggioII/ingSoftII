package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.ClienteDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.AlquilerDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ClienteDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Cliente que verifica los datos antes de invocar a la
 * API del servidor.
 */
@Service
public class ClienteService extends BaseClientService<ClienteDTO, String> {

    private static final Logger logger = LoggerFactory.getLogger(ClienteService.class);

    private final ClienteDao clienteDao;

    public ClienteService(ClienteDao dao) {
        super(dao);
        this.clienteDao = dao;
    }

    @Override
    protected void validar(BaseUseCaseService useCase, ClienteDTO cliente) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (cliente == null) {
            throw new ErrorServiceException("Debe indicar el cliente");
        }

        // Validar datos de Persona (heredados)
        validarPersona(cliente);

        // Validar dirección de estadía (opcional)
        if (StringUtils.hasText(cliente.getDireccionEstadia())) {
            cliente.setDireccionEstadia(cliente.getDireccionEstadia().trim());
            if (cliente.getDireccionEstadia().length() > 500) {
                throw new ErrorServiceException("La dirección de estadía no puede exceder los 500 caracteres");
            }
        }

        // Validar que tenga al menos una dirección
        if (cliente.getDireccionIds() == null || cliente.getDireccionIds().isEmpty()) {
            throw new ErrorServiceException("El cliente debe tener al menos una dirección asociada");
        }

        // Validar que tenga al menos una nacionalidad
        if (cliente.getNacionalidadIds() == null || cliente.getNacionalidadIds().isEmpty()) {
            throw new ErrorServiceException("El cliente debe tener al menos una nacionalidad");
        }

        if (Boolean.TRUE.equals(cliente.getEliminado())) {
            throw new ErrorServiceException("El cliente indicado se encuentra eliminado");
        }
    }

    /**
     * Validaciones comunes de Persona
     */
    private void validarPersona(ClienteDTO persona) throws ErrorServiceException {
        if (!StringUtils.hasText(persona.getNombre())) {
            throw new ErrorServiceException("Debe indicar el nombre");
        }
        persona.setNombre(persona.getNombre().trim());

        if (!StringUtils.hasText(persona.getApellido())) {
            throw new ErrorServiceException("Debe indicar el apellido");
        }
        persona.setApellido(persona.getApellido().trim());

        if (persona.getFechaNacimiento() == null) {
            throw new ErrorServiceException("Debe indicar la fecha de nacimiento");
        }

        if (persona.getTipoDocumento() == null) {
            throw new ErrorServiceException("Debe indicar el tipo de documento");
        }

        if (!StringUtils.hasText(persona.getNumeroDocumento())) {
            throw new ErrorServiceException("Debe indicar el número de documento");
        }
        persona.setNumeroDocumento(persona.getNumeroDocumento().trim());
    }

    public List<AlquilerDTO> listarAlquileresPorCliente(String clienteId) {
        try {
            return clienteDao.obtenerAlquileresPorCliente(clienteId);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Endpoint /api/clientes/{}/alquileres no disponible o cliente inexistente: {}", clienteId, e.getStatusText());
            return Collections.emptyList();
        } catch (ErrorServiceException e) {
            logger.error("No se pudieron obtener los alquileres del cliente {}", clienteId, e);
            return Collections.emptyList();
        }
    }

    public List<AlquilerDTO> listarAlquileresPendientesFactura(String clienteId) {
        try {
            return clienteDao.obtenerAlquileresPendientesFactura(clienteId);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Endpoint /api/clientes/{}/alquileres/pendientes-factura no disponible o cliente inexistente: {}", clienteId, e.getStatusText());
            return Collections.emptyList();
        } catch (ErrorServiceException e) {
            logger.error("No se pudieron obtener los alquileres pendientes de facturación del cliente {}", clienteId, e);
            return Collections.emptyList();
        }
    }
}
