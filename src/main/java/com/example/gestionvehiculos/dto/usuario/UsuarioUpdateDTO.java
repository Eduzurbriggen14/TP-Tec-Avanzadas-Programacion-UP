package com.example.gestionvehiculos.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUpdateDTO {
    
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String passwd;
    
    private String nombre;
    
    private String apellido;
    
    @Email(message = "El formato del correo no es válido")
    private String correo;
    
    private String telefono;
    
    private Boolean activo;
    
    // NO incluimos userName (no se puede cambiar) ni rol (cambio requiere endpoint especial)
}
