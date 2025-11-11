package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.dto.cliente.ClienteCreateDTO;
import com.example.gestionvehiculos.dto.cliente.ClienteResponseDTO;
import com.example.gestionvehiculos.dto.cliente.ClienteUpdateDTO;
import com.example.gestionvehiculos.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para gestionar clientes del sistema")
public class ClienteController {
    
    private final ClienteService clienteService;
    
    @PostMapping
    @Operation(summary = "Registrar nuevo cliente", description = "Crea un nuevo cliente en el sistema")
    public ResponseEntity<ClienteResponseDTO> crearCliente(@Valid @RequestBody ClienteCreateDTO createDTO) {
        ClienteResponseDTO response = clienteService.crear(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }
    
    @GetMapping("/dni/{dni}")
    @Operation(summary = "Obtener cliente por DNI", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ClienteResponseDTO> obtenerClientePorDni(@PathVariable String dni) {
        return ResponseEntity.ok(clienteService.obtenerPorDni(dni));
    }
    
    @GetMapping
    @Operation(summary = "Listar todos los clientes", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ClienteResponseDTO>> obtenerTodosClientes() {
        return ResponseEntity.ok(clienteService.obtenerTodos());
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ClienteResponseDTO> actualizarCliente(
        @PathVariable Long id,
        @Valid @RequestBody ClienteUpdateDTO updateDTO
    ) {
        return ResponseEntity.ok(clienteService.actualizar(id, updateDTO));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
