
package com.is.biblioteca.business.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.is.biblioteca.business.domain.entity.Usuario;

public interface UsuarioRepository extends BaseRepository<Usuario, String> {
    
    @Query ("SELECT u "
          + "  FROM Usuario u "
          + " WHERE u.email = :email "
          + "   AND u.eliminado = false")
    public Usuario buscarUsuarioPorEmail (@Param ("email") String email);
    
    @Query ("SELECT u "
    	  + "  FROM Usuario u "
    	  + " WHERE u.nombre = :nombre"
    	  + "   AND u.eliminado = false")
    public Usuario buscarUsuarioPorNombre (@Param ("nombre") String nombre);
    
    @Query ("SELECT u "
    	  + "  FROM Usuario u "
    	  + " WHERE u.eliminado = false")
    public List<Usuario> listarUsuarioActivo ();
    
    @Query("SELECT u "
         + "  FROM Usuario u "
         + " WHERE u.email = :email "
         + "   AND u.password = :clave"
         + "   AND u.eliminado = false")
    public Usuario buscarUsuarioPorEmailYClave(@Param("email")String email, @Param("clave")String clave);
}
