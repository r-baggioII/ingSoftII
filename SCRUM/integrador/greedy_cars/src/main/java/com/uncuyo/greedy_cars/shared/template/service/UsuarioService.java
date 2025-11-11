package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.UsuarioDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Persona;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import com.uncuyo.greedy_cars.shared.template.mapper.UsuarioMapper;
import com.uncuyo.greedy_cars.shared.template.repository.PersonaRepository;
import com.uncuyo.greedy_cars.shared.template.repository.UsuarioRepository;

import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UsuarioService extends BaseService<Usuario, String> {

    private final UsuarioRepository usuarioRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioMapper usuarioMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository repository, PersonaRepository personaRepository, UsuarioMapper usuarioMapper) {
        super(repository);
        this.usuarioRepository = repository;
        this.personaRepository = personaRepository;
        this.usuarioMapper = usuarioMapper;
    }

    /**
     * Registra un nuevo usuario encriptando su contraseña
     */
    public Usuario registrarUsuario(Usuario usuario) throws ErrorServiceException {
        // Validar que no exista el username
        if (usuarioRepository.existsByNombreUsuarioAndEliminadoIsFalse(usuario.getNombreUsuario())) {
            throw new ErrorServiceException("El nombre de usuario ya existe");
        }

        // Encriptar la contraseña
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        // Guardar el usuario
        return alta(usuario);
    }

    /**
     * Busca un usuario por nombre de usuario
     */
    public Optional<Usuario> buscarPorNombreUsuario(String nombreUsuario) {
        return usuarioRepository.findByNombreUsuarioAndEliminadoIsFalse(nombreUsuario);
    }

    // Métodos con DTOs
    public List<UsuarioDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Usuario> usuarios = listarActivos();
            return usuarioMapper.toDTOList(usuarios);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar usuarios: " + e.getMessage());
        }
    }
    
    public Optional<UsuarioDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<Usuario> usuario = obtener(id);
            return usuario.map(usuarioMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener usuario: " + e.getMessage());
        }
    }
    
    public UsuarioDTO altaDTO(UsuarioDTO usuarioDTO) throws ErrorServiceException {
        try {
            // Limpiar el ID si está vacío (viene "" del cliente en lugar de null)
            if (usuarioDTO.getId() != null && usuarioDTO.getId().trim().isEmpty()) {
                usuarioDTO.setId(null);
            }
            
            Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
            
            // Asegurar que el ID de la entidad también sea null
            usuario.setId(null);
            
            Usuario usuarioGuardado = registrarUsuario(usuario);
            return usuarioMapper.toDTO(usuarioGuardado);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear usuario: " + e.getMessage());
        }
    }
    
    public Optional<UsuarioDTO> modificarDTO(String id, UsuarioDTO usuarioDTO) throws ErrorServiceException {
        try {
            Usuario usuario = usuarioMapper.toEntity(usuarioDTO);
            Optional<Usuario> usuarioModificado = modificar(id, usuario);
            return usuarioModificado.map(usuarioMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar usuario: " + e.getMessage());
        }
    }

    /**
     * Actualiza los campos de un usuario existente con los datos de un usuario nuevo.
     */
    @Override
    protected void actualizarEntidad(Usuario entidadExistente, Usuario entidadNueva) {
        if (entidadNueva.getNombreUsuario() != null) {
            entidadExistente.setNombreUsuario(entidadNueva.getNombreUsuario());
        }

        // Solo actualizar la clave si se proporciona una nueva Y encriptarla
        if (entidadNueva.getClave() != null && !entidadNueva.getClave().isEmpty()) {
            entidadExistente.setClave(passwordEncoder.encode(entidadNueva.getClave()));
        }

        // Actualizar rol si se proporciona
        if (entidadNueva.getRol() != null) {
            entidadExistente.setRol(entidadNueva.getRol());
        }
        
        // Actualizar persona si se proporciona
        if (entidadNueva.getPersona() != null) {
            entidadExistente.setPersona(entidadNueva.getPersona());
        }
    }
    
    @Override
    protected void preAlta(Usuario usuario) throws ErrorServiceException {
        super.preAlta(usuario);
        
        // Validar que la persona asociada existe y está activa
        if (usuario.getPersona() == null || usuario.getPersona().getId() == null) {
            throw new ErrorServiceException("El usuario debe estar asociado a una persona (Cliente o Empleado)");
        }
        
        Optional<Persona> personaOpt = personaRepository.findByIdAndEliminadoIsFalse(usuario.getPersona().getId());
        if (personaOpt.isEmpty()) {
            throw new ErrorServiceException("La persona con ID " + usuario.getPersona().getId() + " no existe o está eliminada");
        }
        
        // Asignar la persona gestionada
        usuario.setPersona(personaOpt.get());
    }
}