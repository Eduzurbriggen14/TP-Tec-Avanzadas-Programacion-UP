package com.example.gestionvehiculos.dto.usuario;

import com.example.gestionvehiculos.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    
    private Long id;
    private String userName;
    private String nombre;
    private String apellido;
    private String correo;
    private String telefono;
    private Rol rol;
    private Boolean activo;
    
    // NO incluimos passwd por seguridad
}
