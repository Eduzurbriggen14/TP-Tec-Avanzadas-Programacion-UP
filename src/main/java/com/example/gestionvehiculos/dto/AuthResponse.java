package com.example.gestionvehiculos.dto;

import com.example.gestionvehiculos.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private String userName;
    private Rol rol;
    private String nombre;
    private String apellido;
    
    public AuthResponse(String token, String userName, Rol rol, String nombre, String apellido) {
        this.token = token;
        this.userName = userName;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
    }
}
