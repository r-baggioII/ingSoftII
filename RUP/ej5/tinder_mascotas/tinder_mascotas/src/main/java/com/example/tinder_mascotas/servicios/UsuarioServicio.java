package com.example.tinder_mascotas.servicios;

import com.example.tinder_mascotas.entidades.Foto;
import com.example.tinder_mascotas.entidades.Usuario;
import com.example.tinder_mascotas.repositorios.UsuarioRepositorio;

import jakarta.transaction.Transactional;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UsuarioServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private FotoServicio fotoServicio;

    @Autowired
    private NotificacionServicio notificacionServicio;

    @Transactional
    public void registrar(MultipartFile archivo, String nombre, String apellido, String email, String contrasena) {

        if(nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }

        if(apellido == null || apellido.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }

        if(email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if(contrasena == null || contrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        Usuario usuario = new Usuario();

        usuario.setNombre(nombre);
        usuario.setApellido(apellido);
        usuario.setEmail(email);
        usuario.setContrasena(contrasena);
        usuario.setAlta(new Date());

        Foto foto = fotoServicio.guardar(archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);

        notificacionServicio.enviar("Bienvenido a Tinder de Mascotas", "Tinder de mascotas", usuario.getEmail());

    }

    @Transactional
    public void modificar(MultipartFile archivo, String id, String nombre, String apellido, String email, String contrasena) {


        Optional<Usuario> optionalUsuario = usuarioRepositorio.findById(id);
        Usuario usuario = optionalUsuario.get();

        if(nombre != null && !nombre.trim().isEmpty()) {
            usuario.setNombre(nombre);
        }

        if(apellido != null && !apellido.trim().isEmpty()) {
            usuario.setApellido(apellido);
        }

        if(email != null && !email.trim().isEmpty()) {
            usuario.setEmail(email);
        }

        if(contrasena != null && !contrasena.trim().isEmpty()) {
            usuario.setContrasena(contrasena);
        }

        String idFoto = null;
        if (usuario.getFoto() != null) {
            idFoto = usuario.getFoto().getId();
        }

        Foto foto = fotoServicio.actualizar(idFoto, archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);

    }

}