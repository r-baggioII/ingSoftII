package com.greedy_cars_institucional.institucional.shared.template.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.greedy_cars_institucional.institucional.shared.template.dao.CostoVehiculoDao;
import com.greedy_cars_institucional.institucional.shared.template.dto.CostoVehiculoDTO;
import com.greedy_cars_institucional.institucional.shared.template.exception.ErrorServiceException;

@Service
public class CostoVehiculoService extends BaseClientService<CostoVehiculoDTO, String> {

    private final CostoVehiculoDao costoVehiculoDao;

    public CostoVehiculoService(CostoVehiculoDao dao) {
        super(dao);
        this.costoVehiculoDao = dao;
    }

    public Optional<CostoVehiculoDTO> buscarCostoVigente(String caracteristicaId) throws ErrorServiceException {
        return costoVehiculoDao.buscarCostoVigente(caracteristicaId);
    }
}
