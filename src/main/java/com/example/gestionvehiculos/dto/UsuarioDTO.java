package com.example.gestionvehiculos.dto;

import com.example.gestionvehiculos.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    
    @Email(message = "Email inválido")
    @NotBlank(message = "El correo es obligatorio")
    private String correo;
    
    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String userName;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String passwd;
    
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;
    
    private boolean activo;
}
