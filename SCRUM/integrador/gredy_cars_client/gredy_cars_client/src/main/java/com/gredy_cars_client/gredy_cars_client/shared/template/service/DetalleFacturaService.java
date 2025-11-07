package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.DetalleFacturaDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.DetalleFacturaDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio cliente para los detalles de factura.
 */
@Service
public class DetalleFacturaService extends BaseClientService<DetalleFacturaDTO, String> {

    private final DetalleFacturaDao detalleFacturaDao;

    public DetalleFacturaService(DetalleFacturaDao dao) {
        super(dao);
        this.detalleFacturaDao = dao;
    }

    public List<DetalleFacturaDTO> listar() throws ErrorServiceException {
        return detalleFacturaDao.findAll();
    }

    public Optional<DetalleFacturaDTO> buscar(String id) throws ErrorServiceException {
        return detalleFacturaDao.findById(id);
    }

    public DetalleFacturaDTO crear(DetalleFacturaDTO dto) throws ErrorServiceException {
        return super.alta(dto);
    }

    public Optional<DetalleFacturaDTO> modificar(String id, DetalleFacturaDTO dto) throws ErrorServiceException {
        return super.modificar(id, dto);
    }

    public void eliminar(String id) throws ErrorServiceException {
        super.baja(id);
    }

    public List<DetalleFacturaDTO> listarPorFactura(String facturaId) throws ErrorServiceException {
        return detalleFacturaDao.findByFactura(facturaId);
    }
}
