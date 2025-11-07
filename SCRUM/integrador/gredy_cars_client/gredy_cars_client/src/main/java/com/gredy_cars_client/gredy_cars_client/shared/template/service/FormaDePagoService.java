package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.FormaDePagoDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FormaDePagoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio cliente para gestionar formas de pago remotas.
 */
@Service
public class FormaDePagoService extends BaseClientService<FormaDePagoDTO, String> {

    private final FormaDePagoDao formaDePagoDao;

    public FormaDePagoService(FormaDePagoDao dao) {
        super(dao);
        this.formaDePagoDao = dao;
    }

    public List<FormaDePagoDTO> listar() throws ErrorServiceException {
        return formaDePagoDao.findAll();
    }

    public Optional<FormaDePagoDTO> buscar(String id) throws ErrorServiceException {
        return formaDePagoDao.findById(id);
    }

    public FormaDePagoDTO crear(FormaDePagoDTO dto) throws ErrorServiceException {
        return super.alta(dto);
    }

    public Optional<FormaDePagoDTO> modificar(String id, FormaDePagoDTO dto) throws ErrorServiceException {
        return super.modificar(id, dto);
    }

    public void eliminar(String id) throws ErrorServiceException {
        super.baja(id);
    }

    public List<FormaDePagoDTO> listarPorFactura(String facturaId) throws ErrorServiceException {
        return formaDePagoDao.findByFactura(facturaId);
    }
}
