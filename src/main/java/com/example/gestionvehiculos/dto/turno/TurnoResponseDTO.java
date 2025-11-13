package com.example.gestionvehiculos.dto.turno;

import com.example.gestionvehiculos.enums.EstadoTurno;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoResponseDTO {
    
    private Long id;
    private Long vehiculoId;
    private String patente;
    private String marcaModelo; // Marca + Modelo del vehículo
    private Long clienteId;
    private String clienteNombre; // Nombre completo del cliente que solicitó
    private LocalDate fechaTurno;
    private LocalTime horaTurno;
    private EstadoTurno estadoTurno;
    private LocalDate fechaSolicitud;
    private String observaciones;
}
