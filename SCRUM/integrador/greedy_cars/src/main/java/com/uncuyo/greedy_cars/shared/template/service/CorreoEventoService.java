package com.uncuyo.greedy_cars.shared.template.service;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.repository.AlquilerRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CorreoEventoService {

    private final AlquilerRepository alquilerRepository;
    private final ClienteRepository clienteRepository;
    private final NotificacionCorreoService notificacionCorreoService;

    public CorreoEventoService(AlquilerRepository alquilerRepository,
                               ClienteRepository clienteRepository,
                               NotificacionCorreoService notificacionCorreoService) {
        this.alquilerRepository = alquilerRepository;
        this.clienteRepository = clienteRepository;
        this.notificacionCorreoService = notificacionCorreoService;
    }

    public void enviarRecordatorioDevolucion(String alquilerId, String empresaId) throws ErrorServiceException {
        if (alquilerId == null || alquilerId.isBlank()) {
            throw new ErrorServiceException("Debe indicar el alquiler");
        }
        Alquiler alquiler = alquilerRepository.findByIdAndEliminadoIsFalse(alquilerId)
                .orElseThrow(() -> new ErrorServiceException("Alquiler no encontrado"));
        notificacionCorreoService.enviarRecordatorioDevolucion(alquiler, empresaId);
    }

    public void enviarPromocion(String clienteId, String codigo, Integer porcentaje, String empresaId)
            throws ErrorServiceException {
        if (clienteId == null || clienteId.isBlank()) {
            throw new ErrorServiceException("Debe indicar el cliente");
        }
        Cliente cliente = clienteRepository.findByIdAndEliminadoIsFalse(clienteId)
                .orElseThrow(() -> new ErrorServiceException("Cliente no encontrado"));
        notificacionCorreoService.enviarPromocion(cliente, codigo, porcentaje, empresaId);
    }

    public void enviarBienvenida(String correoDestino, String nombre, String empresaId) {
        notificacionCorreoService.enviarCorreoBienvenida(correoDestino, nombre, empresaId);
    }
}
