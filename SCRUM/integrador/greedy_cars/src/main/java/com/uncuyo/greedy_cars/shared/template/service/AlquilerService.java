package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.AlquilerDTO;
import com.uncuyo.greedy_cars.shared.template.entity.*;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.AlquilerMapper;
import com.uncuyo.greedy_cars.shared.template.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlquilerService extends BaseService<Alquiler, String> {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final DocumentacionRepository documentacionRepository;
    private final AlquilerMapper alquilerMapper;

    public AlquilerService(AlquilerRepository repository,
                           ClienteRepository clienteRepository,
                           VehiculoRepository vehiculoRepository,
                           DocumentacionRepository documentacionRepository,
                           AlquilerMapper alquilerMapper) {
        super(repository);
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.documentacionRepository = documentacionRepository;
        this.alquilerMapper = alquilerMapper;
    }

    // =============== Métodos con DTOs ===============
    public List<AlquilerDTO> listarActivosDTO() throws ErrorServiceException {
        List<Alquiler> lista = listarActivos();
        return lista.stream().map(alquilerMapper::toDTO).collect(Collectors.toList());
    }

    public Optional<AlquilerDTO> obtenerDTO(String id) throws ErrorServiceException {
        Optional<Alquiler> opt = obtener(id);
        return opt.map(alquilerMapper::toDTO);
    }

    public AlquilerDTO altaDTO(AlquilerDTO dto) throws ErrorServiceException {
        Cliente cli = findCliente(dto.getIdCliente());
        Vehiculo veh = findVehiculo(dto.getIdVehiculo());
        List<Documentacion> docs = (dto.getDocumentacionIds() == null || dto.getDocumentacionIds().isEmpty())
                ? List.of()
                : documentacionRepository.findAllById(dto.getDocumentacionIds());

        Alquiler entidad = alquilerMapper.toEntity(dto, cli, veh, docs);
        Alquiler guardado = alta(entidad);
        return alquilerMapper.toDTO(guardado);
    }

    public Optional<AlquilerDTO> modificarDTO(String id, AlquilerDTO dto) throws ErrorServiceException {
        Cliente cli = findCliente(dto.getIdCliente());
        Vehiculo veh = findVehiculo(dto.getIdVehiculo());
        List<Documentacion> docs = (dto.getDocumentacionIds() == null || dto.getDocumentacionIds().isEmpty())
                ? List.of()
                : documentacionRepository.findAllById(dto.getDocumentacionIds());

        Alquiler entidad = alquilerMapper.toEntity(dto, cli, veh, docs);
        Optional<Alquiler> mod = modificar(id, entidad);
        return mod.map(alquilerMapper::toDTO);
    }

    // =============== Métodos solicitados ===============
    public void crearAlquiler(LocalDate fechaDesde, LocalDate fechaHasta, String idCliente, String idVehiculo) throws ErrorServiceException {
        Cliente cliente = findCliente(idCliente);
        Vehiculo vehiculo = findVehiculo(idVehiculo);

        Alquiler nuevo = new Alquiler();
        nuevo.crearAlquiler(fechaDesde, fechaHasta, cliente, vehiculo);

        alta(nuevo);
    }

    public void validar(LocalDate fechaDesde, LocalDate fechaHasta, String idCliente, String idVehiculo) throws ErrorServiceException {
        // Usa la validación del servicio para no depender de la entidad
        validarFechasYRelaciones(fechaDesde, fechaHasta, idCliente, idVehiculo);
        // Validación de traslape
        boolean solapa = ((AlquilerRepository) repository).existeTraslapeParaVehiculo(idVehiculo, fechaDesde, fechaHasta);
        if (solapa) {
            throw new ErrorServiceException("El vehículo tiene un alquiler activo en el rango indicado");
        }
    }

    public void modificarAlquiler(String id, LocalDate fechaDesde, LocalDate fechaHasta, String idCliente, String idVehiculo) throws ErrorServiceException {
        Cliente cliente = findCliente(idCliente);
        Vehiculo vehiculo = findVehiculo(idVehiculo);

        Alquiler cambios = new Alquiler();
        cambios.setId(id);
        cambios.modificarAlquiler(fechaDesde, fechaHasta, cliente, vehiculo);

        modificar(id, cambios).orElseThrow(() -> new ErrorServiceException("Alquiler no encontrado"));
    }

    public Alquiler buscarAlquiler(String id) throws ErrorServiceException {
        return obtenerEntidad(id);
    }

    // Nota: mantengo el nombre con el typo pedido por el enunciado
    public void eliminarAlquier(String id) throws ErrorServiceException {
        baja(id);
    }

    public Collection<Alquiler> listarAlquiler() throws ErrorServiceException {
        return listarActivos();
    }

    public Collection<Alquiler> listarAlquierActivo() throws ErrorServiceException {
        return listarActivos();
    }

    // =============== Hooks de BaseService ===============
    @Override
    protected void actualizarEntidad(Alquiler existente, Alquiler nueva) {
        if (nueva.getFechaDesde() != null) existente.setFechaDesde(nueva.getFechaDesde());
        if (nueva.getFechaHasta() != null) existente.setFechaHasta(nueva.getFechaHasta());
        if (nueva.getCliente() != null) existente.setCliente(nueva.getCliente());
        if (nueva.getVehiculo() != null) existente.setVehiculo(nueva.getVehiculo());
        if (nueva.getDocumentaciones() != null && !nueva.getDocumentaciones().isEmpty()) {
            existente.setDocumentaciones(nueva.getDocumentaciones());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Alquiler alquiler) throws ErrorServiceException {
        try {
            if (useCase != BaseUseCaseService.BAJA) {
                if (alquiler == null) {
                    throw new ErrorServiceException("Debe indicar el alquiler");
                }
                validarFechasYRelaciones(alquiler.getFechaDesde(), alquiler.getFechaHasta(),
                        alquiler.getCliente() != null ? alquiler.getCliente().getId() : null,
                        alquiler.getVehiculo() != null ? alquiler.getVehiculo().getId() : null);

                // Verificar traslape de vehículo
                boolean solapa = ((AlquilerRepository) repository)
                        .existeTraslapeParaVehiculo(alquiler.getVehiculo().getId(), alquiler.getFechaDesde(), alquiler.getFechaHasta());
                if (solapa && useCase == BaseUseCaseService.ALTA) {
                    throw new ErrorServiceException("El vehículo tiene un alquiler activo en el rango indicado");
                }
                // En modificación, permitir si el registro que solapa es el mismo ID (simplificado)
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    // =============== Helpers ===============
    private void validarFechasYRelaciones(LocalDate fechaDesde, LocalDate fechaHasta, String idCliente, String idVehiculo) throws ErrorServiceException {
        if (fechaDesde == null) throw new ErrorServiceException("Debe indicar la fecha desde");
        if (fechaHasta == null) throw new ErrorServiceException("Debe indicar la fecha hasta");
        if (fechaHasta.isBefore(fechaDesde)) throw new ErrorServiceException("La fecha hasta no puede ser anterior a la fecha desde");
        if (idCliente == null || idCliente.isBlank()) throw new ErrorServiceException("Debe indicar el cliente");
        if (idVehiculo == null || idVehiculo.isBlank()) throw new ErrorServiceException("Debe indicar el vehículo");
        // Confirma existencia
        if (clienteRepository.findByIdAndEliminadoIsFalse(idCliente).isEmpty()) {
            throw new ErrorServiceException("Cliente no encontrado o eliminado");
        }
        if (vehiculoRepository.findByIdAndEliminadoIsFalse(idVehiculo).isEmpty()) {
            throw new ErrorServiceException("Vehículo no encontrado o eliminado");
        }
    }

    private Cliente findCliente(String id) throws ErrorServiceException {
        return clienteRepository.findByIdAndEliminadoIsFalse(id)
                .orElseThrow(() -> new ErrorServiceException("Cliente no encontrado"));
    }

    private Vehiculo findVehiculo(String id) throws ErrorServiceException {
        return vehiculoRepository.findByIdAndEliminadoIsFalse(id)
                .orElseThrow(() -> new ErrorServiceException("Vehículo no encontrado"));
    }
}
