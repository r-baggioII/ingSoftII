package com.uncuyo.greedy_cars.service;

import com.uncuyo.greedy_cars.shared.auth0.dto.RegistroIntermedioDTO;
import com.uncuyo.greedy_cars.shared.template.entity.*;
import com.uncuyo.greedy_cars.shared.template.enums.Rol;
import com.uncuyo.greedy_cars.shared.template.enums.TipoContacto;
import com.uncuyo.greedy_cars.shared.template.enums.TipoDocumento;
import com.uncuyo.greedy_cars.shared.template.enums.TipoTelefono;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

/**
 * Servicio para completar el registro de usuarios autenticados con Auth0.
 * Similar a RegistroService pero adaptado para usuarios externos (Google, etc.)
 */
@Slf4j
@Service
public class Auth0RegistrationService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final NacionalidadRepository nacionalidadRepository;
    private final PaisRepository paisRepository;
    private final ProvinciaRepository provinciaRepository;
    private final DepartamentoRepository departamentoRepository;
    private final LocalidadRepository localidadRepository;
    private final DireccionRepository direccionRepository;
    private final ContactoRepository contactoRepository;
    private final ImagenRepository imagenRepository;

    @Autowired
    public Auth0RegistrationService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            NacionalidadRepository nacionalidadRepository,
            PaisRepository paisRepository,
            ProvinciaRepository provinciaRepository,
            DepartamentoRepository departamentoRepository,
            LocalidadRepository localidadRepository,
            DireccionRepository direccionRepository,
            ContactoRepository contactoRepository,
            ImagenRepository imagenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.nacionalidadRepository = nacionalidadRepository;
        this.paisRepository = paisRepository;
        this.provinciaRepository = provinciaRepository;
        this.departamentoRepository = departamentoRepository;
        this.localidadRepository = localidadRepository;
        this.direccionRepository = direccionRepository;
        this.contactoRepository = contactoRepository;
        this.imagenRepository = imagenRepository;
    }

    /**
     * Crea un nuevo cliente con autenticación Auth0.
     * El usuario no tendrá contraseña local ya que se autentica externamente.
     * 
     * @param dto DTO con todos los datos del registro
     * @param externalId ID del usuario en Auth0 (sub claim del JWT)
     * @return El usuario creado con su cliente asociado
     * @throws ErrorServiceException si ocurre algún error durante el registro
     */
    @Transactional
    public Usuario crearClienteConAuth0(RegistroIntermedioDTO dto, String externalId) throws ErrorServiceException {
        log.info("Iniciando registro Auth0 para externalId: {}", externalId);

        try {
            // 1. Validar que no exista un usuario con este externalId
            if (usuarioRepository.findByExternalIdAndEliminadoIsFalse(externalId).isPresent()) {
                throw new ErrorServiceException("Ya existe un usuario con este externalId");
            }

            // 2. Validar que el email no esté en uso (extraerlo de los contactos)
            String email = extraerEmail(dto.getContactos());
            if (email != null && usuarioRepository.findByEmailAndEliminadoIsFalse(email).isPresent()) {
                throw new ErrorServiceException("El email '" + email + "' ya está en uso");
            }

            // 3. Validar que el número de documento no exista
            validarDocumentoUnico(dto.getNumeroDocumento());

            // 4. Crear o buscar Nacionalidad
            Nacionalidad nacionalidad = obtenerOCrearNacionalidad(dto.getNacionalidad());

            // 5. Crear la estructura geográfica y dirección
            Direccion direccion = crearDireccion(dto.getDireccion());

            // 6. Crear Cliente
            Cliente cliente = crearCliente(dto, direccion);
            cliente.addNacionalidad(nacionalidad);

            // 7. Crear y asociar contactos al cliente
            crearYAsociarContactos(dto.getContactos(), cliente);

            // 8. Crear y asociar imagen si existe
            if (dto.getImagen() != null && dto.getImagen().getContenidoBase64() != null) {
                crearYAsociarImagen(dto.getImagen().getContenidoBase64(), cliente);
            }

            // 9. Guardar el cliente
            cliente = clienteRepository.save(cliente);
            log.info("Cliente guardado con ID: {}", cliente.getId());

            // 10. Crear Usuario con Auth0
            Usuario usuario = crearUsuarioAuth0(email, cliente, externalId);
            usuario = usuarioRepository.save(usuario);
            log.info("Usuario Auth0 creado con ID: {}", usuario.getId());

            // 11. Asociar el usuario al cliente
            cliente.setUsuario(usuario);
            clienteRepository.save(cliente);

            log.info("Registro Auth0 completado para: {} {}", cliente.getNombre(), cliente.getApellido());
            return usuario;

        } catch (ErrorServiceException e) {
            log.error("Error en el registro Auth0: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante el registro Auth0", e);
            throw new ErrorServiceException("Error al registrar el cliente: " + e.getMessage());
        }
    }

    /**
     * Extrae el email del primer contacto de tipo correo electrónico
     */
    private String extraerEmail(List<RegistroIntermedioDTO.ContactoDTO> contactos) {
        if (contactos == null || contactos.isEmpty()) {
            return null;
        }
        
        return contactos.stream()
            .filter(c -> "EMAIL".equalsIgnoreCase(c.getTipoContacto()) || "CORREO".equalsIgnoreCase(c.getTipoContacto()))
            .map(RegistroIntermedioDTO.ContactoDTO::getMail)
            .findFirst()
            .orElse(null);
    }

    /**
     * Valida que el número de documento no esté registrado
     */
    private void validarDocumentoUnico(String numeroDocumento) throws ErrorServiceException {
        if (clienteRepository.existsByNumeroDocumentoAndEliminadoIsFalse(numeroDocumento)) {
            throw new ErrorServiceException("El número de documento '" + numeroDocumento + "' ya está registrado");
        }
    }

    /**
     * Obtiene una nacionalidad existente o crea una nueva
     */
    private Nacionalidad obtenerOCrearNacionalidad(String nombreNacionalidad) {
        Nacionalidad nacionalidad = nacionalidadRepository.buscarNacionalidadPorNombre(nombreNacionalidad);
        
        if (nacionalidad == null) {
            log.info("Creando nueva nacionalidad: {}", nombreNacionalidad);
            nacionalidad = new Nacionalidad();
            nacionalidad.setNombre(nombreNacionalidad);
            nacionalidad.setEliminado(false);
            nacionalidad = nacionalidadRepository.save(nacionalidad);
        }
        
        return nacionalidad;
    }

    /**
     * Crea la estructura geográfica completa y la dirección
     */
    private Direccion crearDireccion(RegistroIntermedioDTO.DireccionDTO direccionDTO) throws ErrorServiceException {
        // 1. Buscar o crear País
        Pais pais = paisRepository.buscarPaisPorNombre(direccionDTO.getPais());
        if (pais == null) {
            pais = new Pais();
            pais.setNombre(direccionDTO.getPais());
            pais.setEliminado(false);
            pais = paisRepository.save(pais);
            log.info("País creado: {}", pais.getNombre());
        }

        // 2. Buscar o crear Provincia
        Provincia provincia = provinciaRepository.buscarProvinciaPorNombre(direccionDTO.getProvincia());
        if (provincia == null) {
            provincia = new Provincia();
            provincia.setNombre(direccionDTO.getProvincia());
            provincia.setPais(pais);
            provincia.setEliminado(false);
            provincia = provinciaRepository.save(provincia);
            log.info("Provincia creada: {}", provincia.getNombre());
        }

        // 3. Buscar o crear Departamento
        Departamento departamento = departamentoRepository.buscarDepartamentoPorNombre(direccionDTO.getDepartamento());
        if (departamento == null) {
            departamento = new Departamento();
            departamento.setNombre(direccionDTO.getDepartamento());
            departamento.setProvincia(provincia);
            departamento.setEliminado(false);
            departamento = departamentoRepository.save(departamento);
            log.info("Departamento creado: {}", departamento.getNombre());
        }

        // 4. Buscar o crear Localidad
        Localidad localidad = localidadRepository.buscarLocalidadPorNombre(direccionDTO.getLocalidad());
        if (localidad == null) {
            localidad = new Localidad();
            localidad.setNombre(direccionDTO.getLocalidad());
            localidad.setDepartamento(departamento);
            localidad.setCodigoPostal(direccionDTO.getCodigoPostal());
            localidad.setEliminado(false);
            localidad = localidadRepository.save(localidad);
            log.info("Localidad creada: {}", localidad.getNombre());
        }

        // 5. Crear Dirección
        Direccion direccion = new Direccion();
        direccion.setCalle(direccionDTO.getCalle());
        direccion.setNumeracion(direccionDTO.getNumeracion());
        direccion.setPisoCasa(direccionDTO.getPisoCasa());
        direccion.setPuertaManzana(direccionDTO.getPuertaManzana());
        direccion.setBarrio(direccionDTO.getBarrio());
        direccion.setObservacion(direccionDTO.getObservacion());
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);
        direccion = direccionRepository.save(direccion);
        log.info("Dirección creada: {} {}", direccion.getCalle(), direccion.getNumeracion());

        return direccion;
    }

    /**
     * Crea la entidad Cliente
     */
    private Cliente crearCliente(RegistroIntermedioDTO dto, Direccion direccion) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        
        // Parse fecha nacimiento
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        cliente.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento(), formatter));
        
        // Tipo documento
        cliente.setTipoDocumento(TipoDocumento.valueOf(dto.getTipoDocumento()));
        cliente.setNumeroDocumento(dto.getNumeroDocumento());
        cliente.setDireccionEstadia(dto.getDireccionEstadia());
        cliente.setEliminado(false);
        
        // Agregar dirección a la lista
        cliente.getDirecciones().add(direccion);
        
        return cliente;
    }

    /**
     * Crea y asocia los contactos al cliente
     */
    private void crearYAsociarContactos(List<RegistroIntermedioDTO.ContactoDTO> contactosDTO, Cliente cliente) {
        if (contactosDTO == null || contactosDTO.isEmpty()) {
            return;
        }
        
        for (RegistroIntermedioDTO.ContactoDTO contactoDTO : contactosDTO) {
            if ("EMAIL".equalsIgnoreCase(contactoDTO.getTipoContacto()) || 
                "CORREO".equalsIgnoreCase(contactoDTO.getTipoContacto())) {
                
                ContactoCorreoElectronico contactoEmail = new ContactoCorreoElectronico();
                contactoEmail.setTipoContacto(TipoContacto.PERSONAL);
                contactoEmail.setMail(contactoDTO.getMail());
                contactoEmail.setObservacion(contactoDTO.getObservacion());
                contactoEmail.setEliminado(false);
                contactoEmail = (ContactoCorreoElectronico) contactoRepository.save(contactoEmail);
                cliente.addContacto(contactoEmail);
                log.info("Contacto EMAIL creado: {}", contactoDTO.getMail());
                
            } else if ("TELEFONO".equalsIgnoreCase(contactoDTO.getTipoContacto())) {
                
                ContactoTelefonico contactoTelefono = new ContactoTelefonico();
                contactoTelefono.setTipoContacto(TipoContacto.PERSONAL);
                contactoTelefono.setTelefono(contactoDTO.getTelefono());
                
                // Parsear tipo de teléfono
                if (contactoDTO.getTipoTelefono() != null) {
                    contactoTelefono.setTipoTelefono(TipoTelefono.valueOf(contactoDTO.getTipoTelefono()));
                } else {
                    contactoTelefono.setTipoTelefono(TipoTelefono.CELULAR);
                }
                
                contactoTelefono.setObservacion(contactoDTO.getObservacion());
                contactoTelefono.setEliminado(false);
                contactoTelefono = (ContactoTelefonico) contactoRepository.save(contactoTelefono);
                cliente.addContacto(contactoTelefono);
                log.info("Contacto TELEFONO creado: {}", contactoDTO.getTelefono());
            }
        }
    }

    /**
     * Crea y asocia la imagen al cliente
     */
    private void crearYAsociarImagen(String imagenBase64, Cliente cliente) {
        try {
            // Decodificar Base64
            byte[] imagenBytes = Base64.getDecoder().decode(imagenBase64);
            
            Imagen imagen = new Imagen();
            imagen.setNombre("perfil_" + cliente.getNumeroDocumento());
            imagen.setContenido(imagenBytes);
            imagen.setEliminado(false);
            
            // Agregar a la lista de imágenes del cliente
            cliente.getImagenes().add(imagen);
            
            log.info("Imagen de perfil creada para el cliente");
        } catch (Exception e) {
            log.warn("Error al guardar la imagen: {}", e.getMessage());
            // No lanzamos excepción, la imagen es opcional
        }
    }

    /**
     * Crea el usuario asociado al cliente con Auth0
     */
    private Usuario crearUsuarioAuth0(String email, Cliente cliente, String externalId) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(email); // Usar email como nombre de usuario
        usuario.setEmail(email);
        usuario.setClave(null); // No hay contraseña local para usuarios Auth0
        usuario.setRol(Rol.CLIENTE);
        usuario.setPersona(cliente);
        usuario.setExternalId(externalId);
        usuario.setIsExternal(true);
        
        // Extraer provider del externalId
        usuario.setProvider(extractProvider(externalId));
        usuario.setEmailVerified(true); // Auth0 ya verificó el email
        usuario.setEliminado(false);
        
        return usuario;
    }
    
    /**
     * Extrae el proveedor del externalId de Auth0.
     * Ejemplos: "auth0|123" -> "auth0", "google-oauth2|123" -> "google"
     */
    private String extractProvider(String externalId) {
        if (externalId == null) return "unknown";
        
        if (externalId.startsWith("google-oauth2|")) return "GOOGLE";
        if (externalId.startsWith("facebook|")) return "FACEBOOK";
        if (externalId.startsWith("twitter|")) return "TWITTER";
        if (externalId.startsWith("github|")) return "GITHUB";
        if (externalId.startsWith("auth0|")) return "AUTH0";
        
        return externalId.split("\\|")[0].toUpperCase();
    }
}
