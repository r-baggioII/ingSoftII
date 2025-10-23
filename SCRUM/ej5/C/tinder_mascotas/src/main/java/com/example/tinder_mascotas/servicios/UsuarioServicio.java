package com.example.tinder_mascotas.servicios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.tinder_mascotas.entidades.Foto;
import com.example.tinder_mascotas.entidades.Usuario;
import com.example.tinder_mascotas.enumeraciones.Rol;
import com.example.tinder_mascotas.repositorios.UsuarioRepositorio;

import jakarta.transaction.Transactional;

@Service
public class UsuarioServicio implements UserDetailsService {

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
        usuario.setContrasena(new BCryptPasswordEncoder().encode(contrasena));
        usuario.setAlta(new Date());
        usuario.setRol(Rol.USER);

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
            usuario.setContrasena(new BCryptPasswordEncoder().encode(contrasena));
        }

        String idFoto = null;
        if (usuario.getFoto() != null) {
            idFoto = usuario.getFoto().getId();
        }

        Foto foto = fotoServicio.actualizar(idFoto, archivo);
        usuario.setFoto(foto);

        usuarioRepositorio.save(usuario);

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorCorreo(username);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }

        if (usuario.getBaja() != null) {
            throw new UsernameNotFoundException("Usuario dado de baja: " + username);
        }

        if (usuario.getRol() == null) {
            throw new UsernameNotFoundException("Usuario sin rol asignado: " + username);
        }

        List<GrantedAuthority> permisos = new ArrayList<>();
        GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
        permisos.add(p);
        
        return new User(usuario.getEmail(), usuario.getContrasena(), permisos);
    }

}