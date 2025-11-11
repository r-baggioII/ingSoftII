package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.PersonaDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ContactoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DireccionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PersonaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoDocumento;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Persona que verifica los datos antes de invocar a la
 * API del servidor.
 */
@Service
public class PersonaService extends BaseClientService<PersonaDTO, String> {

    private static final int EDAD_MINIMA = 18;
    private static final int EDAD_MAXIMA = 120;

    public PersonaService(PersonaDao dao) {
        super(dao);
    }

    @Override
    protected void validar(BaseUseCaseService useCase, PersonaDTO persona) throws ErrorServiceException {

        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }

        if (persona == null) {
            throw new ErrorServiceException("Debe indicar la persona");
        }

        // Validar nombre
        if (!StringUtils.hasText(persona.getNombre())) {
            throw new ErrorServiceException("Debe indicar el nombre");
        }
        persona.setNombre(persona.getNombre().trim());

        if (persona.getNombre().length() > 120) {
            throw new ErrorServiceException("El nombre no puede exceder los 120 caracteres");
        }

        // Validar apellido
        if (!StringUtils.hasText(persona.getApellido())) {
            throw new ErrorServiceException("Debe indicar el apellido");
        }
        persona.setApellido(persona.getApellido().trim());

        if (persona.getApellido().length() > 120) {
            throw new ErrorServiceException("El apellido no puede exceder los 120 caracteres");
        }

        // Validar fecha de nacimiento
        if (persona.getFechaNacimiento() == null) {
            throw new ErrorServiceException("Debe indicar la fecha de nacimiento");
        }

        if (persona.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new ErrorServiceException("La fecha de nacimiento no puede ser futura");
        }

        // Validar edad
        int edad = Period.between(persona.getFechaNacimiento(), LocalDate.now()).getYears();
        if (edad < EDAD_MINIMA) {
            throw new ErrorServiceException("La persona debe tener al menos " + EDAD_MINIMA + " años");
        }

        if (edad > EDAD_MAXIMA) {
            throw new ErrorServiceException("La edad no puede ser mayor a " + EDAD_MAXIMA + " años");
        }

        // Validar tipo de documento
        if (persona.getTipoDocumento() == null) {
            throw new ErrorServiceException("Debe indicar el tipo de documento");
        }

        // Validar número de documento
        if (!StringUtils.hasText(persona.getNumeroDocumento())) {
            throw new ErrorServiceException("Debe indicar el número de documento");
        }
        persona.setNumeroDocumento(persona.getNumeroDocumento().trim());

        if (persona.getNumeroDocumento().length() > 20) {
            throw new ErrorServiceException("El número de documento no puede exceder los 20 caracteres");
        }

        // Validar formato del documento según el tipo
        validarFormatoDocumento(persona.getTipoDocumento(), persona.getNumeroDocumento());

        // Validar documento duplicado (solo en alta o si cambió el documento)
        if (useCase == BaseUseCaseService.ALTA) {
            validarDocumentoDuplicado(persona.getNumeroDocumento(), null);
        } else if (useCase == BaseUseCaseService.MODIFICACION && persona.getId() != null) {
            validarDocumentoDuplicado(persona.getNumeroDocumento(), persona.getId());
        }

        // Validar contactos
        if (persona.getContactos() != null) {
            for (ContactoDTO contacto : persona.getContactos()) {
                if (contacto.getTipoContacto() == null) {
                    throw new ErrorServiceException("Todos los contactos deben tener un tipo");
                }
                if (contacto.getCanalContacto() == null) {
                    throw new ErrorServiceException("Todos los contactos deben tener un canal");
                }
            }
        }

        // Validar direcciones
        if (persona.getDirecciones() != null) {
            for (DireccionDTO direccion : persona.getDirecciones()) {
                if (direccion.getId() == null) {
                    throw new ErrorServiceException("Todas las direcciones deben tener un ID válido");
                }
            }
        }

        // Validar eliminado
        if (Boolean.TRUE.equals(persona.getEliminado())) {
            throw new ErrorServiceException("La persona indicada se encuentra eliminada");
        }
    }

    /**
     * Valida el formato del número de documento según su tipo
     */
    private void validarFormatoDocumento(TipoDocumento tipo, String numeroDocumento) throws ErrorServiceException {
        if (tipo == TipoDocumento.DNI) {
            // DNI debe ser numérico y tener entre 7 y 8 dígitos
            if (!numeroDocumento.matches("\\d{7,8}")) {
                throw new ErrorServiceException("El DNI debe contener entre 7 y 8 dígitos numéricos");
            }
        } else if (tipo == TipoDocumento.PASAPORTE) {
            // Pasaporte: formato alfanumérico
            if (!numeroDocumento.matches("[A-Z0-9]{6,9}")) {
                throw new ErrorServiceException("El pasaporte debe tener entre 6 y 9 caracteres alfanuméricos en mayúsculas");
            }
        }
        // Otros tipos no tienen validación de formato específica
    }

    /**
     * Valida que no exista otra persona con el mismo número de documento
     */
    private void validarDocumentoDuplicado(String numeroDocumento, String personaIdActual) throws ErrorServiceException {
        try {
            List<PersonaDTO> personas = listarActivos();
            boolean duplicado = personas.stream()
                .anyMatch(p -> p.getNumeroDocumento().equalsIgnoreCase(numeroDocumento)
                    && (personaIdActual == null || !p.getId().equals(personaIdActual)));

            if (duplicado) {
                throw new ErrorServiceException("Ya existe una persona con el número de documento " + numeroDocumento);
            }
        } catch (ErrorServiceException e) {
            // Si el error es el de duplicado, lo relanzamos
            if (e.getMessage().contains("Ya existe una persona")) {
                throw e;
            }
            // Otros errores al listar personas se ignoran para no bloquear la validación
        }
    }
}
