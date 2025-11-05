package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.ImagenDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.enums.TipoImagen;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.ImagenMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ImagenService extends BaseService<Imagen, String> {

    private final ImagenMapper imagenMapper;
    private final ImagenRepository imagenRepository;

    @Autowired
    public ImagenService(ImagenRepository repository, ImagenMapper imagenMapper) {
        super(repository);
        this.imagenRepository = repository;
        this.imagenMapper = imagenMapper;
    }

    // ==================== Métodos de dominio ====================
    
    /**
     * Crea una nueva imagen con validación.
     *
     * @param nombre el nombre de la imagen (con extensión)
     * @param contenido el contenido binario de la imagen
     * @param tipoImagen el tipo de imagen
     * @return la imagen creada y guardada
     * @throws ErrorServiceException si hay un error en la creación
     */
    @Transactional
    public Imagen crearImagen(String nombre, byte[] contenido, TipoImagen tipoImagen) 
            throws ErrorServiceException {
        try {
            Imagen imagen = new Imagen();
            imagen.crearImagen(nombre, contenido, tipoImagen);
            return alta(imagen);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear imagen: " + e.getMessage());
        }
    }
    
    /**
     * Modifica una imagen existente.
     *
     * @param id el ID de la imagen a modificar
     * @param nombre el nuevo nombre (opcional, con extensión)
     * @param contenido el nuevo contenido (opcional)
     * @param tipoImagen el nuevo tipo de imagen (opcional)
     * @return Optional con la imagen modificada
     * @throws ErrorServiceException si hay un error en la modificación
     */
    @Transactional
    public Optional<Imagen> modificarImagen(String id, String nombre, 
                                            byte[] contenido, TipoImagen tipoImagen) 
            throws ErrorServiceException {
        try {
            Optional<Imagen> imagenOpt = obtener(id);
            if (imagenOpt.isEmpty()) {
                throw new ErrorServiceException("Imagen no encontrada con ID: " + id);
            }
            
            Imagen imagen = imagenOpt.get();
            imagen.modificarImagen(nombre, contenido, tipoImagen);
            return Optional.of(repository.save(imagen));
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar imagen: " + e.getMessage());
        }
    }
    
    /**
     * Valida los datos de una imagen.
     *
     * @param nombre el nombre de la imagen
     * @param contenido el contenido binario
     * @param tipoImagen el tipo de imagen
     * @throws ErrorServiceException si la validación falla
     */
    public void validarImagen(String nombre, byte[] contenido, TipoImagen tipoImagen) 
            throws ErrorServiceException {
        try {
            Imagen imagen = new Imagen();
            imagen.validar(nombre, contenido, tipoImagen);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al validar imagen: " + e.getMessage());
        }
    }

    // ==================== Métodos con DTOs ====================
    
    public List<ImagenDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<Imagen> imagenes = listarActivos();
            return imagenMapper.toDTOList(imagenes);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar imágenes: " + e.getMessage());
        }
    }
    
    public Optional<ImagenDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<Imagen> imagen = obtener(id);
            return imagen.map(imagenMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener imagen: " + e.getMessage());
        }
    }
    
    @Transactional
    public ImagenDTO altaDTO(ImagenDTO imagenDTO) throws ErrorServiceException {
        try {
            // Usar el método de dominio para crear
            Imagen imagen = crearImagen(
                imagenDTO.getNombre(),
                imagenDTO.getContenido(),
                imagenDTO.getTipoImagen()
            );
            return imagenMapper.toDTO(imagen);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear imagen: " + e.getMessage());
        }
    }
    
    @Transactional
    public Optional<ImagenDTO> modificarDTO(String id, ImagenDTO imagenDTO) throws ErrorServiceException {
        try {
            // Usar el método de dominio para modificar
            Optional<Imagen> imagenModificada = modificarImagen(
                id,
                imagenDTO.getNombre(),
                imagenDTO.getContenido(),
                imagenDTO.getTipoImagen()
            );
            return imagenModificada.map(imagenMapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar imagen: " + e.getMessage());
        }
    }

    // ==================== Métodos de consulta ====================
    
    /**
     * Busca imágenes por tipo.
     * 
     * @param tipoImagen el tipo de imagen a buscar
     * @return lista de ImagenDTO del tipo especificado
     */
    public List<ImagenDTO> listarPorTipo(TipoImagen tipoImagen) throws ErrorServiceException {
        try {
            List<Imagen> imagenes = imagenRepository.findByTipoImagenAndEliminadoIsFalse(tipoImagen);
            return imagenMapper.toDTOList(imagenes);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar imágenes por tipo: " + e.getMessage());
        }
    }

    /**
     * Busca imágenes por nombre.
     * 
     * @param nombre el texto a buscar en el nombre
     * @return lista de ImagenDTO que coincidan con la búsqueda
     */
    public List<ImagenDTO> buscarPorNombre(String nombre) throws ErrorServiceException {
        try {
            List<Imagen> imagenes = imagenRepository.findByNombreContainingAndEliminadoIsFalse(nombre);
            return imagenMapper.toDTOList(imagenes);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al buscar imágenes por nombre: " + e.getMessage());
        }
    }

    @Override
    protected void actualizarEntidad(Imagen entidadExistente, Imagen entidadNueva) {
        if (entidadNueva.getNombre() != null) {
            entidadExistente.setNombre(entidadNueva.getNombre());
        }
        
        if (entidadNueva.getMime() != null) {
            entidadExistente.setMime(entidadNueva.getMime());
        }
        
        if (entidadNueva.getContenido() != null) {
            entidadExistente.setContenido(entidadNueva.getContenido());
        }
        
        if (entidadNueva.getTipoImagen() != null) {
            entidadExistente.setTipoImagen(entidadNueva.getTipoImagen());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Imagen imagen) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {
                if (imagen == null) {
                    throw new ErrorServiceException("Debe indicar la imagen");
                }
                
                // Usar el método de validación de dominio
                imagen.validar(imagen.getNombre(), imagen.getContenido(), imagen.getTipoImagen());
                
                if (imagen.getEliminado()) {
                    throw new ErrorServiceException("La imagen indicada se encuentra eliminada");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistema al validar imagen");
        }
    }
}