package com.example.gestionvehiculos.dto.revision;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionVehiculoCreateDTO {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;
    
    @NotNull(message = "El ID del vehículo es obligatorio")
    private Long vehiculoId;
    
    @NotNull(message = "La fecha de revisión es obligatoria")
    private LocalDate fechaRevision;
    
    @NotBlank(message = "El resumen es obligatorio")
    private String resumen;
}
