package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.EmpresaDAO;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.EmpresaDTO;
import org.springframework.stereotype.Service;

@Service
public class EmpresaService extends BaseClientService<EmpresaDTO, String> {

    public EmpresaService(EmpresaDAO empresaDAO) {
        super(empresaDAO);
    }
}
