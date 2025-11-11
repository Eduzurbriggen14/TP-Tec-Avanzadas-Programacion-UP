package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.dto.VehiculoDTO;
import com.example.gestionvehiculos.dto.vehiculo.VehiculoCreateDTO;
import com.example.gestionvehiculos.enums.TipoVehiculo;
import com.example.gestionvehiculos.service.VehiculoService;
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
@RequestMapping("/api/vehiculos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Vehículos", description = "Gestión de vehículos del sistema")
@SecurityRequirement(name = "bearerAuth")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @PostMapping
    public ResponseEntity<VehiculoDTO> crear(@Valid @RequestBody VehiculoCreateDTO vehiculoCreateDTO) {
        VehiculoDTO creado = vehiculoService.crear(vehiculoCreateDTO);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculoDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody VehiculoDTO vehiculoDTO) {
        VehiculoDTO actualizado = vehiculoService.actualizar(id, vehiculoDTO);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        vehiculoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculoDTO> obtenerPorId(@PathVariable Long id) {
        VehiculoDTO vehiculo = vehiculoService.obtenerPorId(id);
        return ResponseEntity.ok(vehiculo);
    }

    @GetMapping
    public ResponseEntity<List<VehiculoDTO>> obtenerTodos() {
        List<VehiculoDTO> vehiculos = vehiculoService.obtenerTodos();
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/patente/{patente}")
    public ResponseEntity<VehiculoDTO> obtenerPorPatente(@PathVariable String patente) {
        VehiculoDTO vehiculo = vehiculoService.obtenerPorPatente(patente);
        return ResponseEntity.ok(vehiculo);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<VehiculoDTO>> obtenerPorTipo(@PathVariable TipoVehiculo tipo) {
        List<VehiculoDTO> vehiculos = vehiculoService.obtenerPorTipo(tipo);
        return ResponseEntity.ok(vehiculos);
    }

    // El endpoint para filtrar por estado y cambiar estado se removió: el sistema no usa estados de alquiler
}
