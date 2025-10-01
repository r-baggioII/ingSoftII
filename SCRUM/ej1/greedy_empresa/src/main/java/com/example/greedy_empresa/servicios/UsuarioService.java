package com.example.greedy_empresa.servicios;

import com.example.greedy_empresa.entidades.Usuario;
import com.example.greedy_empresa.repositorios.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;

    public Page<Usuario> buscar(String filtro, Pageable pageable) {
        if (filtro == null || filtro.isBlank()) {
            return usuarioRepository.findByEliminadoFalse(pageable);
        }
        return usuarioRepository.findByUsernameContainingIgnoreCaseAndEliminadoFalse(filtro.trim(), pageable);
    }

    public Usuario buscarPorId(String id) {
        return usuarioRepository.findById(id)
                .filter(usuario -> !usuario.isEliminado())
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        String usernameNormalizado = usuario.getUsername() != null ? usuario.getUsername().trim() : "";
        if (usernameNormalizado.isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }

        usuarioRepository.findByUsernameIgnoreCaseAndEliminadoFalse(usernameNormalizado)
                .filter(existente -> !existente.getId().equals(usuario.getId()))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un usuario con ese nombre");
                });

        if (usuario.getId() != null && !usuario.getId().isBlank()) {
            Usuario existente = buscarPorId(usuario.getId());
            existente.setUsername(usernameNormalizado);
            existente.setRol(usuario.getRol());
            if (usuario.getPassword() != null && !usuario.getPassword().isBlank()) {
                validarPasswords(usuario.getPassword(), usuario.getConfirmPassword());
                existente.setPasswordHash(passwordService.hash(usuario.getPassword()));
            }
            return existente;
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        validarPasswords(usuario.getPassword(), usuario.getConfirmPassword());
        usuario.setUsername(usernameNormalizado);
        usuario.setPasswordHash(passwordService.hash(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    private void validarPasswords(String password, String confirmPassword) {
        if (confirmPassword == null || confirmPassword.isBlank()) {
            throw new IllegalArgumentException("Debe confirmar la contraseña");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("La contraseña y la confirmación no coinciden");
        }
    }

    @Transactional
    public void eliminar(String id) {
        Usuario usuario = buscarPorId(id);
        usuario.setEliminado(true);
    }

    public long contarActivos() {
        return usuarioRepository.countByEliminadoFalse();
    }
}
