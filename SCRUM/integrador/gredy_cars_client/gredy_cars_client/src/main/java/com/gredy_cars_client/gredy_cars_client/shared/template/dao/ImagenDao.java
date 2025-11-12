package com.gredy_cars_client.gredy_cars_client.shared.template.dao;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.gredy_cars_client.gredy_cars_client.config.GreedyApiProperties;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ImagenDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoImagen;

/**
 * DAO concreto que consume los endpoints REST de Imagen.
 */
@Repository
public class ImagenDao extends BaseApiDao<ImagenDTO, String> {

    public ImagenDao(RestTemplate restTemplate, GreedyApiProperties properties) {
        super(restTemplate, properties);
    }

    @Override
    protected String getResourcePath() {
        return "/imagenes";
    }

    @Override
    protected Class<ImagenDTO> getEntityClass() {
        return ImagenDTO.class;
    }

    @Override
    protected ParameterizedTypeReference<List<ImagenDTO>> getListTypeReference() {
        return new ParameterizedTypeReference<List<ImagenDTO>>() {};
    }

    /**
     * Obtiene todas las imágenes de un tipo específico
     * @param tipoImagen tipo de imagen (PERSONA o VEHICULO)
     * @return lista de imágenes del tipo especificado
     */
    public List<ImagenDTO> listarPorTipo(TipoImagen tipoImagen) {
        String url = collectionUrl() + "/tipo/" + tipoImagen.name();
        HttpHeaders headers = buildHeaders();
        return restTemplate.exchange(
            url,
            HttpMethod.GET,
            new HttpEntity<>(null, headers),
            getListTypeReference()
        ).getBody();
    }
}

