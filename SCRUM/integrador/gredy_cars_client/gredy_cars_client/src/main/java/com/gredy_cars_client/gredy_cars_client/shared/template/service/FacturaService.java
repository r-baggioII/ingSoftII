package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.FacturaDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DetalleFacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.FormaDePagoDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio cliente para operar contra el API de facturas.
 */
@Service
public class FacturaService extends BaseClientService<FacturaDTO, String> {

    private final FacturaDao facturaDao;

    public FacturaService(FacturaDao dao) {
        super(dao);
        this.facturaDao = dao;
    }

    public List<FacturaDTO> listar() throws ErrorServiceException {
        return facturaDao.findAll();
    }

    public Optional<FacturaDTO> buscar(String id) throws ErrorServiceException {
        return facturaDao.findById(id);
    }

    public FacturaDTO crear(FacturaDTO dto) throws ErrorServiceException {
        return super.alta(dto);
    }

    public Optional<FacturaDTO> modificar(String id, FacturaDTO dto) throws ErrorServiceException {
        return super.modificar(id, dto);
    }

    public void eliminar(String id) throws ErrorServiceException {
        super.baja(id);
    }

    public List<FacturaDTO> listarPorEstado(String estado) throws ErrorServiceException {
        return facturaDao.findByEstado(estado);
    }

    public List<DetalleFacturaDTO> listarDetalles(String facturaId) throws ErrorServiceException {
        return facturaDao.findDetallesByFactura(facturaId);
    }

    public List<FormaDePagoDTO> listarFormasPago(String facturaId) throws ErrorServiceException {
        return facturaDao.findFormasPagoByFactura(facturaId);
    }

    public byte[] descargarPdf(String facturaId) throws ErrorServiceException {
        return facturaDao.descargarPdf(facturaId);
    }

    public List<FacturaDTO> listarPorCliente(String clienteId) throws ErrorServiceException {
        return facturaDao.findByCliente(clienteId);
    }
}
