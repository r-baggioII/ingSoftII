package com.example.greedy_gym.servicios;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuario en la base de datos
        Usuario usuario = usuarioRepository.findByNombreUsuarioAndEliminadoIsFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Construir UserDetails de Spring Security
        return User.builder()
                .username(usuario.getNombreUsuario())
                .password(usuario.getClave()) // Ya está encriptada
                .authorities(getAuthorities(usuario))
                .disabled(usuario.isEliminado()) // Considera el soft-delete como deshabilitado
                .build();
    }

    /**
     * Convierte el rol del usuario en autoridades de Spring Security
     */
    private Collection<? extends GrantedAuthority> getAuthorities(Usuario usuario) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );
    }
}