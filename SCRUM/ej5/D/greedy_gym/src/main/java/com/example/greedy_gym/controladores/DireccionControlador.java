package com.example.greedy_gym.controladores;

import com.example.greedy_gym.entidades.Direccion;
import com.example.greedy_gym.entidades.Pais;
import com.example.greedy_gym.entidades.Provincia;
import com.example.greedy_gym.entidades.Departamento;
import com.example.greedy_gym.entidades.Localidad;
import com.example.greedy_gym.servicios.DireccionServicio;
import com.example.greedy_gym.servicios.PaisServicio;
import com.example.greedy_gym.servicios.ProvinciaServicio;
import com.example.greedy_gym.servicios.DepartamentoServicio;
import com.example.greedy_gym.servicios.LocalidadServicio;
import com.example.greedy_gym.repositorios.DireccionRepositorio;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionControlador {

    private final DireccionServicio direccionServicio;
    private final DireccionRepositorio direccionRepositorio;
    private final PaisServicio paisServicio;
    private final ProvinciaServicio provinciaServicio;
    private final DepartamentoServicio departamentoServicio;
    private final LocalidadServicio localidadServicio;

    @Autowired
    public DireccionControlador(DireccionServicio direccionServicio,
                               DireccionRepositorio direccionRepositorio,
                               PaisServicio paisServicio,
                               ProvinciaServicio provinciaServicio,
                               DepartamentoServicio departamentoServicio,
                               LocalidadServicio localidadServicio) {
        this.direccionServicio = direccionServicio;
        this.direccionRepositorio = direccionRepositorio;
        this.paisServicio = paisServicio;
        this.provinciaServicio = provinciaServicio;
        this.departamentoServicio = departamentoServicio;
        this.localidadServicio = localidadServicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Direccion crear(@RequestBody DireccionRequest request) {
        return direccionServicio.crearDireccion(
            request.getCalle(),
            request.getNumeracion(),
            request.getBarrio(),
            request.getManzanaPiso(),
            request.getCasaDepartamento(),
            request.getReferencia(),
            request.getIdLocalidad()
        );
    }

    @PostMapping("/con-nombres")
    @ResponseStatus(HttpStatus.CREATED)
    public Direccion crearConNombres(@RequestBody DireccionConNombresRequest request) {
        // Validar campos básicos
        if (request.getCalle() == null || request.getCalle().trim().isEmpty()) {
            throw new IllegalArgumentException("La calle es obligatoria");
        }
        if (request.getNumeracion() == null || request.getNumeracion().trim().isEmpty()) {
            throw new IllegalArgumentException("La numeración es obligatoria");
        }
        
        // Buscar o crear país
        Pais pais = paisServicio.buscarPorNombre(request.getNombrePais());
        if (pais == null) {
            pais = paisServicio.crearPais(request.getNombrePais());
        }

        // Buscar o crear provincia
        Provincia provincia = provinciaServicio.buscarPorNombreYPais(request.getNombreProvincia(), pais.getId());
        if (provincia == null) {
            provincia = provinciaServicio.crearProvincia(request.getNombreProvincia(), pais.getId());
        }

        // Buscar o crear departamento
        Departamento departamento = departamentoServicio.buscarPorNombreYProvincia(request.getNombreDepartamento(), provincia.getId());
        if (departamento == null) {
            departamento = departamentoServicio.crearDepartamento(request.getNombreDepartamento(), provincia.getId());
        }

        // Buscar o crear localidad
        Localidad localidad = localidadServicio.buscarPorNombreYDepartamento(request.getNombreLocalidad(), departamento.getId());
        if (localidad == null) {
            localidad = localidadServicio.crearLocalidad(
                request.getNombreLocalidad(),
                request.getCodigoPostal(),
                departamento.getId()
            );
        }

        // Crear la dirección usando solo DireccionServicio
        return direccionServicio.crearDireccion(
            request.getCalle(),
            request.getNumeracion(),
            request.getBarrio(),
            request.getManzanaPiso(),
            request.getCasaDepartamento(),
            request.getReferencia(),
            localidad.getId()
        );
    }

    @GetMapping("/{id}")
    public Direccion obtener(@PathVariable String id) {
        return direccionServicio.buscarDireccion(id);
    }

    @GetMapping("/buscar")
    public Direccion buscarPorCalleNumeracion(
            @RequestParam String calle, 
            @RequestParam String numeracion) {
        return direccionServicio.buscarDireccionPorCalleNumeracion(calle, numeracion);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificar(@PathVariable String id, @RequestBody DireccionRequest request) {
        direccionServicio.modificarDireccion(
            id,
            request.getCalle(),
            request.getNumeracion(),
            request.getBarrio(),
            request.getManzanaPiso(),
            request.getCasaDepartamento(),
            request.getReferencia(),
            request.getIdLocalidad()
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        direccionServicio.eliminarDireccion(id);
    }

    @GetMapping
    public List<Direccion> listar(@RequestParam(required = false) String filtro) {
        if (filtro == null || "activos".equals(filtro)) {
            return direccionRepositorio.findAll().stream()
                    .filter(d -> !d.isEliminado())
                    .toList();
        }
        return direccionRepositorio.findAll();
    }

    // ===============================
    // ENDPOINTS PARA PAISES
    // ===============================
    @PostMapping("/paises")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearPais(@RequestBody Pais body) {
        paisServicio.crearPais(body.getNombre());
    }

    @GetMapping("/paises")
    public List<Pais> listarPaises(@RequestParam(required = false) String filtro,
                                   @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return paisServicio.listarPais().stream()
                    .filter(p -> !p.isEliminado())
                    .filter(p -> p.getNombre().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
        if (filtro == null || "activos".equals(filtro)) {
            return paisServicio.listarPaisActivo();
        }
        return paisServicio.listarPais();
    }

    @GetMapping("/paises/{id}")
    public Pais obtenerPais(@PathVariable String id) {
        return paisServicio.buscarPais(id);
    }

    @PutMapping("/paises/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificarPais(@PathVariable String id, @RequestParam String nombre) {
        paisServicio.modificarPais(id, nombre);
    }

    @DeleteMapping("/paises/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarPais(@PathVariable String id) {
        paisServicio.eliminarPais(id);
    }

    // ===============================
    // ENDPOINTS PARA PROVINCIAS
    // ===============================
    @PostMapping("/provincias")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearProvincia(@RequestBody ProvinciaRequest request) {
        provinciaServicio.crearProvincia(request.getNombre(), request.getIdPais());
    }

    @GetMapping("/provincias")
    public List<Provincia> listarProvincias(@RequestParam(required = false) String paisId,
                                           @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return provinciaServicio.listarTodasLasProvincias().stream()
                    .filter(p -> !p.isEliminado())
                    .filter(p -> p.getNombre().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
        if (paisId != null && !paisId.isEmpty()) {
            return provinciaServicio.listarProvicniaActiva(paisId);
        }
        return provinciaServicio.listarTodasLasProvincias().stream()
                .filter(p -> !p.isEliminado())
                .toList();
    }

    @GetMapping("/provincias/{id}")
    public Provincia obtenerProvincia(@PathVariable String id) {
        return provinciaServicio.buscarProvincia(id);
    }

    @PutMapping("/provincias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificarProvincia(@PathVariable String id, @RequestBody ProvinciaRequest request) {
        provinciaServicio.modificarProvincia(id, request.getNombre(), request.getIdPais());
    }

    @DeleteMapping("/provincias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProvincia(@PathVariable String id) {
        provinciaServicio.eliminarProvincia(id);
    }

    // ===============================
    // ENDPOINTS PARA DEPARTAMENTOS
    // ===============================
    @PostMapping("/departamentos")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearDepartamento(@RequestBody DepartamentoRequest request) {
        departamentoServicio.crearDepartamento(request.getNombre(), request.getIdProvincia());
    }

    @GetMapping("/departamentos")
    public List<Departamento> listarDepartamentos(@RequestParam(required = false) String provinciaId,
                                                  @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return departamentoServicio.listarTodosLosDepartamentos().stream()
                    .filter(d -> !d.isEliminado())
                    .filter(d -> d.getNombre().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
        if (provinciaId != null && !provinciaId.isEmpty()) {
            return departamentoServicio.listarDepartamentoActivo(provinciaId);
        }
        return departamentoServicio.listarTodosLosDepartamentos().stream()
                .filter(d -> !d.isEliminado())
                .toList();
    }

    @GetMapping("/departamentos/{id}")
    public Departamento obtenerDepartamento(@PathVariable String id) {
        return departamentoServicio.buscarDepartamento(id);
    }

    @PutMapping("/departamentos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificarDepartamento(@PathVariable String id, @RequestBody DepartamentoRequest request) {
        departamentoServicio.modificarDepartamento(id, request.getNombre(), request.getIdProvincia());
    }

    @DeleteMapping("/departamentos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarDepartamento(@PathVariable String id) {
        departamentoServicio.eliminarDepartamento(id);
    }

    // ===============================
    // ENDPOINTS PARA LOCALIDADES
    // ===============================
    @PostMapping("/localidades")
    @ResponseStatus(HttpStatus.CREATED)
    public void crearLocalidad(@RequestBody LocalidadRequest request) {
        localidadServicio.crearLocalidad(request.getNombre(), request.getCodigoPostal(), request.getIdDepartamento());
    }

    @GetMapping("/localidades")
    public List<Localidad> listarLocalidades(@RequestParam(required = false) String departamentoId,
                                             @RequestParam(required = false) String search) {
        if (search != null && !search.trim().isEmpty()) {
            return localidadServicio.listarTodasLasLocalidades().stream()
                    .filter(l -> !l.isEliminado())
                    .filter(l -> l.getNombre().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }
        if (departamentoId != null && !departamentoId.isEmpty()) {
            return localidadServicio.listarLocalidadActivo(departamentoId);
        }
        return localidadServicio.listarTodasLasLocalidades().stream()
                .filter(l -> !l.isEliminado())
                .toList();
    }

    @GetMapping("/localidades/{id}")
    public Localidad obtenerLocalidad(@PathVariable String id) {
        return localidadServicio.buscarLocalidad(id);
    }

    @PutMapping("/localidades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void modificarLocalidad(@PathVariable String id, @RequestBody LocalidadRequest request) {
        localidadServicio.modificarLocalidad(id, request.getNombre(), request.getCodigoPostal(), request.getIdDepartamento());
    }

    @DeleteMapping("/localidades/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarLocalidad(@PathVariable String id) {
        localidadServicio.eliminarLocalidad(id);
    }

    // ===============================
    // CLASES DE REQUEST
    // ===============================
    
    // Clase para el request de dirección (original)
    public static class DireccionRequest {
        private String calle;
        private String numeracion;
        private String barrio;
        private String manzanaPiso;
        private String casaDepartamento;
        private String referencia;
        private String idLocalidad;

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public String getNumeracion() {
            return numeracion;
        }

        public void setNumeracion(String numeracion) {
            this.numeracion = numeracion;
        }

        public String getBarrio() {
            return barrio;
        }

        public void setBarrio(String barrio) {
            this.barrio = barrio;
        }

        public String getManzanaPiso() {
            return manzanaPiso;
        }

        public void setManzanaPiso(String manzanaPiso) {
            this.manzanaPiso = manzanaPiso;
        }

        public String getCasaDepartamento() {
            return casaDepartamento;
        }

        public void setCasaDepartamento(String casaDepartamento) {
            this.casaDepartamento = casaDepartamento;
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }

        public String getIdLocalidad() {
            return idLocalidad;
        }

        public void setIdLocalidad(String idLocalidad) {
            this.idLocalidad = idLocalidad;
        }
    }

    // Clase para el request de provincia
    public static class ProvinciaRequest {
        private String nombre;
        private String idPais;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getIdPais() {
            return idPais;
        }

        public void setIdPais(String idPais) {
            this.idPais = idPais;
        }
    }

    // Clase para el request de departamento
    public static class DepartamentoRequest {
        private String nombre;
        private String idProvincia;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getIdProvincia() {
            return idProvincia;
        }

        public void setIdProvincia(String idProvincia) {
            this.idProvincia = idProvincia;
        }
    }

    // Clase para el request de localidad
    public static class LocalidadRequest {
        private String nombre;
        private String codigoPostal;
        private String idDepartamento;

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCodigoPostal() {
            return codigoPostal;
        }

        public void setCodigoPostal(String codigoPostal) {
            this.codigoPostal = codigoPostal;
        }

        public String getIdDepartamento() {
            return idDepartamento;
        }

        public void setIdDepartamento(String idDepartamento) {
            this.idDepartamento = idDepartamento;
        }
    }

    // Clase para el request de dirección con nombres de entidades geográficas
    public static class DireccionConNombresRequest {
        private String calle;
        private String numeracion;
        private String barrio;
        private String manzanaPiso;
        private String casaDepartamento;
        private String referencia;
        private String nombrePais;
        private String nombreProvincia;
        private String nombreDepartamento;
        private String nombreLocalidad;
        private String codigoPostal;

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public String getNumeracion() {
            return numeracion;
        }

        public void setNumeracion(String numeracion) {
            this.numeracion = numeracion;
        }

        public String getBarrio() {
            return barrio;
        }

        public void setBarrio(String barrio) {
            this.barrio = barrio;
        }

        public String getManzanaPiso() {
            return manzanaPiso;
        }

        public void setManzanaPiso(String manzanaPiso) {
            this.manzanaPiso = manzanaPiso;
        }

        public String getCasaDepartamento() {
            return casaDepartamento;
        }

        public void setCasaDepartamento(String casaDepartamento) {
            this.casaDepartamento = casaDepartamento;
        }

        public String getReferencia() {
            return referencia;
        }

        public void setReferencia(String referencia) {
            this.referencia = referencia;
        }

        public String getNombrePais() {
            return nombrePais;
        }

        public void setNombrePais(String nombrePais) {
            this.nombrePais = nombrePais;
        }

        public String getNombreProvincia() {
            return nombreProvincia;
        }

        public void setNombreProvincia(String nombreProvincia) {
            this.nombreProvincia = nombreProvincia;
        }

        public String getNombreDepartamento() {
            return nombreDepartamento;
        }

        public void setNombreDepartamento(String nombreDepartamento) {
            this.nombreDepartamento = nombreDepartamento;
        }

        public String getNombreLocalidad() {
            return nombreLocalidad;
        }

        public void setNombreLocalidad(String nombreLocalidad) {
            this.nombreLocalidad = nombreLocalidad;
        }

        public String getCodigoPostal() {
            return codigoPostal;
        }

        public void setCodigoPostal(String codigoPostal) {
            this.codigoPostal = codigoPostal;
        }
    }
}
