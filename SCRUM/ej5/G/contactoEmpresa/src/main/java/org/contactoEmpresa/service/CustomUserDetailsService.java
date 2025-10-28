package org.contactoEmpresa.service;

import org.contactoEmpresa.entity.Usuario;
import org.contactoEmpresa.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
                .disabled(usuario.getEliminado()) // Considera el soft-delete como deshabilitado
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