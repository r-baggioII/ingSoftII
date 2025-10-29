package com.is.biblioteca.business.logic.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.logic.service.LibroService;

@Service
public class LibroRegistroFacade {

    private final LibroService libroService;

    @Autowired
    public LibroRegistroFacade(LibroService libroService) {
        this.libroService = libroService;
    }

    /**
     * Fachada para registrar un libro. Simplifica la interacción del controlador
     * orquestando validaciones, creación de imagen y persistencia.
     */
    public void registrarLibro(MultipartFile archivo,
                               Long isbn,
                               String titulo,
                               Integer ejemplares,
                               String idAutor,
                               String idEditorial) throws ErrorServiceException {
        // Delegamos en el servicio existente, que ya valida y orquesta dependencias.
        libroService.crearLibro(archivo, isbn, titulo, ejemplares, idAutor, idEditorial);
    }
}
