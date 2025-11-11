package com.uncuyo.greedy_cars.shared.template.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uncuyo.greedy_cars.shared.template.dto.EmpleadoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Contacto;
import com.uncuyo.greedy_cars.shared.template.entity.Direccion;
import com.uncuyo.greedy_cars.shared.template.entity.Empleado;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.EmpleadoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ContactoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DireccionRepository;
import com.uncuyo.greedy_cars.shared.template.repository.EmpleadoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ImagenRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmpleadoService extends BaseService<Empleado, String> {

    @PersistenceContext
    private EntityManager entityManager;

    private final EmpleadoMapper empleadoMapper;
    private final ContactoRepository contactoRepository;
    private final ImagenRepository imagenRepository;
    private final DireccionRepository direccionRepository;

    @Autowired
    public EmpleadoService(EmpleadoRepository repository, 
                          EmpleadoMapper empleadoMapper,
                          ContactoRepository contactoRepository,
                          ImagenRepository imagenRepository,
                          DireccionRepository direccionRepository) {
        super(repository);
        this.empleadoMapper = empleadoMapper;
        this.contactoRepository = contactoRepository;
        this.imagenRepository = imagenRepository;
        this.direccionRepository = direccionRepository;
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
            
            // Flush to database and refresh to ensure all collections are loaded
            entityManager.flush();
            entityManager.refresh(guardado);
            
            // Force initialization of lazy collections
            guardado.getDirecciones().size();
            guardado.getContactos().size();
            guardado.getImagenes().size();
            
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

    @Override
    protected void preAlta(Empleado entidad) throws ErrorServiceException {
        super.preAlta(entidad);
        
        System.out.println("=== EMPLEADO preAlta - INICIO ===");
        System.out.println("Direcciones size: " + (entidad.getDirecciones() != null ? entidad.getDirecciones().size() : "null"));
        System.out.println("Contactos size: " + (entidad.getContactos() != null ? entidad.getContactos().size() : "null"));
        System.out.println("Imagenes size: " + (entidad.getImagenes() != null ? entidad.getImagenes().size() : "null"));
        
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
        if (entidad.getContactos() != null && !entidad.getContactos().isEmpty()) {
            List<Contacto> contactosToAssociate = new ArrayList<>();
            for (Contacto contacto : entidad.getContactos()) {
                if (contacto.getId() != null) {
                    Optional<Contacto> existingContacto = contactoRepository.findById(contacto.getId());
                    if (existingContacto.isPresent()) {
                        contactosToAssociate.add(existingContacto.get());
                    } else {
                        throw new ErrorServiceException("Contacto no encontrado con ID: " + contacto.getId());
                    }
                } else {
                    contactosToAssociate.add(contacto);
                }
            }
            entidad.getContactos().clear();
            entidad.getContactos().addAll(contactosToAssociate);
            System.out.println("Contactos after processing: " + entidad.getContactos().size());
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
            System.out.println("Imagenes after processing: " + entidad.getImagenes().size());
        }
        
        System.out.println("=== EMPLEADO preAlta - FIN ===");
    }

    @Override
    protected void actualizarEntidad(Empleado existente, Empleado nueva) {
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
        if (nueva.getTipoEmpleado() != null) {
            existente.setTipoEmpleado(nueva.getTipoEmpleado());
        }

        // Update collections
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
    }
}
