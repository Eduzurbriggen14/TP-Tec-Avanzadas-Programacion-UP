package com.example.gestionvehiculos.entity;

import com.example.gestionvehiculos.enums.Rol;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "usuarios")
public class Usuario extends Persona {
    
    @Column(nullable = false, unique = true, name = "user_name", length = 50)
    private String userName;
    
    @Column(nullable = false, name = "passwd")
    private String passwd;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Rol rol;
    
    @Column(nullable = false)
    private boolean activo = true;
    
    @Override
    public String obtenerDatos() {
        return String.format("Usuario: %s %s - Username: %s - Rol: %s - DNI: %s", 
            getNombre(), getApellido(), userName, rol, getDni());
    }
    
    public boolean autenticarse(String username, String password) {
        return this.userName.equals(username) && this.passwd.equals(password) && this.activo;
    }
    
    public Rol obtenerRol() {
        return this.rol;
    }
}
