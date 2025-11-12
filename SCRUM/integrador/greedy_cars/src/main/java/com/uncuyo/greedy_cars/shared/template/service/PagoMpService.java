package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dao.PagoMpDao;
import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceRequest;
import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceResponse;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class PagoMpService {

    private final PagoMpDao pagoMpDao;

    public PagoMpService(PagoMpDao pagoMpDao) {
        this.pagoMpDao = pagoMpDao;
    }

    public MercadoPagoPreferenceResponse generarPreferenciaPorFactura(String facturaId) throws ErrorServiceException {
        if (!StringUtils.hasText(facturaId)) {
            throw new ErrorServiceException("Debe indicar el ID de la factura");
        }
        MercadoPagoPreferenceRequest request = new MercadoPagoPreferenceRequest();
        request.setFacturaId(facturaId.trim());
        return pagoMpDao.crearPreferencia(request);
    }

    public MercadoPagoPreferenceResponse generarPreferenciaPorAlquiler(String vehiculoId, int dias) throws ErrorServiceException {
        if (!StringUtils.hasText(vehiculoId)) {
            throw new ErrorServiceException("Debe indicar el vehículo");
        }
        if (dias < 1) {
            throw new ErrorServiceException("La cantidad de días debe ser mayor o igual a 1");
        }
        MercadoPagoPreferenceRequest request = new MercadoPagoPreferenceRequest();
        request.setVehiculoId(vehiculoId.trim());
        request.setCantidadDias(dias);
        return pagoMpDao.crearPreferencia(request);
    }
}
