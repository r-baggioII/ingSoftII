package com.uncuyo.greedy_cars.shared.template.controller;

import com.uncuyo.greedy_cars.shared.template.dto.ClienteDTO;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
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

    @Autowired
    public ClienteController(ClienteService clienteService) {
        super(clienteService);
        this.clienteService = clienteService;
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

    @PostMapping("/dto")
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

    @PutMapping("/dto/{id}")
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

}
