package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.dto.MercadoPagoPreferenceResponse;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import org.springframework.stereotype.Service;

@Service
public class PagoMpService {

    private static final String MENSAJE_NO_DISPONIBLE = "PagoMpService no está disponible en el backend. "
            + "Si necesitás invocar Mercado Pago usá el servicio expuesto en el módulo cliente.";

    public MercadoPagoPreferenceResponse generarPreferenciaPorFactura(String facturaId) throws ErrorServiceException {
        throw new ErrorServiceException(MENSAJE_NO_DISPONIBLE);
    }

    public MercadoPagoPreferenceResponse generarPreferenciaPorAlquiler(String vehiculoId, int dias) throws ErrorServiceException {
        throw new ErrorServiceException(MENSAJE_NO_DISPONIBLE);
    }
}
