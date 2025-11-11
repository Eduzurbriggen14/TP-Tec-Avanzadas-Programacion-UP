package com.example.gestionvehiculos.dto.cliente;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDTO {
    
    private Long id;
    private String dni;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private Integer cantidadVehiculos;
    private Integer cantidadTurnos;
}
