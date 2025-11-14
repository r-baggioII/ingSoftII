package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.RegistroClienteDTO;
import com.uncuyo.greedy_cars.shared.template.entity.*;
import com.uncuyo.greedy_cars.shared.template.enums.Rol;
import com.uncuyo.greedy_cars.shared.template.enums.TipoContacto;
import com.uncuyo.greedy_cars.shared.template.enums.TipoImagen;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

/**
 * Servicio para el registro público de nuevos clientes.
 * Gestiona la creación transaccional de todas las entidades relacionadas.
 */
@Slf4j
@Service
public class RegistroService {

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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistroService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            NacionalidadRepository nacionalidadRepository,
            PaisRepository paisRepository,
            ProvinciaRepository provinciaRepository,
            DepartamentoRepository departamentoRepository,
            LocalidadRepository localidadRepository,
            DireccionRepository direccionRepository,
            ContactoRepository contactoRepository,
            ImagenRepository imagenRepository,
            PasswordEncoder passwordEncoder) {
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
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registra un nuevo cliente con todas sus entidades relacionadas de forma transaccional.
     * 
     * @param dto DTO con todos los datos del registro
     * @return El cliente creado
     * @throws ErrorServiceException si ocurre algún error durante el registro
     */
    @Transactional
    public Cliente registrarCliente(RegistroClienteDTO dto) throws ErrorServiceException {
        log.info("Iniciando registro de nuevo cliente: {}", dto.getNombreUsuario());

        try {
            // 1. Validar que el nombre de usuario no exista
            validarUsuarioUnico(dto.getNombreUsuario());

            // 2. Validar que el número de documento no exista
            validarDocumentoUnico(dto.getNumeroDocumento());

            // 3. Crear o buscar Nacionalidad
            Nacionalidad nacionalidad = obtenerOCrearNacionalidad(dto.getNacionalidad());

            // 4. Crear la estructura geográfica y dirección
            Direccion direccion = crearDireccion(dto.getDireccion());

            // 5. Crear contactos
            // (Los contactos se asociarán al Cliente después de crearlo)

            // 6. Crear Cliente
            Cliente cliente = crearCliente(dto, nacionalidad, direccion);

            // 7. Crear y asociar contactos al cliente
            crearYAsociarContactos(dto, cliente);

            // 8. Crear y asociar imagen si existe
            if (dto.getImagen() != null) {
                crearYAsociarImagen(dto.getImagen(), cliente);
            }

            // 9. Guardar el cliente
            cliente = clienteRepository.save(cliente);
            log.info("Cliente guardado con ID: {}", cliente.getId());

            // 10. Crear Usuario y asociarlo al Cliente
            Usuario usuario = crearUsuario(dto, cliente);
            usuario = usuarioRepository.save(usuario);
            log.info("Usuario creado con ID: {}", usuario.getId());

            // 11. Asociar el usuario al cliente
            cliente.setUsuario(usuario);
            cliente = clienteRepository.save(cliente);

            log.info("Registro completado exitosamente para el cliente: {} {}", cliente.getNombre(), cliente.getApellido());
            return cliente;

        } catch (ErrorServiceException e) {
            log.error("Error en el registro: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante el registro", e);
            throw new ErrorServiceException("Error al registrar el cliente: " + e.getMessage());
        }
    }

    /**
     * Valida que el nombre de usuario no esté en uso
     */
    private void validarUsuarioUnico(String nombreUsuario) throws ErrorServiceException {
        if (usuarioRepository.existsByNombreUsuarioAndEliminadoIsFalse(nombreUsuario)) {
            throw new ErrorServiceException("El nombre de usuario '" + nombreUsuario + "' ya está en uso");
        }
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
            nacionalidad = new Nacionalidad();
            nacionalidad.setNombre(nombreNacionalidad);
            nacionalidad.setEliminado(false);
            nacionalidad = nacionalidadRepository.save(nacionalidad);
            log.info("Nueva nacionalidad creada: {}", nombreNacionalidad);
        }
        
        return nacionalidad;
    }

    /**
     * Crea la estructura geográfica completa (País, Provincia, Departamento, Localidad) y la Dirección
     */
    private Direccion crearDireccion(RegistroClienteDTO.DireccionRegistroDTO dtoDir) {
        // 1. Obtener o crear País
        Pais pais = paisRepository.buscarPaisPorNombre(dtoDir.getPais());
        if (pais == null) {
            pais = new Pais();
            pais.setNombre(dtoDir.getPais());
            pais.setEliminado(false);
            pais = paisRepository.save(pais);
            log.info("Nuevo país creado: {}", dtoDir.getPais());
        }

        // 2. Obtener o crear Provincia
        Provincia provincia = provinciaRepository.buscarProvinciaPorPaisYNombre(pais.getId(), dtoDir.getProvincia());
        if (provincia == null) {
            provincia = new Provincia();
            provincia.setNombre(dtoDir.getProvincia());
            provincia.setPais(pais);
            provincia.setEliminado(false);
            provincia = provinciaRepository.save(provincia);
            log.info("Nueva provincia creada: {}", dtoDir.getProvincia());
        }

        // 3. Obtener o crear Departamento
        Departamento departamento = departamentoRepository.buscarDepartamentoPorProvinciaYNombre(
                provincia.getId(), dtoDir.getDepartamento());
        if (departamento == null) {
            departamento = new Departamento();
            departamento.setNombre(dtoDir.getDepartamento());
            departamento.setProvincia(provincia);
            departamento.setEliminado(false);
            departamento = departamentoRepository.save(departamento);
            log.info("Nuevo departamento creado: {}", dtoDir.getDepartamento());
        }

        // 4. Obtener o crear Localidad
        Localidad localidad = localidadRepository.buscarLocalidadPorDepartamentoYNombre(
                departamento.getId(), dtoDir.getLocalidad());
        if (localidad == null) {
            localidad = new Localidad();
            localidad.setNombre(dtoDir.getLocalidad());
            localidad.setCodigoPostal(dtoDir.getCodigoPostal());
            localidad.setDepartamento(departamento);
            localidad.setEliminado(false);
            localidad = localidadRepository.save(localidad);
            log.info("Nueva localidad creada: {}", dtoDir.getLocalidad());
        }

        // 5. Crear Dirección
        Direccion direccion = new Direccion();
        direccion.setCalle(dtoDir.getCalle());
        direccion.setNumeracion(dtoDir.getNumeracion());
        direccion.setBarrio(dtoDir.getBarrio());
        direccion.setPisoCasa(dtoDir.getPisoCasa());
        direccion.setPuertaManzana(dtoDir.getPuertaManzana());
        direccion.setObservacion(dtoDir.getObservacion());
        direccion.setLocalidad(localidad);
        direccion.setEliminado(false);
        direccion = direccionRepository.save(direccion);
        log.info("Nueva dirección creada: {} {}", direccion.getCalle(), direccion.getNumeracion());

        return direccion;
    }

    /**
     * Crea la entidad Cliente con los datos proporcionados
     */
    private Cliente crearCliente(RegistroClienteDTO dto, Nacionalidad nacionalidad, Direccion direccion) {
        Cliente cliente = new Cliente();
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setFechaNacimiento(dto.getFechaNacimiento());
        cliente.setTipoDocumento(dto.getTipoDocumento());
        cliente.setNumeroDocumento(dto.getNumeroDocumento());
        cliente.setDireccionEstadia(dto.getDireccionEstadia());
        cliente.setEliminado(false);

        // Asociar nacionalidad
        cliente.addNacionalidad(nacionalidad);

        // Asociar dirección
        cliente.addDireccion(direccion);

        return cliente;
    }

    /**
     * Crea y asocia los contactos al cliente
     */
    private void crearYAsociarContactos(RegistroClienteDTO dto, Cliente cliente) {
        for (RegistroClienteDTO.ContactoRegistroDTO contactoDto : dto.getContactos()) {
            Contacto contacto;

            if (contactoDto.getTipoContacto() == TipoContacto.PERSONAL || 
                contactoDto.getTipoContacto() == TipoContacto.LABORAL) {
                
                // Determinar si es correo o teléfono según los campos proporcionados
                if (contactoDto.getMail() != null && !contactoDto.getMail().isBlank()) {
                    // Crear ContactoCorreoElectronico
                    ContactoCorreoElectronico correo = new ContactoCorreoElectronico();
                    correo.setMail(contactoDto.getMail());
                    correo.setTipoContacto(contactoDto.getTipoContacto());
                    correo.setObservacion(contactoDto.getObservacion());
                    correo.setEliminado(false);
                    contacto = contactoRepository.save(correo);
                    log.info("Contacto correo creado: {}", contactoDto.getMail());
                } else if (contactoDto.getTelefono() != null && !contactoDto.getTelefono().isBlank()) {
                    // Crear ContactoTelefonico
                    ContactoTelefonico telefono = new ContactoTelefonico();
                    telefono.setTelefono(contactoDto.getTelefono());
                    telefono.setTipoTelefono(contactoDto.getTipoTelefono());
                    telefono.setTipoContacto(contactoDto.getTipoContacto());
                    telefono.setObservacion(contactoDto.getObservacion());
                    telefono.setEliminado(false);
                    contacto = contactoRepository.save(telefono);
                    log.info("Contacto teléfono creado: {}", contactoDto.getTelefono());
                } else {
                    continue; // Saltar si no hay ni correo ni teléfono
                }

                cliente.addContacto(contacto);
            }
        }
    }

    /**
     * Crea y asocia una imagen al cliente
     */
    private void crearYAsociarImagen(RegistroClienteDTO.ImagenRegistroDTO imagenDto, Cliente cliente) {
        try {
            byte[] contenidoBytes = Base64.getDecoder().decode(imagenDto.getContenidoBase64());
            
            Imagen imagen = new Imagen();
            imagen.setNombre(imagenDto.getNombre());
            imagen.setMime(imagenDto.getMime());
            imagen.setContenido(contenidoBytes);
            imagen.setTipoImagen(TipoImagen.PERSONA);
            imagen.setEliminado(false);
            
            imagen = imagenRepository.save(imagen);
            cliente.addImagen(imagen);
            log.info("Imagen creada y asociada: {}", imagenDto.getNombre());
        } catch (IllegalArgumentException e) {
            log.warn("Error al decodificar imagen en base64: {}", e.getMessage());
            // No lanzar excepción, la imagen es opcional
        }
    }

    /**
     * Crea el usuario asociado al cliente
     */
    private Usuario crearUsuario(RegistroClienteDTO dto, Cliente cliente) {
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.getNombreUsuario());
        usuario.setClave(passwordEncoder.encode(dto.getClave())); // Encriptar contraseña
        usuario.setRol(Rol.CLIENTE);
        usuario.setPersona(cliente);
        usuario.setEliminado(false);
        
        return usuario;
    }
}
