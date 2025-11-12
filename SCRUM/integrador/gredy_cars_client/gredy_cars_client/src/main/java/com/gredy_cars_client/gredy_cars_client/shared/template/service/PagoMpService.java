package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.PagoMpDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.MercadoPagoPreferenceRequest;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.MercadoPagoPreferenceResponse;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

@Service
public class PagoMpService {

    private final PagoMpDao pagoMpDao;

    public PagoMpService(PagoMpDao pagoMpDao) {
        this.pagoMpDao = pagoMpDao;
    }

    public MercadoPagoPreferenceResponse generarPreferenciaPorFactura(String facturaId) throws ErrorServiceException {
        if (!StringUtils.hasText(facturaId)) {
            throw new ErrorServiceException("Debe indicar la factura");
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
            throw new ErrorServiceException("La cantidad de días debe ser al menos 1");
        }
        MercadoPagoPreferenceRequest request = new MercadoPagoPreferenceRequest();
        request.setVehiculoId(vehiculoId.trim());
        request.setCantidadDias(dias);
        return pagoMpDao.crearPreferencia(request);
    }
}
