package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.FormaDePagoDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Factura;
import com.uncuyo.greedy_cars.shared.template.entity.FormaDePago;
import com.uncuyo.greedy_cars.shared.template.enums.BaseUseCaseService;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.FormaDePagoMapper;
import com.uncuyo.greedy_cars.shared.template.repository.FacturaRepository;
import com.uncuyo.greedy_cars.shared.template.repository.FormaDePagoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FormaDePagoService extends BaseService<FormaDePago, String> {

    private final FormaDePagoRepository formaDePagoRepository;
    private final FacturaRepository facturaRepository;
    private final FormaDePagoMapper formaDePagoMapper;

    public FormaDePagoService(
            FormaDePagoRepository formaDePagoRepository,
            FacturaRepository facturaRepository,
            FormaDePagoMapper formaDePagoMapper) {
        super(formaDePagoRepository);
        this.formaDePagoRepository = formaDePagoRepository;
        this.facturaRepository = facturaRepository;
        this.formaDePagoMapper = formaDePagoMapper;
    }

    @Transactional(readOnly = true)
    public List<FormaDePagoDTO> listarActivosDTO() throws ErrorServiceException {
        List<FormaDePago> formas = listarActivos();
        return formaDePagoMapper.toDTOList(formas);
    }

    @Transactional(readOnly = true)
    public List<FormaDePagoDTO> listarPorFactura(String facturaId) throws ErrorServiceException {
        if (facturaId == null || facturaId.isBlank()) {
            throw new ErrorServiceException("Debe indicar la factura");
        }
        List<FormaDePago> formas = formaDePagoRepository.findAllByFacturaIdAndEliminadoIsFalse(facturaId);
        return formaDePagoMapper.toDTOList(formas);
    }

    @Transactional(readOnly = true)
    public Optional<FormaDePagoDTO> obtenerDTO(String id) throws ErrorServiceException {
        Optional<FormaDePago> forma = obtener(id);
        return forma.map(formaDePagoMapper::toDTO);
    }

    public FormaDePagoDTO altaDTO(FormaDePagoDTO dto) throws ErrorServiceException {
        try {
            FormaDePago formaDePago = prepararFormaDesdeDTO(dto);
            FormaDePago guardada = alta(formaDePago);
            return formaDePagoMapper.toDTO(guardada);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al crear la forma de pago", e);
        }
    }

    public Optional<FormaDePagoDTO> modificarDTO(String id, FormaDePagoDTO dto) throws ErrorServiceException {
        try {
            FormaDePago formaDePago = prepararFormaDesdeDTO(dto);
            Optional<FormaDePago> modificada = modificar(id, formaDePago);
            return modificada.map(formaDePagoMapper::toDTO);
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error al modificar la forma de pago", e);
        }
    }

    private FormaDePago prepararFormaDesdeDTO(FormaDePagoDTO dto) throws ErrorServiceException {
        if (dto == null) {
            throw new ErrorServiceException("Los datos de la forma de pago son obligatorios");
        }
        FormaDePago forma = formaDePagoMapper.toEntity(dto);
        Factura factura = obtenerFacturaActiva(dto.getFacturaId());
        forma.setFactura(factura);
        forma.setEliminado(Boolean.FALSE);
        return forma;
    }

    private Factura obtenerFacturaActiva(String facturaId) throws ErrorServiceException {
        if (facturaId == null || facturaId.isBlank()) {
            throw new ErrorServiceException("Debe indicar la factura asociada");
        }
        return facturaRepository.findByIdAndEliminadoIsFalse(facturaId)
                .orElseThrow(() -> new ErrorServiceException("Factura no encontrada o eliminada"));
    }

    @Override
    protected void actualizarEntidad(FormaDePago existente, FormaDePago nueva) {
        if (nueva.getTipoPago() != null) {
            existente.setTipoPago(nueva.getTipoPago());
        }
        if (nueva.getObservacion() != null) {
            existente.setObservacion(nueva.getObservacion());
        }
        if (nueva.getFactura() != null) {
            existente.setFactura(nueva.getFactura());
        }
    }

    @Override
    protected void validar(BaseUseCaseService useCase, FormaDePago formaDePago) throws ErrorServiceException {
        try {
            if (formaDePago == null) {
                throw new ErrorServiceException("Debe indicar la forma de pago");
            }
            if (useCase == BaseUseCaseService.BAJA) {
                return;
            }
            if (formaDePago.getTipoPago() == null) {
                throw new ErrorServiceException("El tipo de pago es obligatorio");
            }
            if (formaDePago.getFactura() == null || formaDePago.getFactura().getId() == null) {
                throw new ErrorServiceException("Debe indicar una factura válida");
            }
            if (formaDePago.getFactura().getEliminado() != null && formaDePago.getFactura().getEliminado()) {
                throw new ErrorServiceException("No se pueden registrar pagos en facturas eliminadas");
            }
        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorServiceException("Error de Sistema al validar la forma de pago", e);
        }
    }
}

