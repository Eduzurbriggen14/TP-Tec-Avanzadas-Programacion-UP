package com.example.gestionvehiculos.dto.vehiculo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculoUpdateDTO {
    
    private String marca;
    
    private String modelo;
    
    // NO incluimos patente (no se puede cambiar)
    // NO incluimos tipoVehiculo (no se puede cambiar)
    // estadoMantenimiento se cambia con endpoint específico
    // fechaAlta no se modifica
}
