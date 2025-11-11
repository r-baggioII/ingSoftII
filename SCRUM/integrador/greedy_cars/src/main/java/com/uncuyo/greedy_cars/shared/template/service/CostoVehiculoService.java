package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.CostoVehiculoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.CostoVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.CostoVehiculoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.CostoVehiculoRepository;
import com.uncuyo.greedy_cars.shared.template.repository.CaracteristicaVehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CostoVehiculoService extends BaseService<CostoVehiculo, String> {

    private final CostoVehiculoMapper mapper;
    private final CaracteristicaVehiculoRepository caracteristicaRepo;
    private final CostoVehiculoRepository costoRepo;

    @Autowired
    public CostoVehiculoService(CostoVehiculoRepository repository,
                               CostoVehiculoMapper mapper,
                               CaracteristicaVehiculoRepository caracteristicaRepo) {
        super(repository);
        this.costoRepo = repository;
        this.mapper = mapper;
        this.caracteristicaRepo = caracteristicaRepo;
    }

    public List<CostoVehiculoDTO> listarActivosDTO() throws ErrorServiceException {
        try {
            List<CostoVehiculo> lista = listarActivos();
            return lista.stream().map(mapper::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar costos: " + e.getMessage());
        }
    }

    public Optional<CostoVehiculoDTO> obtenerDTO(String id) throws ErrorServiceException {
        try {
            Optional<CostoVehiculo> opt = obtener(id);
            return opt.map(mapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al obtener costo: " + e.getMessage());
        }
    }

    @Transactional
    public CostoVehiculoDTO altaDTO(CostoVehiculoDTO dto) throws ErrorServiceException {
        try {
            CaracteristicaVehiculo car = caracteristicaRepo.findByIdAndEliminadoIsFalse(dto.getIdCaracteristicaVehiculo())
                    .orElseThrow(() -> new ErrorServiceException("Característica no encontrada"));
            CostoVehiculo entidad = mapper.toEntity(dto, car);
            CostoVehiculo guardado = alta(entidad);
            return mapper.toDTO(guardado);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear costo: " + e.getMessage());
        }
    }

    @Transactional
    public Optional<CostoVehiculoDTO> modificarDTO(String id, CostoVehiculoDTO dto) throws ErrorServiceException {
        try {
            CaracteristicaVehiculo car = caracteristicaRepo.findByIdAndEliminadoIsFalse(dto.getIdCaracteristicaVehiculo())
                    .orElseThrow(() -> new ErrorServiceException("Característica no encontrada"));
            CostoVehiculo cambios = mapper.toEntity(dto, car);
            Optional<CostoVehiculo> mod = modificar(id, cambios);
            return mod.map(mapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar costo: " + e.getMessage());
        }
    }

    @Transactional
    public void crearCostoVehiculo(LocalDate fechaDesde, LocalDate fechaHasta, double costo, String idCaracteristicaVehiculo) throws ErrorServiceException {
        try {
            CaracteristicaVehiculo car = caracteristicaRepo.findByIdAndEliminadoIsFalse(idCaracteristicaVehiculo)
                    .orElseThrow(() -> new ErrorServiceException("Característica no encontrada"));
            CostoVehiculo entidad = new CostoVehiculo();
            entidad.crearCostoVehiculo(fechaDesde, fechaHasta, costo, car);
            alta(entidad);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear costo: " + e.getMessage());
        }
    }

    public void validar(LocalDate fechaDesde, LocalDate fechaHasta, double costo, String idCaracteristicaVehiculo) throws ErrorServiceException {
        try {
            if (idCaracteristicaVehiculo == null || idCaracteristicaVehiculo.isBlank()) throw new ErrorServiceException("Debe indicar la característica");
            if (caracteristicaRepo.findByIdAndEliminadoIsFalse(idCaracteristicaVehiculo).isEmpty()) throw new ErrorServiceException("Característica no encontrada");
            if (fechaDesde == null) throw new ErrorServiceException("Debe indicar la fecha desde");
            if (fechaHasta == null) throw new ErrorServiceException("Debe indicar la fecha hasta");
            if (fechaHasta.isBefore(fechaDesde)) throw new ErrorServiceException("La fecha hasta no puede ser anterior a la fecha desde");
            if (costo < 0) throw new ErrorServiceException("El costo no puede ser negativo");
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al validar costo: " + e.getMessage());
        }
    }

    @Transactional
    public void modificarCostoVehiculo(String id, LocalDate fechaDesde, LocalDate fechaHasta, double costo, String idCaracteristicaVehiculo) throws ErrorServiceException {
        try {
            CaracteristicaVehiculo car = caracteristicaRepo.findByIdAndEliminadoIsFalse(idCaracteristicaVehiculo)
                    .orElseThrow(() -> new ErrorServiceException("Característica no encontrada"));
            CostoVehiculo cambios = new CostoVehiculo();
            cambios.setId(id);
            cambios.modificarCostoVehiculo(fechaDesde, fechaHasta, costo, car);
            modificar(id, cambios).orElseThrow(() -> new ErrorServiceException("Costo no encontrado"));
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar costo: " + e.getMessage());
        }
    }

    public CostoVehiculo buscarCostoVehiculo(String id) throws ErrorServiceException {
        return obtenerEntidad(id);
    }

    public void eliminarCostoVehiculo(String id) throws ErrorServiceException {
        baja(id);
    }

    public Collection<CostoVehiculo> listarCostoVehiculo() throws ErrorServiceException {
        return listarActivos();
    }

    public Collection<CostoVehiculo> listarCostoVehiculoActivo() throws ErrorServiceException {
        return listarActivos();
    }

    public Optional<CostoVehiculo> buscarCostoVehiculoVigente(String idCaracteristicaVehiculo) throws ErrorServiceException {
        return buscarCostoVehiculoVigente(idCaracteristicaVehiculo, LocalDate.now());
    }

    public Optional<CostoVehiculo> buscarCostoVehiculoVigente(String idCaracteristicaVehiculo, LocalDate fechaReferencia) throws ErrorServiceException {
        try {
            LocalDate fecha = fechaReferencia != null ? fechaReferencia : LocalDate.now();
            return costoRepo.findVigenteByCaracteristicaAndDate(idCaracteristicaVehiculo, fecha);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al buscar costo vigente: " + e.getMessage());
        }
    }

    public Optional<CostoVehiculoDTO> buscarCostoVehiculoVigenteDTO(String idCaracteristicaVehiculo) throws ErrorServiceException {
        try {
            Optional<CostoVehiculo> opt = costoRepo.findVigenteByCaracteristicaAndDate(idCaracteristicaVehiculo, LocalDate.now());
            return opt.map(mapper::toDTO);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al buscar costo vigente: " + e.getMessage());
        }
    }

    @Override
    protected void actualizarEntidad(CostoVehiculo existente, CostoVehiculo nueva) {
        if (nueva.getFechaDesde() != null) existente.setFechaDesde(nueva.getFechaDesde());
        if (nueva.getFechaHasta() != null) existente.setFechaHasta(nueva.getFechaHasta());
        existente.setCosto(nueva.getCosto());
        if (nueva.getCaracteristicaVehiculo() != null) existente.setCaracteristicaVehiculo(nueva.getCaracteristicaVehiculo());
    }

    @Override
    protected void validar(BaseUseCaseService useCase, CostoVehiculo entidad) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {
                if (entidad == null) throw new ErrorServiceException("Debe indicar el costo");
                entidad.validar(entidad.getFechaDesde(), entidad.getFechaHasta(), entidad.getCosto(), entidad.getCaracteristicaVehiculo());
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de sistema al validar costo");
        }
    }
}
