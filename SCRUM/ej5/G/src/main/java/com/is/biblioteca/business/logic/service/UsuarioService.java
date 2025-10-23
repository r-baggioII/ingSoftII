package com.is.biblioteca.business.logic.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.is.biblioteca.business.domain.entity.Imagen;
import com.is.biblioteca.business.domain.entity.Usuario;
import com.is.biblioteca.business.domain.enumeration.Rol;
import com.is.biblioteca.business.logic.error.ErrorServiceException;
import com.is.biblioteca.business.persistence.repository.UsuarioRepository;

import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpSession;

@Service
public class UsuarioService extends BaseService<Usuario, String> implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final ImagenService imagenService;
    private final PasswordEncoder encoder;

    /**
     * Constructor injection - recommended over field injection.
     */
    @Autowired
    public UsuarioService(UsuarioRepository repository, ImagenService imagenService, PasswordEncoder encoder) {
        super(repository);
        this.usuarioRepository = repository;
        this.imagenService = imagenService;
        this.encoder = encoder;
    }

    public void validar(String nombre, String email, String clave, String confirmacion) throws ErrorServiceException {
        try {

            if (nombre == null || nombre.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }

            if (email == null || email.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el Email");
            }

            if (clave == null || clave.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar la clave");
            }

            if (confirmacion == null || confirmacion.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar la confirmación de clave");
            }

            if (!clave.trim().equals(confirmacion.trim())) {
                throw new ErrorServiceException("La clave debe ser igual a su confirmación");
            }

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public Usuario crearUsuario(String nombre, String email, String clave, String confirmacion, MultipartFile archivo)
            throws ErrorServiceException {

        try {

            validar(nombre, email, clave, confirmacion);

            Usuario usuario = new Usuario();
            usuario.setId(UUID.randomUUID().toString());
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setRol(Rol.USER);
            usuario.setPassword(encoder.encode(clave));
            usuario.setEliminado(false);

            if (archivo != null) {
                Imagen imagen = imagenService.crearImagen(archivo);
                usuario.setImagen(imagen);
            }

            return usuarioRepository.save(usuario);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public Usuario modificarUsuario(String idUsuario, String nombre, String email, String clave, String confirmacion,
            MultipartFile archivo) throws ErrorServiceException {

        try {

            validar(nombre, email, clave, confirmacion);

            Usuario usuario = buscarUsuario(idUsuario);
            usuario.setNombre(nombre);
            usuario.setEmail(email);
            usuario.setRol(Rol.USER);
            usuario.setPassword(encoder.encode(clave));

            String idImagen = null;
            if (usuario.getImagen() != null) {
                idImagen = usuario.getImagen().getId();
            }

            Imagen imagen = imagenService.modificarImagen(idImagen, archivo);
            usuario.setImagen(imagen);

            return usuarioRepository.save(usuario);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

    @Transactional
    public void eliminarUsuario(String idUsuario) throws ErrorServiceException {

        try {

            Usuario usuario = buscarUsuario(idUsuario);
            usuario.setEliminado(true);

            usuarioRepository.save(usuario);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }

    }

    @Transactional
    public void cambiarRol(String idUsuario) throws ErrorServiceException {

        try {

            Usuario usuario = buscarUsuario(idUsuario);

            if (usuario.getRol() == Rol.ADMIN)
                usuario.setRol(Rol.USER);
            else
                usuario.setRol(Rol.ADMIN);

            usuarioRepository.save(usuario);

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }

    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        System.out.println("========================================");
        System.out.println("🔍 loadUserByUsername llamado");
        System.out.println("📧 Email recibido: [" + email + "]");

        Usuario usuario = usuarioRepository.buscarUsuarioPorEmail(email);

        System.out.println("👤 Usuario encontrado: " + (usuario != null ? "SÍ" : "NO"));
        if (usuario != null) {
            System.out.println("   - ID: " + usuario.getId());
            System.out.println("   - Nombre: " + usuario.getNombre());
            System.out.println("   - Email: " + usuario.getEmail());
            System.out.println("   - Rol: " + usuario.getRol());
            System.out.println("   - Eliminado: " + usuario.getEliminado());
            System.out.println("   - Password (primeros 20 chars): " + (usuario.getPassword() != null
                    ? usuario.getPassword().substring(0, Math.min(20, usuario.getPassword().length()))
                    : "NULL"));
        }

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList<>();

            GrantedAuthority permiso = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(permiso);

            System.out.println("✅ Permisos asignados: " + permisos);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);

            System.out.println("✅ Usuario guardado en sesión");
            System.out.println("========================================");

            return new User(usuario.getEmail(), usuario.getPassword(), permisos);

        } else {
            System.out.println("❌ No se encontró usuario con email: " + email);
            System.out.println("========================================");
            throw new UsernameNotFoundException("No se encontró un usuario con el email: " + email);
        }

    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(String idUsuario) throws ErrorServiceException {

        try {

            if (idUsuario == null || idUsuario.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el usuario");
            }

            Optional<Usuario> optional = usuarioRepository.findById(idUsuario);
            Usuario usuario = null;
            if (optional.isPresent()) {
                usuario = optional.get();
                if (usuario == null || usuario.getEliminado()) {
                    throw new ErrorServiceException("No se encuentra el usuario indicado");
                }
            }

            return usuario;

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }

    }

    public Usuario buscarUsuarioPorEmail(String email) throws ErrorServiceException {

        try {

            if (email == null || email.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el email");
            }

            return usuarioRepository.buscarUsuarioPorEmail(email);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    public Usuario buscarUsuarioPorNombre(String nombre) throws ErrorServiceException {

        try {

            if (nombre == null || nombre.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el nombre");
            }

            return usuarioRepository.buscarUsuarioPorNombre(nombre);

        } catch (ErrorServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ErrorServiceException("Error de sistema");
        }
    }

    public List<Usuario> listarUsuario() throws ErrorServiceException {

        try {

            return usuarioRepository.findAll();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }

    }

    public Usuario login(String email, String clave) throws ErrorServiceException {

        try {

            if (email == null || email.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar el usuario");
            }

            if (clave == null || clave.trim().isEmpty()) {
                throw new ErrorServiceException("Debe indicar la clave");
            }

            Usuario usuario = null;
            try {
                usuario = usuarioRepository.buscarUsuarioPorEmailYClave(email, clave);
            } catch (NoResultException ex) {
                throw new ErrorServiceException("No existe usuario para el correo y clave indicado");
            }

            return usuario;

        } catch (ErrorServiceException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ErrorServiceException("Error de Sistemas");
        }
    }

}
