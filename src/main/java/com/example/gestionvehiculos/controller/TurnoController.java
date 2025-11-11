package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.dto.turno.TurnoResponseDTO;
import com.example.gestionvehiculos.dto.turno.TurnoSolicitudDTO;
import com.example.gestionvehiculos.dto.turno.TurnoSlotStatusDTO;
import com.example.gestionvehiculos.service.TurnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/turnos")
@RequiredArgsConstructor
@Tag(name = "Turnos", description = "API para gestión de turnos de revisión vehicular")
@SecurityRequirement(name = "bearerAuth")
public class TurnoController {
    
    private final TurnoService turnoService;
    
    @Value("${turnos.horario.inicio:09:00:00}")
    private String turnosHorarioInicio;

    @Value("${turnos.horario.fin:17:00:00}")
    private String turnosHorarioFin;

    @Value("${turnos.slot.duracionMinutos:60}")
    private int turnosSlotDuracion;
    
    @PostMapping("/solicitar")
    @Operation(summary = "Solicitar un turno para revisión vehicular")
    public ResponseEntity<TurnoResponseDTO> solicitarTurno(
            @Valid @RequestBody TurnoSolicitudDTO solicitudDTO,
            Authentication authentication) {
        
        String userName = authentication.getName();
        TurnoResponseDTO turno = turnoService.solicitarTurno(solicitudDTO, userName);
        return ResponseEntity.status(HttpStatus.CREATED).body(turno);
    }
    
    @GetMapping("/disponibles")
    @Operation(summary = "Obtener turnos libres (horario 09:00-17:00) Si includeOccupied=false, el turno esta ocupado.")
    public ResponseEntity<List<TurnoSlotStatusDTO>> obtenerSlotsParaFecha(
            @RequestParam("fecha") LocalDate fecha,
            @RequestParam(name = "includeOccupied", required = false, defaultValue = "true") boolean includeOccupied
    ) {
        // Usar horario fijo y duración constante desde configuración
        LocalTime inicioTime = LocalTime.parse(turnosHorarioInicio);
        LocalTime finTime = LocalTime.parse(turnosHorarioFin);
        LocalDateTime inicioDT = fecha.atTime(inicioTime);
        LocalDateTime finDT = fecha.atTime(finTime);
        int duracion = turnosSlotDuracion;

        List<TurnoSlotStatusDTO> slots = turnoService.obtenerSlotsConEstado(inicioDT, finDT, duracion);

        if (!includeOccupied) {
            List<TurnoSlotStatusDTO> soloLibres = new ArrayList<>();
            for (TurnoSlotStatusDTO s : slots) {
                if (s.isDisponible()) soloLibres.add(s);
            }
            return ResponseEntity.ok(soloLibres);
        }

        return ResponseEntity.ok(slots);
    }    
    
    @PutMapping("/{id}/confirmar")
    @Operation(summary = "Confirmar un turno")
    public ResponseEntity<TurnoResponseDTO> confirmarTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.confirmarTurno(id));
    }
    
    @PutMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar un turno")
    public ResponseEntity<TurnoResponseDTO> cancelarTurno(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.cancelarTurno(id));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATIVO')")
    @Operation(summary = "Obtener todos los turnos (solo ADMIN y ADMINISTRATIVO)")
    public ResponseEntity<List<TurnoResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(turnoService.obtenerTodos());
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Obtener turno por ID")
    public ResponseEntity<TurnoResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(turnoService.obtenerPorId(id));
    }
    
    @GetMapping("/patente/{patente}")
    @Operation(summary = "Obtener turnos por patente")
    public ResponseEntity<List<TurnoResponseDTO>> obtenerPorPatente(@PathVariable String patente) {
        return ResponseEntity.ok(turnoService.obtenerPorPatente(patente));
    }
}
