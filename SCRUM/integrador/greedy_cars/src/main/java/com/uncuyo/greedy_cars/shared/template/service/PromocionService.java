package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.PromocionDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.Promocion;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.PromocionMapper;
import com.uncuyo.greedy_cars.shared.template.repository.ClienteRepository;
import com.uncuyo.greedy_cars.shared.template.repository.PromocionRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class PromocionService extends BaseService<Promocion, String> {

    private final PromocionRepository promocionRepository;
    private final PromocionMapper promocionMapper;
    private final ClienteRepository clienteRepository;
    private final NotificacionCorreoService notificacionCorreoService;

    public PromocionService(
            PromocionRepository promocionRepository,
            PromocionMapper promocionMapper,
            ClienteRepository clienteRepository,
            NotificacionCorreoService notificacionCorreoService) {
        super(promocionRepository);
        this.promocionRepository = promocionRepository;
        this.promocionMapper = promocionMapper;
        this.clienteRepository = clienteRepository;
        this.notificacionCorreoService = notificacionCorreoService;
    }

    @Transactional(readOnly = true)
    public List<PromocionDTO> listarActivosDTO() throws ErrorServiceException {
        return listarActivos().stream()
                .map(promocionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PromocionDTO> listarVigentesDTO(LocalDate fechaReferencia) throws ErrorServiceException {
        LocalDate referencia = fechaReferencia != null ? fechaReferencia : LocalDate.now();
        return promocionRepository.findActivas(referencia).stream()
                .map(promocionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PromocionDTO> obtenerDTO(String id) throws ErrorServiceException {
        return obtener(id).map(promocionMapper::toDTO);
    }

    public PromocionDTO altaDTO(PromocionDTO dto) throws ErrorServiceException {
        Promocion promocion = promocionMapper.toEntity(dto);
        configurarDestinatarios(promocion, dto);
        Promocion guardada = alta(promocion);
        notificacionCorreoService.enviarNuevaPromocion(guardada);
        return promocionMapper.toDTO(guardada);
    }

    public Optional<PromocionDTO> modificarDTO(String id, PromocionDTO dto) throws ErrorServiceException {
        Promocion promocion = promocionMapper.toEntity(dto);
        configurarDestinatarios(promocion, dto);
        Optional<Promocion> modificada = modificar(id, promocion);
        return modificada.map(promocionMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public PromocionDTO obtenerPorCodigoDTO(String codigo) throws ErrorServiceException {
        return promocionMapper.toDTO(buscarPorCodigo(codigo));
    }

    @Transactional(readOnly = true)
    public Promocion buscarPorCodigo(String codigo) throws ErrorServiceException {
        return buscarPorCodigo(codigo, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Promocion buscarPorCodigo(String codigo, LocalDate fechaReferencia) throws ErrorServiceException {
        if (!StringUtils.hasText(codigo)) {
            throw new ErrorServiceException("Debe indicar un código de promoción");
        }
        LocalDate referencia = fechaReferencia != null ? fechaReferencia : LocalDate.now();
        return promocionRepository.buscarVigentePorCodigo(codigo.trim(), referencia)
                .orElseThrow(() -> new ErrorServiceException("No existe una promoción vigente con el código indicado"));
    }

    public Promocion obtenerPromocionVigenteParaClienteObligatoria(String codigo, Cliente cliente, LocalDate fechaReferencia)
            throws ErrorServiceException {
        if (!StringUtils.hasText(codigo)) {
            throw new ErrorServiceException("El código de promoción es obligatorio");
        }
        Promocion promocion = buscarPorCodigo(codigo.trim(), fechaReferencia);
        validarClienteDestinatario(promocion, cliente);
        return promocion;
    }

    public Promocion obtenerPromocionVigenteParaClientePorId(
            String promocionId, Cliente cliente, LocalDate fechaReferencia) throws ErrorServiceException {
        if (!StringUtils.hasText(promocionId)) {
            throw new ErrorServiceException("Debe indicar la promoción a validar");
        }
        Promocion promocion = promocionRepository.findByIdAndEliminadoIsFalse(promocionId)
                .orElseThrow(() -> new ErrorServiceException("Promoción no encontrada o eliminada"));
        validarVigencia(promocion, fechaReferencia);
        validarClienteDestinatario(promocion, cliente);
        return promocion;
    }

    @Override
    protected void actualizarEntidad(Promocion existente, Promocion nueva) {
        if (StringUtils.hasText(nueva.getCodigoDescuento())) {
            existente.setCodigoDescuento(nueva.getCodigoDescuento().trim());
        }
        if (nueva.getDescripcionDescuento() != null) {
            existente.setDescripcionDescuento(nueva.getDescripcionDescuento());
        }
        if (nueva.getPorcentajeDescuento() != null) {
            existente.setPorcentajeDescuento(nueva.getPorcentajeDescuento());
        }
        if (nueva.getFechaInicioPromocion() != null) {
            existente.setFechaInicioPromocion(nueva.getFechaInicioPromocion());
        }
        if (nueva.getFechaFinPromocion() != null) {
            existente.setFechaFinPromocion(nueva.getFechaFinPromocion());
        }
        existente.setAplicaATodos(nueva.isAplicaATodos());
        if (nueva.getClientesDestino() != null) {
            existente.getClientesDestino().clear();
            existente.getClientesDestino().addAll(nueva.getClientesDestino());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, Promocion promocion) throws ErrorServiceException {
        try {
            if (promocion == null) {
                throw new ErrorServiceException("Debe indicar la promoción");
            }
            if (useCase == BaseUseCaseService.BAJA) {
                return;
            }

            if (!StringUtils.hasText(promocion.getCodigoDescuento())) {
                throw new ErrorServiceException("El código de descuento es obligatorio");
            }
            String codigoNormalizado = promocion.getCodigoDescuento().trim();
            promocion.setCodigoDescuento(codigoNormalizado);

            Double porcentaje = promocion.getPorcentajeDescuento();
            if (porcentaje == null || porcentaje <= 0 || porcentaje > 100) {
                throw new ErrorServiceException("El porcentaje de descuento debe ser mayor a 0 y menor o igual a 100");
            }

            if (promocion.getFechaInicioPromocion() == null || promocion.getFechaFinPromocion() == null) {
                throw new ErrorServiceException("Las fechas de vigencia son obligatorias");
            }
            if (promocion.getFechaFinPromocion().isBefore(promocion.getFechaInicioPromocion())) {
                throw new ErrorServiceException("La fecha de fin no puede ser anterior a la fecha de inicio");
            }

            if (!promocion.isAplicaATodos()) {
                if (promocion.getClientesDestino() == null || promocion.getClientesDestino().isEmpty()) {
                    throw new ErrorServiceException("Debe seleccionar al menos un cliente destinatario");
                }
            } else if (promocion.getClientesDestino() != null) {
                promocion.getClientesDestino().clear();
            }

            promocionRepository.findByCodigoDescuentoIgnoreCaseAndEliminadoIsFalse(codigoNormalizado)
                    .ifPresent(existente -> {
                        boolean mismaPromocion = existente.getId() != null && existente.getId().equals(promocion.getId());
                        boolean esAlta = useCase == BaseUseCaseService.ALTA;
                        if (esAlta || !mismaPromocion) {
                            throw new ErrorServiceException("Ya existe una promoción con el código indicado");
                        }
                    });
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistema al validar la promoción", e);
        }
    }

    private void configurarDestinatarios(Promocion promocion, PromocionDTO dto) throws ErrorServiceException {
        boolean aplicaATodos = dto == null || dto.getAplicaATodos() == null || Boolean.TRUE.equals(dto.getAplicaATodos());
        promocion.setAplicaATodos(aplicaATodos);
        if (aplicaATodos) {
            promocion.setClientesDestino(new HashSet<>());
            return;
        }
        Set<String> idsSolicitados = dto.getClientesDestinoIds() != null ? dto.getClientesDestinoIds() : Collections.emptySet();
        Set<String> idsNormalizados = idsSolicitados.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toSet());
        if (idsNormalizados.isEmpty()) {
            throw new ErrorServiceException("Debe seleccionar al menos un cliente destinatario");
        }
        promocion.setClientesDestino(cargarClientes(idsNormalizados));
    }

    private Set<Cliente> cargarClientes(Set<String> ids) throws ErrorServiceException {
        List<Cliente> encontrados = clienteRepository.findAllById(ids);
        Set<String> encontradosIds = encontrados.stream()
                .filter(cliente -> cliente.getEliminado() == null || !cliente.getEliminado())
                .map(Cliente::getId)
                .collect(Collectors.toSet());
        List<String> faltantes = ids.stream()
                .filter(id -> !encontradosIds.contains(id))
                .collect(Collectors.toList());
        if (!faltantes.isEmpty()) {
            throw new ErrorServiceException("Clientes no encontrados para la promoción: " + String.join(", ", faltantes));
        }
        return encontrados.stream()
                .filter(cliente -> cliente.getEliminado() == null || !cliente.getEliminado())
                .collect(Collectors.toSet());
    }

    private void validarVigencia(Promocion promocion, LocalDate fechaReferencia) throws ErrorServiceException {
        LocalDate referencia = fechaReferencia != null ? fechaReferencia : LocalDate.now();
        if (promocion.getFechaInicioPromocion() != null && referencia.isBefore(promocion.getFechaInicioPromocion())) {
            throw new ErrorServiceException("La promoción aún no está vigente");
        }
        if (promocion.getFechaFinPromocion() != null && referencia.isAfter(promocion.getFechaFinPromocion())) {
            throw new ErrorServiceException("La promoción ya no está vigente");
        }
        if (Boolean.TRUE.equals(promocion.getEliminado())) {
            throw new ErrorServiceException("La promoción se encuentra eliminada");
        }
    }

    private void validarClienteDestinatario(Promocion promocion, Cliente cliente) throws ErrorServiceException {
        if (promocion == null) {
            throw new ErrorServiceException("No se encontró la promoción indicada");
        }
        if (cliente == null || !StringUtils.hasText(cliente.getId())) {
            throw new ErrorServiceException("Debe indicar el cliente para validar la promoción");
        }
        if (!promocion.isAplicaATodos()) {
            boolean habilitado = promocion.getClientesDestino().stream()
                    .map(Cliente::getId)
                    .filter(StringUtils::hasText)
                    .anyMatch(id -> id.equals(cliente.getId()));
            if (!habilitado) {
                throw new ErrorServiceException("La promoción no está disponible para el cliente seleccionado");
            }
        }
    }
}
