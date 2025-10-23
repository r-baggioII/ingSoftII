package com.example.greedy_gym.config;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.UsuarioRepositorio;

/**
 * Servicio de UserDetails personalizado que carga usuarios desde UsuarioRepositorio
 * para que Spring Security pueda autenticar usando la base de datos existente.
 */
@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioDetailsService(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.findByNombreUsuarioIgnoreCase(nombreUsuario)
                .filter(u -> !u.isEliminado())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + nombreUsuario));

        // Mapear el rol de Usuario a GrantedAuthority para Spring Security
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );

    // Crear UserDetails con el nombreUsuario y clave
    // IMPORTANTE: Las contraseñas están en texto plano en la BD.
    // Estamos usando NoOpPasswordEncoder en SeguridadWeb, por lo que
    // NO debemos prefijar con {noop}. Si cambiamos a DelegatingPasswordEncoder,
    // entonces sí debería usarse el prefijo correspondiente.
        return User.builder()
                .username(usuario.getNombreUsuario())
        .password(usuario.getClave())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(usuario.isEliminado())
                .build();
    }
}
