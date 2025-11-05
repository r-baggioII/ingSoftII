package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.CaracteristicaVehiculoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.Imagen;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.CaracteristicaVehiculoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.CaracteristicaVehiculoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ImagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CaracteristicaVehiculoService extends BaseService<CaracteristicaVehiculo, String> {

    private final CaracteristicaVehiculoMapper mapper;
    private final ImagenRepository imagenRepository;

    @Autowired
    public CaracteristicaVehiculoService(CaracteristicaVehiculoRepository repository,
                                         CaracteristicaVehiculoMapper mapper,
                                         ImagenRepository imagenRepository) {
        super(repository);
        this.mapper = mapper;
        this.imagenRepository = imagenRepository;
    }

    // Métodos con DTOs
    public List<CaracteristicaVehiculoDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<CaracteristicaVehiculo> lista = listarActivos();
            return lista.stream().map(mapper::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar características: " + e.getMessage());
        }
    }

    public Optional<CaracteristicaVehiculoDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<CaracteristicaVehiculo> opt = obtener(id);
            return opt.map(mapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener característica: " + e.getMessage());
        }
    }

    @Transactional
    public CaracteristicaVehiculoDTO altaDTO(CaracteristicaVehiculoDTO dto) throws ErrorServiceException {
        try {
            List<Imagen> imagenes = resolveImagenesFromIds(dto.getImagenIds());
            CaracteristicaVehiculo entidad = mapper.toEntity(dto, imagenes);
            CaracteristicaVehiculo guardada = alta(entidad);
            return mapper.toDTO(guardada);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear característica: " + e.getMessage());
        }
    }

    @Transactional
    public Optional<CaracteristicaVehiculoDTO> modificarDTO(String id, CaracteristicaVehiculoDTO dto) throws ErrorServiceException {
        try {
            List<Imagen> imagenes = resolveImagenesFromIds(dto.getImagenIds());
            CaracteristicaVehiculo cambios = mapper.toEntity(dto, imagenes);
            Optional<CaracteristicaVehiculo> mod = modificar(id, cambios);
            return mod.map(mapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar característica: " + e.getMessage());
        }
    }

    // Métodos solicitados por el enunciado
    @Transactional
    public void crearCaracteristicaVehiculo(String marca, String modelo, int cantidadPuerta,
                                            int cantidadAsiento, long anio,
                                            int cantidadTotalVehiculo, int cantidadVehiculoAlquilado,
                                            String idImagen) throws ErrorServiceException {
        try {
            List<Imagen> imagenes = idImagen != null && !idImagen.isBlank()
                    ? List.of(imagenRepository.findByIdAndEliminadoIsFalse(idImagen)
                    .orElseThrow(() -> new ErrorServiceException("Imagen no encontrada")))
                    : List.of();

            CaracteristicaVehiculo nueva = new CaracteristicaVehiculo();
            nueva.crearCaracteristicaVehiculo(marca, modelo, cantidadPuerta, cantidadAsiento, anio,
                    cantidadTotalVehiculo, cantidadVehiculoAlquilado, imagenes);

            alta(nueva);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear característica: " + e.getMessage());
        }
    }

    public void validar(String marca, String modelo, int cantidadPuerta,
                        int cantidadAsiento, long anio,
                        int cantidadTotalVehiculo, int cantidadVehiculoAlquilado,
                        String idImagen) throws ErrorServiceException {
        try {
            // Validación básica en servicio y existencia de imagen si se proporcionó
            if (marca == null || marca.trim().isEmpty()) throw new ErrorServiceException("Debe indicar la marca");
            if (modelo == null || modelo.trim().isEmpty()) throw new ErrorServiceException("Debe indicar el modelo");
            if (cantidadPuerta <= 0) throw new ErrorServiceException("La cantidad de puertas debe ser mayor a 0");
            if (cantidadAsiento <= 0) throw new ErrorServiceException("La cantidad de asientos debe ser mayor a 0");
            if (anio <= 0) throw new ErrorServiceException("El año debe ser un número válido");
            if (cantidadTotalVehiculo < 0) throw new ErrorServiceException("La cantidad total de vehículos no puede ser negativa");
            if (cantidadVehiculoAlquilado < 0) throw new ErrorServiceException("La cantidad de vehículos alquilados no puede ser negativa");
            if (cantidadVehiculoAlquilado > cantidadTotalVehiculo) throw new ErrorServiceException("La cantidad alquilada no puede ser mayor que la cantidad total");

            if (idImagen != null && !idImagen.isBlank()) {
                if (imagenRepository.findByIdAndEliminadoIsFalse(idImagen).isEmpty()) {
                    throw new ErrorServiceException("Imagen no encontrada o eliminada");
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al validar característica: " + e.getMessage());
        }
    }

    @Transactional
    public void modificarCaracteristicaVehiculo(String id, String marca, String modelo, int cantidadPuerta,
                                                int cantidadAsiento, long anio,
                                                int cantidadTotalVehiculo, int cantidadVehiculoAlquilado,
                                                String idImagen) throws ErrorServiceException {
        try {
            CaracteristicaVehiculo existente = obtenerEntidad(id);
            List<Imagen> imagenes = idImagen != null && !idImagen.isBlank()
                    ? List.of(imagenRepository.findByIdAndEliminadoIsFalse(idImagen)
                    .orElseThrow(() -> new ErrorServiceException("Imagen no encontrada")))
                    : existente.getImagenes();

            CaracteristicaVehiculo cambios = new CaracteristicaVehiculo();
            cambios.setId(id);
            cambios.modificarCaracteristicaVehiculo(marca, modelo, cantidadPuerta, cantidadAsiento, anio,
                    cantidadTotalVehiculo, cantidadVehiculoAlquilado, imagenes);

            modificar(id, cambios).orElseThrow(() -> new ErrorServiceException("Característica no encontrada"));
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar característica: " + e.getMessage());
        }
    }

    public CaracteristicaVehiculo buscarCaracteristicaVehiculo(String id) throws ErrorServiceException {
        return obtenerEntidad(id);
    }

    public void eliminarCaracteristicaVehiculo(String id) throws ErrorServiceException {
        baja(id);
    }

    public Collection<CaracteristicaVehiculo> listarCaracteristicaVehiculo() throws ErrorServiceException {
        return listarActivos();
    }

    public Collection<CaracteristicaVehiculo> listarCaracteristicaVehiculoActivo() throws ErrorServiceException {
        return listarActivos();
    }

    // Hooks
    @Override
    protected void actualizarEntidad(CaracteristicaVehiculo existente, CaracteristicaVehiculo nueva) {
        if (nueva.getMarca() != null) existente.setMarca(nueva.getMarca());
        if (nueva.getModelo() != null) existente.setModelo(nueva.getModelo());
        existente.setCantidadPuerta(nueva.getCantidadPuerta());
        existente.setCantidadAsiento(nueva.getCantidadAsiento());
        existente.setAnio(nueva.getAnio());
        existente.setCantidadTotalVehiculo(nueva.getCantidadTotalVehiculo());
        existente.setCantidadVehiculoAlquilado(nueva.getCantidadVehiculoAlquilado());
        if (nueva.getImagenes() != null) {
            existente.getImagenes().clear();
            existente.getImagenes().addAll(nueva.getImagenes());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, CaracteristicaVehiculo entidad) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {
                if (entidad == null) throw new ErrorServiceException("Debe indicar la característica");
                // Delega a la entidad
                entidad.validar(entidad.getMarca(), entidad.getModelo(), entidad.getCantidadPuerta(), entidad.getCantidadAsiento(),
                        entidad.getAnio(), entidad.getCantidadTotalVehiculo(), entidad.getCantidadVehiculoAlquilado(), entidad.getImagenes());

                // Verificar que las imágenes (si existen) no estén eliminadas
                if (entidad.getImagenes() != null && !entidad.getImagenes().isEmpty()) {
                    for (Imagen img : entidad.getImagenes()) {
                        if (img == null || img.getId() == null) throw new ErrorServiceException("Imagen inválida");
                        if (imagenRepository.findByIdAndEliminadoIsFalse(img.getId()).isEmpty()) {
                            throw new ErrorServiceException("Imagen no encontrada o eliminada: " + img.getId());
                        }
                    }
                }
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de sistema al validar característica");
        }
    }

    // Helper
    private List<Imagen> resolveImagenesFromIds(List<String> imagenIds) {
        if (imagenIds == null || imagenIds.isEmpty()) return List.of();
        return imagenRepository.findAllById(imagenIds);
    }
}
