package com.example.tinder_mascotas.servicios;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.tinder_mascotas.entidades.Foto;
import com.example.tinder_mascotas.repositorios.FotoRepositorio;

import jakarta.transaction.Transactional;

@Service
public class FotoServicio {

    @Autowired
    private FotoRepositorio fotoRepositorio;

    @Transactional
    public Foto guardar(MultipartFile archivo) {
        if (archivo != null && !archivo.isEmpty()) {
            try {

                Foto foto = new Foto();
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                return fotoRepositorio.save(foto);

            } catch (Exception e) {
                System.err.println(e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    @Transactional
    public Foto actualizar(String idFoto, MultipartFile archivo) {
        if (archivo != null && !archivo.isEmpty()) {
            try {
                Foto foto = new Foto();
                if (idFoto != null) {
                    Optional<Foto> respuesta = fotoRepositorio.findById(idFoto);
                    if (respuesta.isPresent()) {
                        foto = respuesta.get();
                    }
                }
                
                foto.setMime(archivo.getContentType());
                foto.setNombre(archivo.getName());
                foto.setContenido(archivo.getBytes());
                
                return fotoRepositorio.save(foto);
                
            } catch (Exception e) {
                System.err.println(e.getMessage());
                return null;
            }
        }
        return null;
    }

}
