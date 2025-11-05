package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.DetalleFacturaDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.DetalleFactura;
import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.DetalleFacturaMapper;
import com.uncuyo.greedy_cars.shared.template.repository.AlquilerRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DetalleFacturaRepository;
import com.uncuyo.greedy_cars.shared.template.repository.FacturaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DetalleFacturaService extends BaseService<DetalleFactura, String> {

    private final DetalleFacturaRepository detalleFacturaRepository;
    private final FacturaRepository facturaRepository;
    private final AlquilerRepository alquilerRepository;
    private final DetalleFacturaMapper detalleFacturaMapper;

    public DetalleFacturaService(
            DetalleFacturaRepository detalleFacturaRepository,
            FacturaRepository facturaRepository,
            AlquilerRepository alquilerRepository,
            DetalleFacturaMapper detalleFacturaMapper) {
        super(detalleFacturaRepository);
        this.detalleFacturaRepository = detalleFacturaRepository;
        this.facturaRepository = facturaRepository;
        this.alquilerRepository = alquilerRepository;
        this.detalleFacturaMapper = detalleFacturaMapper;
    }

    @Transactional(readOnly = true)
    public List<DetalleFacturaDTO> listarActivosDTO() throws ErrorServiceException {
        List<DetalleFactura> detalles = listarActivos();
        return detalleFacturaMapper.toDTOList(detalles);
    }

    @Transactional(readOnly = true)
    public List<DetalleFacturaDTO> listarPorFactura(String facturaId) throws ErrorServiceException {
        if (facturaId == null || facturaId.isBlank()) {
            throw new ErrorServiceException("Debe indicar la factura");
        }
        List<DetalleFactura> detalles = detalleFacturaRepository.findAllByFacturaIdAndEliminadoIsFalse(facturaId);
        return detalleFacturaMapper.toDTOList(detalles);
    }

    @Transactional(readOnly = true)
    public Optional<DetalleFacturaDTO> obtenerDTO(String id) throws ErrorServiceException {
        Optional<DetalleFactura> detalle = obtener(id);
        return detalle.map(detalleFacturaMapper::toDTO);
    }

    public DetalleFacturaDTO altaDTO(DetalleFacturaDTO dto) throws ErrorServiceException {
        try {
            DetalleFactura detalle = prepararDetalleDesdeDTO(dto);
            DetalleFactura guardado = alta(detalle);
            return detalleFacturaMapper.toDTO(guardado);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear el detalle de factura", e);
        }
    }

    public Optional<DetalleFacturaDTO> modificarDTO(String id, DetalleFacturaDTO dto) throws ErrorServiceException {
        try {
            DetalleFactura detalle = prepararDetalleDesdeDTO(dto);
            Optional<DetalleFactura> modificado = modificar(id, detalle);
            return modificado.map(detalleFacturaMapper::toDTO);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar el detalle de factura", e);
        }
    }

    private DetalleFactura prepararDetalleDesdeDTO(DetalleFacturaDTO dto) throws ErrorServiceException {
        if (dto == null) {
            throw new ErrorServiceException("Los datos del detalle son obligatorios");
        }
        DetalleFactura detalle = detalleFacturaMapper.toEntity(dto);
        Factura factura = obtenerFacturaActiva(dto.getFacturaId());
        Alquiler alquiler = obtenerAlquilerActivo(dto.getAlquilerId());
        detalle.setFactura(factura);
        detalle.setAlquiler(alquiler);
        detalle.setEliminado(Boolean.FALSE);
        return detalle;
    }

    private Factura obtenerFacturaActiva(String facturaId) throws ErrorServiceException {
        if (facturaId == null || facturaId.isBlank()) {
            throw new ErrorServiceException("Debe indicar la factura asociada");
        }
        return facturaRepository.findByIdAndEliminadoIsFalse(facturaId)
                .orElseThrow(() -> new ErrorServiceException("Factura no encontrada o eliminada"));
    }

    private Alquiler obtenerAlquilerActivo(String alquilerId) throws ErrorServiceException {
        if (alquilerId == null || alquilerId.isBlank()) {
            throw new ErrorServiceException("Debe indicar el alquiler asociado");
        }
        return alquilerRepository.findByIdAndEliminadoIsFalse(alquilerId)
                .orElseThrow(() -> new ErrorServiceException("Alquiler no encontrado o eliminado"));
    }

    @Override
    protected void actualizarEntidad(DetalleFactura existente, DetalleFactura nuevo) {
        if (nuevo.getCantidad() != null) {
            existente.setCantidad(nuevo.getCantidad());
        }
        if (nuevo.getSubtotal() != null) {
            existente.setSubtotal(nuevo.getSubtotal());
        }
        if (nuevo.getAlquiler() != null) {
            existente.setAlquiler(nuevo.getAlquiler());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, DetalleFactura detalle) throws ErrorServiceException {
        try {
            if (detalle == null) {
                throw new ErrorServiceException("Debe indicar el detalle de factura");
            }
            if (useCase == BaseUseCaseService.BAJA) {
                return;
            }
            if (detalle.getCantidad() == null || detalle.getCantidad() < 1) {
                throw new ErrorServiceException("La cantidad debe ser al menos 1");
            }
            if (detalle.getSubtotal() == null || detalle.getSubtotal() < 0) {
                throw new ErrorServiceException("El subtotal no puede ser negativo");
            }
            if (detalle.getFactura() == null || detalle.getFactura().getId() == null) {
                throw new ErrorServiceException("Debe indicar una factura válida");
            }
            if (detalle.getAlquiler() == null || detalle.getAlquiler().getId() == null) {
                throw new ErrorServiceException("Debe indicar un alquiler válido");
            }
            if (detalle.getFactura().getEliminado() != null && detalle.getFactura().getEliminado()) {
                throw new ErrorServiceException("No se pueden agregar detalles a facturas eliminadas");
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistema al validar el detalle de factura", e);
        }
    }
}

