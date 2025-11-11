package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.AlquilerDTO;
import com.uncuyo.greedy_cars.shared.template.dto.ClienteDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.AlquilerService;
import com.uncuyo.greedy_cars.shared.template.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController extends BaseRestController<Cliente, String> {

    private final ClienteService clienteService;
    private final AlquilerService alquilerService;

    @Autowired
    public ClienteController(ClienteService clienteService, AlquilerService alquilerService) {
        super(clienteService);
        this.clienteService = clienteService;
        this.alquilerService = alquilerService;
        initController(new Cliente());
    }

    @GetMapping
    @Override
    public ResponseEntity<?> listar() {
        try {
            List<ClienteDTO> lista = clienteService.listarActivosDTO();
            return ResponseEntity.ok(lista);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<?> obtener(@PathVariable String id) {
        try {
            ClienteDTO dto = clienteService.obtenerDTO(id)
                    .orElseThrow(() -> new ErrorServiceException("Cliente no encontrado con ID: " + id));
            return ResponseEntity.ok(dto);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new Cliente. 
     * Override to prevent direct entity creation - must use DTO
     */
    @PostMapping
    @Override
    public ResponseEntity<?> crear(@Valid @RequestBody Cliente entity) {
        // Since we can't distinguish between Cliente and ClienteDTO at compile time,
        // we treat all POST requests as DTO format
        try {
            // The incoming JSON with direccionIds, contactoIds, etc will be handled by
            // a custom converter or we redirect to the service that expects a DTO
            return buildErrorResponse("Please use the correct DTO format with relationship IDs", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new Cliente using explicit DTO endpoint
     */
    @PostMapping("/new")
    public ResponseEntity<?> crearDTO(@Valid @RequestBody ClienteDTO dto) {
        try {
            ClienteDTO creado = clienteService.altaDTO(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing Cliente using DTO format (with relationship IDs)
     */
    @PutMapping("/{id}")
    @Override
    public ResponseEntity<?> actualizar(@PathVariable String id, @Valid @RequestBody Cliente entity) {
        return buildErrorResponse("Please use PUT /api/clientes/update/{id} with DTO format", HttpStatus.BAD_REQUEST);
    }

    /**
     * Update an existing Cliente using explicit DTO endpoint
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<?> actualizarDTO(@PathVariable String id, @Valid @RequestBody ClienteDTO dto) {
        try {
            ClienteDTO actualizado = clienteService.modificarDTO(id, dto)
                    .orElseThrow(() -> new ErrorServiceException("Cliente no encontrado con ID: " + id));
            return ResponseEntity.ok(actualizado);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<?> eliminar(@PathVariable String id) {
        try {
            clienteService.baja(id);
            Map<String, String> response = new HashMap<>();
            response.put("mensaje", "Cliente eliminado correctamente");
            return ResponseEntity.ok(response);
        } catch (ErrorServiceException e) {
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return buildErrorResponse("Error de Sistema: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/alquileres")
    public ResponseEntity<?> listarAlquileresCliente(@PathVariable String id) {
        try {
            List<AlquilerDTO> alquileres = alquilerService.listarPorCliente(id);
            return ResponseEntity.ok(alquileres);
        } catch (ErrorServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/alquileres/pendientes-factura")
    public ResponseEntity<?> listarAlquileresPendientesFactura(@PathVariable String id) {
        try {
            List<AlquilerDTO> alquileres = alquilerService.listarPendientesFacturaPorCliente(id);
            return ResponseEntity.ok(alquileres);
        } catch (ErrorServiceException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/usuario/{usuarioId}")
    public ResponseEntity<?> asociarClienteUsuario(@PathVariable String id, @PathVariable String usuarioId) {
        try {
            ClienteDTO cliente = clienteService.asociarClienteUsuario(id, usuarioId);
            return ResponseEntity.ok(cliente);
        } catch (ErrorServiceException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

}
