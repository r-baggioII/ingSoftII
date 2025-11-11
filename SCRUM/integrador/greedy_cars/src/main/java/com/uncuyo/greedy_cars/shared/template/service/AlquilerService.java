package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.AlquilerDTO;
import com.uncuyo.greedy_cars.shared.template.entity.*;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoVehiculo;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.AlquilerMapper;
import com.uncuyo.greedy_cars.shared.template.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlquilerService extends BaseService<Alquiler, String> {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final DocumentacionRepository documentacionRepository;
    private final FacturaService facturaService;
    private final CostoVehiculoService costoVehiculoService;
    private final AlquilerMapper alquilerMapper;

    public AlquilerService(AlquilerRepository repository,
                           ClienteRepository clienteRepository,
                           VehiculoRepository vehiculoRepository,
                           DocumentacionRepository documentacionRepository,
                           FacturaService facturaService,
                           CostoVehiculoService costoVehiculoService,
                           AlquilerMapper alquilerMapper) {
        super(repository);
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.documentacionRepository = documentacionRepository;
        this.facturaService = facturaService;
        this.costoVehiculoService = costoVehiculoService;
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

    public List<AlquilerDTO> listarPorCliente(String clienteId) throws ErrorServiceException {
        if (clienteRepository.findByIdAndEliminadoIsFalse(clienteId).isEmpty()) {
            throw new ErrorServiceException("Cliente no encontrado con ID: " + clienteId);
        }
        try {
            List<Alquiler> lista = ((AlquilerRepository) repository).findAllByClienteIdAndEliminadoIsFalse(clienteId);
            return lista.stream().map(alquilerMapper::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar alquileres del cliente: " + e.getMessage());
        }
    }

    public List<AlquilerDTO> listarPendientesFacturaPorCliente(String clienteId) throws ErrorServiceException {
        if (clienteRepository.findByIdAndEliminadoIsFalse(clienteId).isEmpty()) {
            throw new ErrorServiceException("Cliente no encontrado con ID: " + clienteId);
        }
        try {
            List<Alquiler> lista = ((AlquilerRepository) repository).findPendientesFacturaPorCliente(clienteId);
            return lista.stream().map(alquilerMapper::toDTO).collect(Collectors.toList());
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar alquileres pendientes de facturación del cliente: " + e.getMessage());
        }
    }

    public AlquilerDTO altaDTO(AlquilerDTO dto) throws ErrorServiceException {
        Cliente cli = findCliente(dto.getIdCliente());
        Vehiculo veh = findVehiculo(dto.getIdVehiculo());
        List<Documentacion> docs = (dto.getDocumentacionIds() == null || dto.getDocumentacionIds().isEmpty())
                ? List.of()
                : documentacionRepository.findAllById(dto.getDocumentacionIds());

        Alquiler entidad = alquilerMapper.toEntity(dto, cli, veh, docs);
        Alquiler guardado = alta(entidad);
        generarFacturaInicial(guardado);

        // Synchronize vehicle state based on all current rentals
        sincronizarEstadoVehiculo(veh.getId());
        
        return alquilerMapper.toDTO(guardado);
    }

    public Optional<AlquilerDTO> modificarDTO(String id, AlquilerDTO dto) throws ErrorServiceException {
        Cliente cli = findCliente(dto.getIdCliente());
        Vehiculo veh = findVehiculo(dto.getIdVehiculo());
        List<Documentacion> docs = (dto.getDocumentacionIds() == null || dto.getDocumentacionIds().isEmpty())
                ? List.of()
                : documentacionRepository.findAllById(dto.getDocumentacionIds());

        // Get old vehicle to sync its state after change
        Optional<Alquiler> existingOpt = obtener(id);
        String oldVehiculoId = existingOpt.map(a -> a.getVehiculo().getId()).orElse(null);

        Alquiler entidad = alquilerMapper.toEntity(dto, cli, veh, docs);
        Optional<Alquiler> mod = modificar(id, entidad);
        
        // Synchronize both old and new vehicle states
        if (oldVehiculoId != null && !oldVehiculoId.equals(veh.getId())) {
            sincronizarEstadoVehiculo(oldVehiculoId);
        }
        sincronizarEstadoVehiculo(veh.getId());
        
        return mod.map(alquilerMapper::toDTO);
    }

    // =============== Métodos solicitados ===============
    public void crearAlquiler(LocalDate fechaDesde, LocalDate fechaHasta, String idCliente, String idVehiculo) throws ErrorServiceException {
        Cliente cliente = findCliente(idCliente);
        Vehiculo vehiculo = findVehiculo(idVehiculo);

        Alquiler nuevo = new Alquiler();
        nuevo.crearAlquiler(fechaDesde, fechaHasta, cliente, vehiculo);

        Alquiler guardado = alta(nuevo);
        generarFacturaInicial(guardado);

        // Synchronize vehicle state based on all current rentals
        sincronizarEstadoVehiculo(idVehiculo);
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

        // Get old vehicle to sync its state after change
        Optional<Alquiler> existingOpt = obtener(id);
        String oldVehiculoId = existingOpt.map(a -> a.getVehiculo().getId()).orElse(null);

        Alquiler cambios = new Alquiler();
        cambios.setId(id);
        cambios.modificarAlquiler(fechaDesde, fechaHasta, cliente, vehiculo);

        modificar(id, cambios).orElseThrow(() -> new ErrorServiceException("Alquiler no encontrado"));
        
        // Synchronize both old and new vehicle states
        if (oldVehiculoId != null && !oldVehiculoId.equals(idVehiculo)) {
            sincronizarEstadoVehiculo(oldVehiculoId);
        }
        sincronizarEstadoVehiculo(idVehiculo);
    }

    public Alquiler buscarAlquiler(String id) throws ErrorServiceException {
        return obtenerEntidad(id);
    }

    // Nota: mantengo el nombre con el typo pedido por el enunciado
    public void eliminarAlquier(String id) throws ErrorServiceException {
        // Get the rental before deleting to sync the vehicle state
        Optional<Alquiler> alquilerOpt = obtener(id);
        String vehiculoId = alquilerOpt.map(a -> a.getVehiculo().getId()).orElse(null);
        
        baja(id);
        
        // Synchronize vehicle state after deletion
        if (vehiculoId != null) {
            sincronizarEstadoVehiculo(vehiculoId);
        }
    }

    public Collection<Alquiler> listarAlquiler() throws ErrorServiceException {
        return listarActivos();
    }

    public Collection<Alquiler> listarAlquierActivo() throws ErrorServiceException {
        return listarActivos();
    }

    // =============== Facturación automática ===============
    private void generarFacturaInicial(Alquiler alquiler) throws ErrorServiceException {
        if (alquiler == null) {
            throw new ErrorServiceException("No se pudo generar la factura para el alquiler indicado");
        }
        Vehiculo vehiculo = alquiler.getVehiculo();
        if (vehiculo == null) {
            throw new ErrorServiceException("El alquiler no tiene un vehículo asociado");
        }
        if (vehiculo.getCaracteristicaVehiculo() == null) {
            throw new ErrorServiceException("El vehículo no tiene configurada una característica para calcular el costo");
        }

        int cantidadDias = calcularDiasFacturados(alquiler.getFechaDesde(), alquiler.getFechaHasta());
        double total = costoVehiculoService.buscarCostoVehiculoVigente(
                        vehiculo.getCaracteristicaVehiculo().getId(),
                        alquiler.getFechaDesde())
                .map(costo -> redondear(costo.getCosto() * cantidadDias))
                .orElseThrow(() -> new ErrorServiceException(
                        "No hay un costo vigente para la característica del vehículo seleccionado"));

        facturaService.crearFacturaBorradorDesdeAlquiler(alquiler, total, cantidadDias);
    }

    private int calcularDiasFacturados(LocalDate fechaDesde, LocalDate fechaHasta) throws ErrorServiceException {
        if (fechaDesde == null || fechaHasta == null) {
            throw new ErrorServiceException("El alquiler debe indicar fechas desde y hasta");
        }
        long dias = ChronoUnit.DAYS.between(fechaDesde, fechaHasta);
        long total = dias >= 0 ? dias + 1 : 1;
        return (int) Math.max(1, total);
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
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

    // =============== Vehicle State Management ===============
    /**
     * Synchronizes vehicle state based on current active rentals.
     * This ensures the state reflects reality regardless of when rentals were created.
     */
    public void sincronizarEstadoVehiculo(String vehiculoId) throws ErrorServiceException {
        Vehiculo vehiculo = findVehiculo(vehiculoId);
        LocalDate today = LocalDate.now();
        
        boolean tieneAlquilerActivo = ((AlquilerRepository) repository).tieneAlquilerActivo(vehiculoId, today);
        
        EstadoVehiculo estadoEsperado = tieneAlquilerActivo ? EstadoVehiculo.ALQUILADO : EstadoVehiculo.DISPONIBLE;
        
        if (vehiculo.getEstadoVehiculo() != estadoEsperado) {
            vehiculo.setEstadoVehiculo(estadoEsperado);
            vehiculoRepository.save(vehiculo);
        }
    }
    
    /**
     * Synchronizes all vehicle states based on current date.
     * Should be called periodically (e.g., daily scheduled task) or on-demand.
     */
    public void sincronizarTodosLosEstadosVehiculos() throws ErrorServiceException {
        try {
            List<Vehiculo> vehiculos = vehiculoRepository.findAll();
            LocalDate today = LocalDate.now();
            
            for (Vehiculo vehiculo : vehiculos) {
                if (!vehiculo.getEliminado()) {
                    boolean tieneAlquilerActivo = ((AlquilerRepository) repository)
                        .tieneAlquilerActivo(vehiculo.getId(), today);
                    
                    EstadoVehiculo estadoEsperado = tieneAlquilerActivo 
                        ? EstadoVehiculo.ALQUILADO 
                        : EstadoVehiculo.DISPONIBLE;
                    
                    if (vehiculo.getEstadoVehiculo() != estadoEsperado) {
                        vehiculo.setEstadoVehiculo(estadoEsperado);
                        vehiculoRepository.save(vehiculo);
                    }
                }
            }
        } catch (Exception e) {
            throw new ErrorServiceException("Error sincronizando estados de vehículos: " + e.getMessage());
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
