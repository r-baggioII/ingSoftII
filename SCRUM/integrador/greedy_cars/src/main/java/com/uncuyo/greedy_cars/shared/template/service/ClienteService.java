package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uncuyo.greedy_cars.shared.template.dto.ClienteDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.entity.Nacionalidad;
import com.uncuyo.greedy_cars.shared.template.entity.Usuario;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.ClienteMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ClienteRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ImagenRepository;
import com.uncuyo.greedy_cars.shared.template.repository.NacionalidadRepository;
import com.uncuyo.greedy_cars.shared.template.repository.UsuarioRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService extends BaseService<Cliente, String> {

    @PersistenceContext
    private EntityManager entityManager;

    private final ClienteMapper clienteMapper;
    private final NacionalidadRepository nacionalidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final ContactoRepository contactoRepository;
    private final ImagenRepository imagenRepository;
    private final DireccionRepository direccionRepository;

    @Autowired
    public ClienteService(ClienteRepository repository,
                          ClienteMapper clienteMapper,
                          NacionalidadRepository nacionalidadRepository,
                          UsuarioRepository usuarioRepository,
                          ContactoRepository contactoRepository,
                          ImagenRepository imagenRepository,
                          DireccionRepository direccionRepository) {
        super(repository);
        this.clienteMapper = clienteMapper;
        this.nacionalidadRepository = nacionalidadRepository;
        this.usuarioRepository = usuarioRepository;
        this.contactoRepository = contactoRepository;
        this.imagenRepository = imagenRepository;
        this.direccionRepository = direccionRepository;
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
            
            // Flush to database and refresh to ensure all collections are loaded
            entityManager.flush();
            entityManager.refresh(guardado);
            
            // Force initialization of lazy collections
            guardado.getDirecciones().size();
            guardado.getContactos().size();
            guardado.getImagenes().size();
            guardado.getNacionalidades().size();
            
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

    @Override
    protected void preAlta(Cliente entidad) throws ErrorServiceException {
        super.preAlta(entidad);
        
        System.out.println("=== DEBUG preAlta ===");
        System.out.println("Direcciones size: " + (entidad.getDirecciones() != null ? entidad.getDirecciones().size() : "null"));
        System.out.println("Contactos size: " + (entidad.getContactos() != null ? entidad.getContactos().size() : "null"));
        System.out.println("Imagenes size: " + (entidad.getImagenes() != null ? entidad.getImagenes().size() : "null"));
        System.out.println("Nacionalidades size: " + (entidad.getNacionalidades() != null ? entidad.getNacionalidades().size() : "null"));
        
        // Handle Direcciones - ensure they're managed entities
        if (entidad.getDirecciones() != null && !entidad.getDirecciones().isEmpty()) {
            List<Direccion> managedDirecciones = new ArrayList<>();
            for (Direccion direccion : entidad.getDirecciones()) {
                if (direccion.getId() != null) {
                    Optional<Direccion> existingDireccion = direccionRepository.findById(direccion.getId());
                    if (existingDireccion.isPresent()) {
                        managedDirecciones.add(existingDireccion.get());
                    } else {
                        throw new ErrorServiceException("Direccion no encontrada con ID: " + direccion.getId());
                    }
                } else {
                    managedDirecciones.add(direccion);
                }
            }
            entidad.getDirecciones().clear();
            entidad.getDirecciones().addAll(managedDirecciones);
            System.out.println("Direcciones after processing: " + entidad.getDirecciones().size());
        }
        
        // Handle Contactos - ManyToMany relationship
        // Los contactos pueden ser compartidos entre múltiples personas
        if (entidad.getContactos() != null && !entidad.getContactos().isEmpty()) {
            List<Contacto> contactosToAssociate = new ArrayList<>();
            for (Contacto contacto : entidad.getContactos()) {
                if (contacto.getId() != null) {
                    // Es un contacto existente - lo obtenemos de la BD
                    Optional<Contacto> existingContacto = contactoRepository.findById(contacto.getId());
                    if (existingContacto.isPresent()) {
                        contactosToAssociate.add(existingContacto.get());
                    } else {
                        throw new ErrorServiceException("Contacto no encontrado con ID: " + contacto.getId());
                    }
                } else {
                    // Es un contacto nuevo - simplemente lo agregamos
                    contactosToAssociate.add(contacto);
                }
            }
            entidad.getContactos().clear();
            entidad.getContactos().addAll(contactosToAssociate);
        }
        
        // Handle Imagenes - ensure they're managed entities
        if (entidad.getImagenes() != null && !entidad.getImagenes().isEmpty()) {
            List<Imagen> managedImagenes = new ArrayList<>();
            for (Imagen imagen : entidad.getImagenes()) {
                if (imagen.getId() != null) {
                    Optional<Imagen> existingImagen = imagenRepository.findById(imagen.getId());
                    if (existingImagen.isPresent()) {
                        managedImagenes.add(existingImagen.get());
                    } else {
                        throw new ErrorServiceException("Imagen no encontrada con ID: " + imagen.getId());
                    }
                } else {
                    managedImagenes.add(imagen);
                }
            }
            entidad.getImagenes().clear();
            entidad.getImagenes().addAll(managedImagenes);
        }
        
        // Handle Nacionalidades - ensure they're managed entities
        if (entidad.getNacionalidades() != null && !entidad.getNacionalidades().isEmpty()) {
            List<Nacionalidad> managedNacionalidades = new ArrayList<>();
            for (Nacionalidad nac : entidad.getNacionalidades()) {
                if (nac.getId() != null) {
                    Optional<Nacionalidad> existingNacionalidad = nacionalidadRepository.findByIdAndEliminadoIsFalse(nac.getId());
                    if (existingNacionalidad.isPresent()) {
                        managedNacionalidades.add(existingNacionalidad.get());
                    } else {
                        throw new ErrorServiceException("Nacionalidad no encontrada con ID: " + nac.getId());
                    }
                } else {
                    managedNacionalidades.add(nac);
                }
            }
            entidad.getNacionalidades().clear();
            entidad.getNacionalidades().addAll(managedNacionalidades);
        }
    }

    @Override
    protected void actualizarEntidad(Cliente existente, Cliente nueva) {
        if (nueva.getNombre() != null) {
            existente.setNombre(nueva.getNombre());
        }
        if (nueva.getApellido() != null) {
            existente.setApellido(nueva.getApellido());
        }
        if (nueva.getFechaNacimiento() != null) {
            existente.setFechaNacimiento(nueva.getFechaNacimiento());
        }
        if (nueva.getTipoDocumento() != null) {
            existente.setTipoDocumento(nueva.getTipoDocumento());
        }
        if (nueva.getNumeroDocumento() != null) {
            existente.setNumeroDocumento(nueva.getNumeroDocumento());
        }
        if (nueva.getDireccionEstadia() != null) {
            existente.setDireccionEstadia(nueva.getDireccionEstadia());
        }
        if (nueva.getRecibirPromociones() != null) {
            existente.setRecibirPromociones(nueva.getRecibirPromociones());
        }
        
        // Update collections
        if (nueva.getNacionalidades() != null) {
            existente.getNacionalidades().clear();
            existente.getNacionalidades().addAll(nueva.getNacionalidades());
        }
        
        if (nueva.getContactos() != null) {
            existente.getContactos().clear();
            existente.getContactos().addAll(nueva.getContactos());
        }

        if (nueva.getDirecciones() != null) {
            existente.getDirecciones().clear();
            existente.getDirecciones().addAll(nueva.getDirecciones());
        }

        if (nueva.getImagenes() != null) {
            existente.getImagenes().clear();
            existente.getImagenes().addAll(nueva.getImagenes());
        }
        
        if (nueva.getUsuario() != null) {
            existente.setUsuario(nueva.getUsuario());
        }
    }
    
    public ClienteDTO asociarClienteUsuario(String clienteId, String usuarioId) throws ErrorServiceException {
        Cliente cliente = obtenerEntidad(clienteId);
        Usuario usuario = usuarioRepository.findByIdAndEliminadoIsFalse(usuarioId)
                .orElseThrow(() -> new ErrorServiceException("Usuario no encontrado o eliminado"));

        cliente.setUsuario(usuario);
        Cliente guardado = repository.save(cliente);
        return clienteMapper.toDTO(guardado);
    }

    public List<ClienteDTO> buscarPorQuery(String query) throws ErrorServiceException {
        try {
            ClienteRepository clienteRepo = (ClienteRepository) repository;
            List<Cliente> clientes = clienteRepo.searchByQuery(query);
            return clienteMapper.toDTOList(clientes);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al buscar clientes: " + e.getMessage(), e);
        }
    }
}
