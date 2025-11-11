package com.example.gestionvehiculos.dto;

import com.example.gestionvehiculos.enums.TipoVehiculo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoDTO {
    
    private Long id;
    
    @NotBlank(message = "La patente es obligatoria")
    private String patente;
    
    @NotBlank(message = "La marca es obligatoria")
    private String marca;
    
    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;
    
    // estadoMantenimiento removido: se asigna por defecto en el servicio
    
    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;
    
    private LocalDate fechaAlta;
}
