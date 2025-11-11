package com.example.gestionvehiculos.dto.vehiculo;

import com.example.gestionvehiculos.enums.TipoVehiculo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculoResponseDTO {
    
    private Long id;
    private String patente;
    private String marca;
    private String modelo;
    private TipoVehiculo tipoVehiculo;
    private LocalDate fechaAlta;
}
