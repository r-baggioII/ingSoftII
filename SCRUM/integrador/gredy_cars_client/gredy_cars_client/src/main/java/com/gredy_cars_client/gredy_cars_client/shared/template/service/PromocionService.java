package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.PromocionDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.PromocionDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PromocionService extends BaseClientService<PromocionDTO, String> {

    private final PromocionDao promocionDao;

    public PromocionService(PromocionDao dao) {
        super(dao);
        this.promocionDao = dao;
    }

    public List<PromocionDTO> listarVigentes() throws ErrorServiceException {
        return promocionDao.listarVigentes();
    }

    @Override
    protected void validar(BaseUseCaseService useCase, PromocionDTO dto) throws ErrorServiceException {
        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }
        if (dto == null) {
            throw new ErrorServiceException("Debe indicar la promoción");
        }
        if (!StringUtils.hasText(dto.getCodigoDescuento())) {
            throw new ErrorServiceException("El código de descuento es obligatorio");
        }
        dto.setCodigoDescuento(dto.getCodigoDescuento().trim());

        Double porcentaje = dto.getPorcentajeDescuento();
        if (porcentaje == null || porcentaje <= 0 || porcentaje > 100) {
            throw new ErrorServiceException("El porcentaje debe ser mayor a 0 y menor o igual a 100");
        }

        if (dto.getFechaInicioPromocion() == null || dto.getFechaFinPromocion() == null) {
            throw new ErrorServiceException("Debe indicar las fechas de vigencia");
        }
        if (dto.getFechaFinPromocion().isBefore(dto.getFechaInicioPromocion())) {
            throw new ErrorServiceException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        boolean aplicaATodos = dto.getAplicaATodos() == null || Boolean.TRUE.equals(dto.getAplicaATodos());
        dto.setAplicaATodos(aplicaATodos);
        if (!aplicaATodos) {
            Set<String> ids = dto.getClientesDestinoIds();
            if (ids == null || ids.isEmpty()) {
                throw new ErrorServiceException("Debe seleccionar al menos un cliente destinatario");
            }
        }
    }

    @Override
    protected void preAlta(PromocionDTO dto) throws ErrorServiceException {
        normalizarDestinatarios(dto);
    }

    @Override
    protected void preModificacion(String id, PromocionDTO dto) throws ErrorServiceException {
        normalizarDestinatarios(dto);
    }

    private void normalizarDestinatarios(PromocionDTO dto) {
        if (dto.getClientesDestinoIds() == null) {
            dto.setClientesDestinoIds(new HashSet<>());
            return;
        }
        Set<String> normalizados = dto.getClientesDestinoIds().stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toSet());
        dto.setClientesDestinoIds(normalizados);
    }
}
