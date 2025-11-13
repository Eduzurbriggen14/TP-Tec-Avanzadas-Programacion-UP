package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.dto.revision.RevisionCreateDTO;
import com.example.gestionvehiculos.dto.revision.RevisionResponseDTO;
import com.example.gestionvehiculos.service.RevisionVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/revisiones")
@RequiredArgsConstructor
@Tag(name = "Revisiones Vehiculares", description = "API para gestión de revisiones vehiculares con sistema de puntuación")
@SecurityRequirement(name = "bearerAuth")
public class RevisionVehiculoController {

    private final RevisionVehiculoService revisionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANTENIMIENTO', 'INSPECTOR')")
    @Operation(summary = "Crear revisión con chequeo de 8 items", 
               description = "Crea una revisión con 8 items de chequeo. Calcula automáticamente el puntaje total y determina si el vehículo es SEGURO (>=80 pts) o requiere RECHEQUEO (<40 pts o algún item <5). Si el puntaje es <40, las observaciones son obligatorias.")
    public ResponseEntity<RevisionResponseDTO> crear(
            @Valid @RequestBody RevisionCreateDTO revisionDTO,
            Authentication authentication) {
        String userName = authentication.getName();
        RevisionResponseDTO creada = revisionService.crear(revisionDTO, userName);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar revisión (solo ADMIN)")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        revisionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener revisión por ID")
    public ResponseEntity<RevisionResponseDTO> obtenerPorId(@PathVariable Long id) {
        RevisionResponseDTO revision = revisionService.obtenerPorId(id);
        return ResponseEntity.ok(revision);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    @Operation(summary = "Obtener todas las revisiones")
    public ResponseEntity<List<RevisionResponseDTO>> obtenerTodos() {
        List<RevisionResponseDTO> revisiones = revisionService.obtenerTodos();
        return ResponseEntity.ok(revisiones);
    }

    @GetMapping("/patente/{patente}")
    @Operation(summary = "Obtener revisiones por patente del vehículo")
    public ResponseEntity<List<RevisionResponseDTO>> obtenerPorPatente(@PathVariable String patente) {
        List<RevisionResponseDTO> revisiones = revisionService.obtenerPorPatente(patente);
        return ResponseEntity.ok(revisiones);
    }

    @GetMapping("/rango-fechas")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSPECTOR')")
    @Operation(summary = "Obtener revisiones por rango de fechas")
    public ResponseEntity<List<RevisionResponseDTO>> obtenerPorRangoFechas(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        List<RevisionResponseDTO> revisiones = revisionService.obtenerPorRangoFechas(inicio, fin);
        return ResponseEntity.ok(revisiones);
    }
}
