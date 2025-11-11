package com.example.gestionvehiculos.dto.cliente;

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
public class ClienteUpdateDTO {
    
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;
    
    @Email(message = "El formato del correo es inválido")
    @Size(max = 150, message = "El correo no puede exceder los 150 caracteres")
    private String correo;
    
    @Size(max = 20, message = "El teléfono no puede exceder los 20 caracteres")
    private String telefono;
}
