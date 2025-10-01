package com.example.tinder_mascotas.servicios;
import com.example.tinder_mascotas.entidades.Foto;
import com.example.tinder_mascotas.entidades.Mascota;
import com.example.tinder_mascotas.entidades.Usuario;
import com.example.tinder_mascotas.repositorios.UsuarioRepositorio;

import jakarta.transaction.Transactional;

import com.example.tinder_mascotas.repositorios.MascotaRepositorio;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.enumeraciones.*;

@Service
public class MascotaServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private MascotaRepositorio mascotaRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

    @Transactional
    public void agregarMascota(MultipartFile archivo, String idUsuario, String nombre, Sexo sexo){
        
        Usuario usuario = usuarioRepositorio.findById(idUsuario).get();
        validar(nombre,sexo);

        Mascota nuevaMascota = new Mascota();
        nuevaMascota.setNombre(nombre);
        nuevaMascota.setSexo(sexo);
        nuevaMascota.setAlta(new Date());
        nuevaMascota.setUsuario(usuario);

        Foto foto = fotoServicio.guardar(archivo);
        nuevaMascota.setFoto(foto);

        mascotaRepositorio.save(nuevaMascota);

    }

    @Transactional
    public void modificar(MultipartFile archivo, String idUsuario, String idMascota, String nombre, Sexo sexo){

        validar(nombre, sexo);

        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);

        if (respuesta.isPresent()){
            Mascota mascota = respuesta.get();
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setNombre(nombre);
                mascota.setSexo(sexo);

                String idFoto = null;
                if (mascota.getFoto() != null) {
                    idFoto = mascota.getFoto().getId();
                }

                Foto foto = fotoServicio.actualizar(idFoto, archivo);
                mascota.setFoto(foto);

                mascotaRepositorio.save(mascota);
            } else {
                throw new IllegalArgumentException("No tienes permiso para modificar esta mascota");
            }

        } else {
            throw new IllegalArgumentException("La mascota no existe");
        }

    }

    public void validar(String nombre, Sexo sexo){
        if(nombre == null || nombre.trim().isEmpty()){
            throw new IllegalArgumentException("El nombre de la mascota no puede estar vacío");
        }
        if(sexo == null){
            throw new IllegalArgumentException("El sexo de la mascota no puede estar vacío");
        }
    }

    @Transactional
    public void eliminar(String idUsuario, String idMascota){

        Optional<Mascota> respuesta = mascotaRepositorio.findById(idMascota);

        if (respuesta.isPresent()){
            Mascota mascota = respuesta.get();
            if (mascota.getUsuario().getId().equals(idUsuario)) {
                mascota.setBaja(new Date());
                mascotaRepositorio.save(mascota);
            } else {
                throw new IllegalArgumentException("No tienes permiso para eliminar esta mascota");
            }

        } else {
            throw new IllegalArgumentException("La mascota no existe");
        }

    }

}
