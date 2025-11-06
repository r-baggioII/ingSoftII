package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.UsuarioDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * DAO para Usuario que realiza llamadas REST al backend.
 * Copia automáticamente la cookie JWT del request actual para autenticar las peticiones.
 */
@Repository
public class UsuarioApiDao extends BaseApiDao<UsuarioDTO, String> {

    private static final Logger log = LoggerFactory.getLogger(UsuarioApiDao.class);

    @Autowired
    public UsuarioApiDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/usuarios";
    }

    @Override
    protected Class<UsuarioDTO> getEntityClass() {
        return UsuarioDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<UsuarioDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<UsuarioDTO>>() {};
    }

    @Override
    protected HttpHeaders buildHeaders() {
        HttpHeaders headers = super.buildHeaders();
        
        // Copiar la cookie JWT del request actual
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String cookieHeader = request.getHeader("Cookie");
            if (cookieHeader != null) {
                headers.add(HttpHeaders.COOKIE, cookieHeader);
                log.debug("Cookie JWT reenviada al backend");
            }
        }
        
        return headers;
    }
}
