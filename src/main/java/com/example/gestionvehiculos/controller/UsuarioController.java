package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.dto.usuario.UsuarioCreateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioUpdateDTO;
import com.example.gestionvehiculos.dto.usuario.UsuarioResponseDTO;
import com.example.gestionvehiculos.enums.Rol;
import com.example.gestionvehiculos.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Usuarios", description = "Gestión de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario (solo ADMIN)")
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioCreateDTO usuarioCreateDTO) {
        UsuarioResponseDTO creado = usuarioService.crear(usuarioCreateDTO);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza un usuario existente (no se puede cambiar userName)")
    public ResponseEntity<UsuarioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO) {
        UsuarioResponseDTO actualizado = usuarioService.actualizar(id, usuarioUpdateDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Obtiene un usuario específico por su ID")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorId(@PathVariable Long id) {
        UsuarioResponseDTO usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene la lista de todos los usuarios")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerTodos() {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/username/{userName}")
    @Operation(summary = "Obtener por username", description = "Obtiene un usuario por su nombre de usuario")
    public ResponseEntity<UsuarioResponseDTO> obtenerPorUserName(@PathVariable String userName) {
        UsuarioResponseDTO usuario = usuarioService.obtenerPorUserName(userName);
        return ResponseEntity.ok(usuario);
    }

    @GetMapping("/rol/{rol}")
    @Operation(summary = "Obtener por rol", description = "Obtiene usuarios filtrados por rol")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerPorRol(@PathVariable Rol rol) {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerPorRol(rol);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener por estado", description = "Obtiene usuarios filtrados por estado activo/inactivo")
    public ResponseEntity<List<UsuarioResponseDTO>> obtenerActivos(@RequestParam boolean activo) {
        List<UsuarioResponseDTO> usuarios = usuarioService.obtenerPorActivo(activo);
        return ResponseEntity.ok(usuarios);
    }

    @PostMapping("/autenticar")
    public ResponseEntity<Boolean> autenticar(
            @RequestParam String userName,
            @RequestParam String password) {
        boolean autenticado = usuarioService.autenticar(userName, password);
        return ResponseEntity.ok(autenticado);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Void> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        usuarioService.cambiarEstado(id, activo);
        return ResponseEntity.ok().build();
    }
}
