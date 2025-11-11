package com.example.gestionvehiculos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevisionVehiculoDTO {
    
    private Long id;
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long idUsuario;
    
    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long idVehiculo;
    
    @NotNull(message = "La fecha de revisión es obligatoria")
    private LocalDate fechaRevision;
    
    @NotBlank(message = "El resumen es obligatorio")
    private String resumen;
}
