package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Empleado;
import com.example.greedy_gym.entidades.RolUsuario;
import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.entidades.TipoDocumento;
import com.example.greedy_gym.entidades.TipoEmpleado;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.servicios.UsuarioServicio;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador {

    private final UsuarioServicio service;

    public UsuarioControlador(UsuarioServicio service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crear(@RequestBody CrearUsuarioRequest body) {
        return service.crearUsuario(body.toCommand());
    }

    @GetMapping("/{id}")
    public Usuario obtener(@PathVariable String id) {
        return service.buscarUsuario(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestBody Usuario body) {
        service.modificarUsuario(id, body.getNombreUsuario(), body.getClave(), body.getRol());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        service.eliminarUsuario(id);
    }

    @GetMapping
    public List<Usuario> listar() {
        return service.listarUsuarios();
    }

    @GetMapping("/activos")
    public List<Usuario> listarActivos() {
        return service.listarUsuariosActivos();
    }

    @GetMapping("/buscar/{nombre}")
    public Usuario buscarPorNombre(@PathVariable String nombre) {
        return service.buscarUsuarioPorNombre(nombre);
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest request) {
        return service.login(request.nombreUsuario, request.clave);
    }

    @PutMapping("/{id}/clave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificarClave(@PathVariable String id, @RequestBody ModificarClaveRequest request) {
        service.modificarClave(id, request.claveActual, request.nuevaClave, request.confirmarClave);
    }

    public static class LoginRequest {
        public String nombreUsuario;
        public String clave;
    }

    public static class ModificarClaveRequest {
        public String claveActual;
        public String nuevaClave;
        public String confirmarClave;
    }

    public static class CrearUsuarioRequest {

        private String nombreUsuario;
        private String clave;
        private RolUsuario rol;
        private String socioId;
        private SocioPayload socio;
        private String empleadoId;
        private EmpleadoPayload empleado;

        public UsuarioServicio.CrearUsuarioCommand toCommand() {
            String socioIdLimpio = socioId != null && !socioId.isBlank() ? socioId.trim() : null;
            String empleadoIdLimpio = empleadoId != null && !empleadoId.isBlank() ? empleadoId.trim() : null;
            return new UsuarioServicio.CrearUsuarioCommand(
                    nombreUsuario,
                    clave,
                    rol,
                    socioIdLimpio,
                    socio != null ? socio.toEntity() : null,
                    socio != null ? socio.getDireccionId() : null,
                    socio != null ? socio.getSucursalId() : null,
                    empleadoIdLimpio,
                    empleado != null ? empleado.toEntity() : null,
                    empleado != null ? empleado.getDireccionId() : null,
                    empleado != null ? empleado.getSucursalId() : null
            );
        }

        public String getNombreUsuario() {
            return nombreUsuario;
        }

        public void setNombreUsuario(String nombreUsuario) {
            this.nombreUsuario = nombreUsuario;
        }

        public String getClave() {
            return clave;
        }

        public void setClave(String clave) {
            this.clave = clave;
        }

        public RolUsuario getRol() {
            return rol;
        }

        public void setRol(RolUsuario rol) {
            this.rol = rol;
        }

        public String getSocioId() {
            return socioId;
        }

        public void setSocioId(String socioId) {
            this.socioId = socioId;
        }

        public SocioPayload getSocio() {
            return socio;
        }

        public void setSocio(SocioPayload socio) {
            this.socio = socio;
        }

        public String getEmpleadoId() {
            return empleadoId;
        }

        public void setEmpleadoId(String empleadoId) {
            this.empleadoId = empleadoId;
        }

        public EmpleadoPayload getEmpleado() {
            return empleado;
        }

        public void setEmpleado(EmpleadoPayload empleado) {
            this.empleado = empleado;
        }
    }

    public static class SocioPayload {
        private String nombre;
        private String apellido;
        private LocalDate fechaNacimiento;
        private TipoDocumento tipoDocumento;
        private String numeroDocumento;
        private String telefono;
        private String correoElectronico;
        private Long numeroSocio;
        private String direccionId;
        private String sucursalId;

        public Socio toEntity() {
            Socio socio = new Socio();
            socio.setNombre(nombre != null ? nombre.trim() : null);
            socio.setApellido(apellido != null ? apellido.trim() : null);
            socio.setFechaNacimiento(fechaNacimiento);
            socio.setTipoDocumento(tipoDocumento);
            socio.setNumeroDocumento(numeroDocumento != null ? numeroDocumento.trim() : null);
            socio.setTelefono(telefono != null ? telefono.trim() : null);
            socio.setCorreoElectronico(correoElectronico != null ? correoElectronico.trim() : null);
            socio.setNumeroSocio(numeroSocio);
            return socio;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public LocalDate getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public TipoDocumento getTipoDocumento() {
            return tipoDocumento;
        }

        public void setTipoDocumento(TipoDocumento tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
        }

        public String getNumeroDocumento() {
            return numeroDocumento;
        }

        public void setNumeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getCorreoElectronico() {
            return correoElectronico;
        }

        public void setCorreoElectronico(String correoElectronico) {
            this.correoElectronico = correoElectronico;
        }

        public Long getNumeroSocio() {
            return numeroSocio;
        }

        public void setNumeroSocio(Long numeroSocio) {
            this.numeroSocio = numeroSocio;
        }

        public String getDireccionId() {
            return direccionId;
        }

        public void setDireccionId(String direccionId) {
            this.direccionId = direccionId;
        }

        public String getSucursalId() {
            return sucursalId;
        }

        public void setSucursalId(String sucursalId) {
            this.sucursalId = sucursalId;
        }
    }

    public static class EmpleadoPayload {
        private String nombre;
        private String apellido;
        private LocalDate fechaNacimiento;
        private TipoDocumento tipoDocumento;
        private String numeroDocumento;
        private String telefono;
        private String correoElectronico;
        private TipoEmpleado tipoEmpleado;
        private String direccionId;
        private String sucursalId;

        public Empleado toEntity() {
            Empleado empleado = new Empleado();
            empleado.setNombre(nombre != null ? nombre.trim() : null);
            empleado.setApellido(apellido != null ? apellido.trim() : null);
            empleado.setFechaNacimiento(fechaNacimiento);
            empleado.setTipoDocumento(tipoDocumento);
            empleado.setNumeroDocumento(numeroDocumento != null ? numeroDocumento.trim() : null);
            empleado.setTelefono(telefono != null ? telefono.trim() : null);
            empleado.setCorreoElectronico(correoElectronico != null ? correoElectronico.trim() : null);
            empleado.setTipoEmpleado(tipoEmpleado);
            return empleado;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellido() {
            return apellido;
        }

        public void setApellido(String apellido) {
            this.apellido = apellido;
        }

        public LocalDate getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(LocalDate fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }

        public TipoDocumento getTipoDocumento() {
            return tipoDocumento;
        }

        public void setTipoDocumento(TipoDocumento tipoDocumento) {
            this.tipoDocumento = tipoDocumento;
        }

        public String getNumeroDocumento() {
            return numeroDocumento;
        }

        public void setNumeroDocumento(String numeroDocumento) {
            this.numeroDocumento = numeroDocumento;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }

        public String getCorreoElectronico() {
            return correoElectronico;
        }

        public void setCorreoElectronico(String correoElectronico) {
            this.correoElectronico = correoElectronico;
        }

        public TipoEmpleado getTipoEmpleado() {
            return tipoEmpleado;
        }

        public void setTipoEmpleado(TipoEmpleado tipoEmpleado) {
            this.tipoEmpleado = tipoEmpleado;
        }

        public String getDireccionId() {
            return direccionId;
        }

        public void setDireccionId(String direccionId) {
            this.direccionId = direccionId;
        }

        public String getSucursalId() {
            return sucursalId;
        }

        public void setSucursalId(String sucursalId) {
            this.sucursalId = sucursalId;
        }
    }
}
