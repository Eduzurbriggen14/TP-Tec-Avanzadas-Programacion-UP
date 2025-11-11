package com.example.gestionvehiculos.dto.turno;

import jakarta.validation.constraints.NotNull;
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
public class TurnoSolicitudDTO {
    
    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehiculoId;

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    @NotNull(message = "La fecha del turno es obligatoria")
    private LocalDate fechaTurno;
    
    @NotNull(message = "La hora del turno es obligatoria")
    private LocalTime horaTurno;
    
    private String observaciones;
}
