package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.servicios.SocioServicio;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/socios")
@RequiredArgsConstructor
public class SocioControlador {

    private final SocioServicio socioServicio;

    @PostMapping
    public ResponseEntity<Socio> crear(@RequestBody Socio socio) {
        Socio creado = socioServicio.crearSocio(socio);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping
    public List<Socio> listar() {
        return socioServicio.listarSocio();
    }

    @GetMapping("/activos")
    public List<Socio> listarActivos() {
        return socioServicio.listarSocioActivo();
    }

    @GetMapping("/{id}")
    public Socio buscarPorId(@PathVariable String id) {
        return socioServicio.buscarPersona(id);
    }

    @PutMapping("/{id}")
    public Socio actualizar(@PathVariable String id, @RequestBody Socio socio) {
        return socioServicio.mdoificarSocio(id, socio.getNombre(), socio.getApellido(), 
                socio.getFechaNacimiento(), socio.getTipoDocumento(), socio.getNumeroDocumento(), 
                socio.getNumeroSocio());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        socioServicio.eliminarPersona(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/usuario")
    public Socio asociarUsuario(@PathVariable String id, @RequestBody AsociarUsuarioRequest request) {
        String usuarioId = request != null ? request.getUsuarioId() : null;
        return socioServicio.asociarSocioUsuario(id, usuarioId);
    }

    public static class AsociarUsuarioRequest {

        private String usuarioId;

        public String getUsuarioId() {
            return usuarioId;
        }

        public void setUsuarioId(String usuarioId) {
            this.usuarioId = usuarioId;
        }
    }
}
