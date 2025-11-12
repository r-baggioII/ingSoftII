package com.gredy_cars_client.gredy_cars_client.shared.template.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.gredy_cars_client.gredy_cars_client.shared.template.dao.ImagenDao;
import com.gredy_cars_client.gredy_cars_client.shared.template.dto.ImagenDTO;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.BaseUseCaseService;
import com.gredy_cars_client.gredy_cars_client.shared.template.enums.TipoImagen;
import com.gredy_cars_client.gredy_cars_client.shared.template.exception.ErrorServiceException;

/**
 * Servicio concreto de Imagen que delega en el DAO remoto.
 */
@Service
public class ImagenService extends BaseClientService<ImagenDTO, String> {

    private final ImagenDao imagenDao;

    public ImagenService(ImagenDao dao) {
        super(dao);
        this.imagenDao = dao;
    }

    @Override
    protected void validar(BaseUseCaseService useCase, ImagenDTO imagen) throws ErrorServiceException {
        if (useCase == BaseUseCaseService.BAJA) {
            return;
        }
        if (imagen == null) {
            throw new ErrorServiceException("Debe indicar la imagen");
        }
    }

    @Override
    protected void preAlta(ImagenDTO dto) throws ErrorServiceException {
        // No se necesita validación especial
    }

    @Override
    protected void preModificacion(String id, ImagenDTO dto) throws ErrorServiceException {
        if (id == null) {
            throw new ErrorServiceException("El id de la imagen es obligatorio para modificar");
        }
        dto.setId(id);
    }

    /**
     * Lista todas las imágenes de un tipo específico
     * @param tipoImagen tipo de imagen (PERSONA o VEHICULO)
     * @return lista de imágenes del tipo especificado
     * @throws ErrorServiceException si ocurre un error
     */
    public List<ImagenDTO> listarPorTipo(TipoImagen tipoImagen) throws ErrorServiceException {
        try {
            return imagenDao.listarPorTipo(tipoImagen);
        } catch (Exception e) {
            throw new ErrorServiceException("Error al listar imágenes por tipo: " + e.getMessage());
        }
    }
}
