package com.is.biblioteca.business.logic.service;

import com.is.biblioteca.business.domain.entity.Imagen;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImagenService {
    
    // NO usar @Transactional aquí porque solo crea objetos, no guarda en BD
    // La transacción se maneja en el servicio que lo llama (UsuarioService)
    public Imagen crearImagen(MultipartFile archivo) throws ErrorServiceException {
        try {
            if (archivo != null && !archivo.isEmpty()) {
                Imagen imagen = new Imagen();
                imagen.setId(UUID.randomUUID().toString());
                imagen.setNombre(archivo.getOriginalFilename());
                imagen.setMime(archivo.getContentType());
                imagen.setContenido(archivo.getBytes());
                return imagen;
            }
            return null;
        } catch (IOException e) {
            throw new ErrorServiceException("Error al procesar la imagen");
        }
    }
    
    // NO usar @Transactional aquí porque solo crea objetos, no guarda en BD
    public Imagen modificarImagen(String idImagen, MultipartFile archivo) throws ErrorServiceException {
        try {
            if (archivo != null && !archivo.isEmpty()) {
                Imagen imagen = new Imagen();
                if (idImagen != null) {
                    imagen.setId(idImagen);
                } else {
                    imagen.setId(UUID.randomUUID().toString());
                }
                imagen.setNombre(archivo.getOriginalFilename());
                imagen.setMime(archivo.getContentType());
                imagen.setContenido(archivo.getBytes());
                return imagen;
            }
            return null;
        } catch (IOException e) {
            throw new ErrorServiceException("Error al procesar la imagen");
        }
    }
}
