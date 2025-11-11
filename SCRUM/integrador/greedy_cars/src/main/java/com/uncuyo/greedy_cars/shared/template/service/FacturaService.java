package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.DetalleFacturaDTO;
import com.uncuyo.greedy_cars.shared.template.dto.FacturaDTO;
import com.uncuyo.greedy_cars.shared.template.dto.FormaDePagoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.DetalleFactura;
import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import com.uncuyo.greedy_cars.shared.template.entity.FormaDePago;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.enums.EstadoFactura;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.DetalleFacturaMapper;
import com.uncuyo.greedy_cars.shared.template.mapper.FacturaMapper;
import com.uncuyo.greedy_cars.shared.template.mapper.FormaDePagoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.AlquilerRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ClienteRepository;
import com.uncuyo.greedy_cars.shared.template.repository.FacturaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class FacturaService extends BaseService<Factura, String> {

    private static final Logger log = LoggerFactory.getLogger(FacturaService.class);

    private final FacturaRepository facturaRepository;
    private final AlquilerRepository alquilerRepository;
    private final ClienteRepository clienteRepository;
    private final FacturaMapper facturaMapper;
    private final DetalleFacturaMapper detalleFacturaMapper;
    private final FormaDePagoMapper formaDePagoMapper;

    public FacturaService(
            FacturaRepository facturaRepository,
            AlquilerRepository alquilerRepository,
            ClienteRepository clienteRepository,
            FacturaMapper facturaMapper,
            DetalleFacturaMapper detalleFacturaMapper,
            FormaDePagoMapper formaDePagoMapper) {
        super(facturaRepository);
        this.facturaRepository = facturaRepository;
        this.alquilerRepository = alquilerRepository;
        this.clienteRepository = clienteRepository;
        this.facturaMapper = facturaMapper;
        this.detalleFacturaMapper = detalleFacturaMapper;
        this.formaDePagoMapper = formaDePagoMapper;
    }

    @Transactional(readOnly = true)
    public List<FacturaDTO> listarActivosDTO() throws ErrorServiceException {
        List<Factura> facturas = listarActivos();
        return facturas.stream()
                .map(this::convertirConClienteSeguro)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<FacturaDTO> obtenerDTO(String id) throws ErrorServiceException {
        Optional<Factura> factura = obtener(id);
        return factura.map(this::convertirConClienteSeguro);
    }

    @Transactional(readOnly = true)
    public List<FacturaDTO> listarFacturaPorEstado(EstadoFactura estado) throws ErrorServiceException {
        if (estado == null) {
            return listarActivosDTO();
        }
        List<Factura> facturas = facturaRepository.findAllByEstadoAndEliminadoIsFalse(estado);
        return facturas.stream()
                .map(this::convertirConClienteSeguro)
                .collect(Collectors.toList());
    }

    public FacturaDTO altaDTO(FacturaDTO dto) throws ErrorServiceException {
        try {
            Factura factura = prepararFacturaDesdeDTO(dto);
            Factura guardada = alta(factura);
            return convertirConClienteSeguro(guardada);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear la factura", e);
        }
    }

    public Optional<FacturaDTO> modificarDTO(String id, FacturaDTO dto) throws ErrorServiceException {
        try {
            Factura factura = prepararFacturaDesdeDTO(dto);
            Optional<Factura> modificada = modificar(id, factura);
            return modificada.map(this::convertirConClienteSeguro);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar la factura", e);
        }
    }

    @Transactional(readOnly = true)
    public List<DetalleFacturaDTO> listarDetalles(String facturaId) throws ErrorServiceException {
        Factura factura = obtenerEntidad(facturaId);
        List<DetalleFactura> detalles = factura.getDetalles();
        return detalleFacturaMapper.toDTOList(detalles == null ? new ArrayList<>() : new ArrayList<>(detalles));
    }

    @Transactional(readOnly = true)
    public List<FormaDePagoDTO> listarFormasPago(String facturaId) throws ErrorServiceException {
        Factura factura = obtenerEntidad(facturaId);
        List<FormaDePago> formas = factura.getFormasPago();
        return formaDePagoMapper.toDTOList(formas == null ? new ArrayList<>() : new ArrayList<>(formas));
    }

    private Factura prepararFacturaDesdeDTO(FacturaDTO dto) throws ErrorServiceException {
        if (dto == null) {
            throw new ErrorServiceException("Los datos de la factura son obligatorios");
        }

        Factura factura = facturaMapper.toEntity(dto);
        if (StringUtils.hasText(dto.getClienteId())) {
            factura.setCliente(obtenerClienteActivo(dto.getClienteId()));
        } else {
            factura.setCliente(null);
        }
        if (factura.getEstado() == null) {
            factura.setEstado(EstadoFactura.SIN_DEFINIR);
        }

        List<DetalleFactura> detallesConstruidos = null;
        if (dto.getDetalles() != null) {
            detallesConstruidos = new ArrayList<>();
            for (DetalleFacturaDTO detalleDTO : dto.getDetalles()) {
                DetalleFactura detalle = detalleFacturaMapper.toEntity(detalleDTO);
                detalle.setAlquiler(obtenerAlquilerActivo(detalleDTO.getAlquilerId()));
                detalle.setFactura(factura);
                detalle.setEliminado(Boolean.FALSE);
                detallesConstruidos.add(detalle);
            }
            factura.setDetalles(detallesConstruidos);
        }

        List<FormaDePago> formasConstruidas = null;
        if (dto.getFormasPago() != null) {
            formasConstruidas = new ArrayList<>();
            for (FormaDePagoDTO formaDTO : dto.getFormasPago()) {
                FormaDePago forma = formaDePagoMapper.toEntity(formaDTO);
                forma.setFactura(factura);
                forma.setEliminado(Boolean.FALSE);
                formasConstruidas.add(forma);
            }
            factura.setFormasPago(formasConstruidas);
        }

        Double totalDesdeDTO = dto.getTotalPagado();
        if ((totalDesdeDTO == null || Double.compare(totalDesdeDTO, 0D) == 0)
                && detallesConstruidos != null && !detallesConstruidos.isEmpty()) {
            factura.setTotalPagado(calcularTotal(detallesConstruidos));
        } else {
            factura.setTotalPagado(totalDesdeDTO);
        }

        return factura;
    }

    private FacturaDTO convertirConClienteSeguro(Factura factura) {
        if (factura == null) {
            return null;
        }
        FacturaDTO dto = facturaMapper.toDTO(factura);
        Cliente cliente = extraerClienteSeguro(factura);
        if (cliente != null) {
            dto.setClienteId(cliente.getId());
            dto.setClienteNombreCompleto(construirNombreCliente(cliente));
        } else {
            dto.setClienteId(null);
            dto.setClienteNombreCompleto(null);
        }
        return dto;
    }

    private Cliente extraerClienteSeguro(Factura factura) {
        try {
            return factura.getCliente();
        } catch (EntityNotFoundException ex) {
            log.warn("Factura {} referencia un cliente inexistente. Se ignorará la relación", factura.getId());
            return null;
        }
    }

    private String construirNombreCliente(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        String nombre = cliente.getNombre() != null ? cliente.getNombre().trim() : "";
        String apellido = cliente.getApellido() != null ? cliente.getApellido().trim() : "";
        String fullName = (nombre + " " + apellido).trim();
        return fullName.isEmpty() ? null : fullName;
    }

    private double calcularTotal(List<DetalleFactura> detalles) {
        return detalles.stream()
                .map(DetalleFactura::getSubtotal)
                .filter(Objects::nonNull)
                .mapToDouble(Double::doubleValue)
                .sum();
    }

    private Alquiler obtenerAlquilerActivo(String alquilerId) throws ErrorServiceException {
        if (alquilerId == null || alquilerId.isBlank()) {
            throw new ErrorServiceException("Debe indicar un alquiler válido para el detalle de factura");
        }
        return alquilerRepository.findByIdAndEliminadoIsFalse(alquilerId)
                .orElseThrow(() -> new ErrorServiceException("Alquiler no encontrado o eliminado"));
    }

    private Cliente obtenerClienteActivo(String clienteId) throws ErrorServiceException {
        if (clienteId == null || clienteId.isBlank()) {
            throw new ErrorServiceException("Debe indicar un cliente válido para la factura");
        }
        return clienteRepository.findByIdAndEliminadoIsFalse(clienteId)
                .orElseThrow(() -> new ErrorServiceException("Cliente no encontrado o eliminado"));
    }

    @Override
    protected void actualizarEntidad(Factura entidadExistente, Factura entidadNueva) {
        if (entidadNueva.getNumeroFactura() != null) {
            entidadExistente.setNumeroFactura(entidadNueva.getNumeroFactura());
        }
        if (entidadNueva.getFechaFactura() != null) {
            entidadExistente.setFechaFactura(entidadNueva.getFechaFactura());
        }
        if (entidadNueva.getTotalPagado() != null) {
            entidadExistente.setTotalPagado(entidadNueva.getTotalPagado());
        }
        if (entidadNueva.getEstado() != null) {
            entidadExistente.setEstado(entidadNueva.getEstado());
        }
        if (entidadNueva.getCliente() != null) {
            entidadExistente.setCliente(entidadNueva.getCliente());
        }

        if (entidadNueva.getDetalles() != null) {
            if (entidadExistente.getDetalles() == null) {
                entidadExistente.setDetalles(new ArrayList<>());
            } else {
                entidadExistente.limpiarDetalles();
                entidadExistente.getDetalles().clear();
            }
            for (DetalleFactura detalle : entidadNueva.getDetalles()) {
                detalle.setFactura(entidadExistente);
                if (detalle.getEliminado() == null) {
                    detalle.setEliminado(Boolean.FALSE);
                }
                entidadExistente.getDetalles().add(detalle);
            }
        }

        if (entidadNueva.getFormasPago() != null) {
            if (entidadExistente.getFormasPago() == null) {
                entidadExistente.setFormasPago(new ArrayList<>());
            } else {
                entidadExistente.limpiarFormasPago();
                entidadExistente.getFormasPago().clear();
            }
            for (FormaDePago forma : entidadNueva.getFormasPago()) {
                forma.setFactura(entidadExistente);
                if (forma.getEliminado() == null) {
                    forma.setEliminado(Boolean.FALSE);
                }
                entidadExistente.getFormasPago().add(forma);
            }
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Factura factura) throws ErrorServiceException {
        try {
            if (factura == null) {
                throw new ErrorServiceException("Debe indicar la factura");
            }

            if (useCase == BaseUseCaseService.BAJA) {
                return;
            }

            if (factura.getFechaFactura() == null) {
                throw new ErrorServiceException("La fecha de la factura es obligatoria");
            }
            if (useCase == BaseUseCaseService.ALTA && factura.getTotalPagado() == null) {
                throw new ErrorServiceException("Debe indicar el total pagado");
            }
            if (factura.getTotalPagado() != null && factura.getTotalPagado() < 0) {
                throw new ErrorServiceException("El total pagado no puede ser negativo");
            }
            if (factura.getNumeroFactura() != null && factura.getNumeroFactura() <= 0) {
                throw new ErrorServiceException("El número de factura debe ser positivo");
            }
            if (factura.getEstado() == null) {
                factura.setEstado(EstadoFactura.SIN_DEFINIR);
            }
            if (useCase == BaseUseCaseService.ALTA && factura.getCliente() == null) {
                throw new ErrorServiceException("Debe asociar la factura a un cliente");
            }

            if (useCase == BaseUseCaseService.ALTA) {
                if (factura.getDetalles() == null || factura.getDetalles().isEmpty()) {
                    throw new ErrorServiceException("La factura debe contener al menos un detalle");
                }
                boolean requiereFormaPago = factura.getEstado() == EstadoFactura.PAGADA;
                if (requiereFormaPago && (factura.getFormasPago() == null || factura.getFormasPago().isEmpty())) {
                    throw new ErrorServiceException("La factura pagada debe registrar al menos una forma de pago");
                }
            }

            if (factura.getDetalles() != null) {
                factura.getDetalles().forEach(this::validarDetalleFactura);
            }

            if (factura.getFormasPago() != null) {
                factura.getFormasPago().forEach(this::validarFormaPago);
            }

            if (factura.getNumeroFactura() != null) {
                facturaRepository.findByNumeroFacturaAndEliminadoIsFalse(factura.getNumeroFactura())
                        .ifPresent(existente -> {
                            boolean mismaFactura = Objects.equals(existente.getId(), factura.getId());
                            boolean esAlta = useCase == BaseUseCaseService.ALTA;
                            if (esAlta || !mismaFactura) {
                                throw new ErrorServiceException("Ya existe una factura con el número indicado");
                            }
                        });
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistema al validar la factura", e);
        }
    }

    @Override
    protected void preAlta(Factura entidad) throws ErrorServiceException {
        if (entidad.getNumeroFactura() == null || entidad.getNumeroFactura() <= 0) {
            Long max = facturaRepository.obtenerMaxNumeroFactura();
            if (max == null) {
                max = 0L;
            }
            entidad.setNumeroFactura(max + 1);
        }
    }

    @Override
    protected void preBaja(Factura entidad) throws ErrorServiceException {
        if (entidad.getDetalles() != null) {
            entidad.getDetalles().forEach(detalle -> detalle.setEliminado(true));
        }
        if (entidad.getFormasPago() != null) {
            entidad.getFormasPago().forEach(forma -> forma.setEliminado(true));
        }
    }

    private void validarDetalleFactura(DetalleFactura detalle) {
        if (detalle.getCantidad() == null || detalle.getCantidad() < 1) {
            throw new ErrorServiceException("Cada detalle debe tener una cantidad válida");
        }
        if (detalle.getSubtotal() == null || detalle.getSubtotal() < 0) {
            throw new ErrorServiceException("El subtotal del detalle no puede ser negativo");
        }
        if (detalle.getAlquiler() == null || detalle.getAlquiler().getId() == null) {
            throw new ErrorServiceException("Cada detalle debe estar asociado a un alquiler");
        }
    }

    public Factura crearFacturaBorradorDesdeAlquiler(Alquiler alquiler, double monto, int cantidadDias) throws ErrorServiceException {
        if (alquiler == null) {
            throw new ErrorServiceException("No se indicó el alquiler para generar la factura");
        }
        if (alquiler.getCliente() == null) {
            throw new ErrorServiceException("El alquiler no tiene un cliente asociado");
        }
        if (cantidadDias <= 0) {
            throw new ErrorServiceException("La cantidad de días debe ser mayor a cero");
        }

        double totalRedondeado = redondear(monto);

        Factura factura = new Factura();
        factura.setFechaFactura(LocalDate.now());
        factura.setEstado(EstadoFactura.SIN_DEFINIR);
        factura.setCliente(alquiler.getCliente());
        factura.setTotalPagado(totalRedondeado);
        factura.setFormasPago(new ArrayList<>());

        DetalleFactura detalle = new DetalleFactura();
        detalle.setCantidad(cantidadDias);
        detalle.setSubtotal(totalRedondeado);
        detalle.setAlquiler(alquiler);
        detalle.setEliminado(false);
        factura.agregarDetalle(detalle);

        return alta(factura);
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private void validarFormaPago(FormaDePago formaDePago) {
        if (formaDePago.getTipoPago() == null) {
            throw new ErrorServiceException("Cada forma de pago debe tener un tipo definido");
        }
    }
}
