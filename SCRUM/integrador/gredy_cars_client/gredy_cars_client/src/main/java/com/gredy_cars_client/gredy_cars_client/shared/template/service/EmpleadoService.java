package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.EmpleadoDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpleadoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Empleado que verifica los datos antes de invocar a la
 * API del servidor.
 */
@Service
public class EmpleadoService extends BaseClientService<EmpleadoDTO, String> {

    public EmpleadoService(EmpleadoDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, EmpleadoDTO empleado) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (empleado == null) {
            throw new ErrorServiceException("Debe indicar el empleado");
        }

        // Validar datos de Persona (heredados)
        validarPersona(empleado);

        // Validar tipo de empleado
        if (empleado.getTipoEmpleado() == null) {
            throw new ErrorServiceException("Debe indicar el tipo de empleado");
        }

        if (Boolean.TRUE.equals(empleado.getEliminado())) {
            throw new ErrorServiceException("El empleado indicado se encuentra eliminado");
        }
    }

    /**
     * Validaciones comunes de Persona
     */
    private void validarPersona(EmpleadoDTO empleado) throws ErrorServiceException {
        if (!StringUtils.hasText(empleado.getNombre())) {
            throw new ErrorServiceException("Debe indicar el nombre");
        }
        empleado.setNombre(empleado.getNombre().trim());

        if (!StringUtils.hasText(empleado.getApellido())) {
            throw new ErrorServiceException("Debe indicar el apellido");
        }
        empleado.setApellido(empleado.getApellido().trim());

        if (empleado.getFechaNacimiento() == null) {
            throw new ErrorServiceException("Debe indicar la fecha de nacimiento");
        }

        if (empleado.getTipoDocumento() == null) {
            throw new ErrorServiceException("Debe indicar el tipo de documento");
        }

        if (!StringUtils.hasText(empleado.getNumeroDocumento())) {
            throw new ErrorServiceException("Debe indicar el número de documento");
        }
        empleado.setNumeroDocumento(empleado.getNumeroDocumento().trim());
    }
}
