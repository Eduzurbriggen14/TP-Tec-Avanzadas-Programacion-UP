package com.example.gestionvehiculos.dto.vehiculo;

import com.example.gestionvehiculos.enums.TipoVehiculo;
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
public class VehiculoCreateDTO {
    
    @NotBlank(message = "La patente es obligatoria")
    private String patente;
    
    @NotBlank(message = "La marca es obligatoria")
    private String marca;
    
    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;
    
    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipoVehiculo;
    
    @NotNull(message = "El clienteId es obligatorio")
    private Long clienteId;
    

}
