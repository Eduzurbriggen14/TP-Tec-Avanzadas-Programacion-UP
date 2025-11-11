package com.example.gestionvehiculos.entity;

import com.example.gestionvehiculos.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehiculos")
public class Vehiculo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String patente;
    
    @Column(nullable = false)
    private String marca;
    
    @Column(nullable = false)
    private String modelo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "tipo_vehiculo")
    private TipoVehiculo tipoVehiculo;
    
    @Column(name = "fecha_alta")
    private LocalDate fechaAlta;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RevisionVehiculo> revisiones = new ArrayList<>();
    
    @OneToMany(mappedBy = "vehiculo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Turno> turnos = new ArrayList<>();
    
    // Métodos según UML
    
    /**
     * Obtiene los datos principales del vehículo
     */
    public String obtenerDatos() {
        return String.format("Vehículo: %s %s - Patente: %s - Tipo: %s", 
            marca, modelo, patente, tipoVehiculo);
    }
    
    /**
     * Valida formato de patente (puede expandirse con regex)
     */
    public boolean validarPatente() {
        if (patente == null || patente.trim().isEmpty()) {
            return false;
        }
        // Validación básica: entre 6 y 8 caracteres alfanuméricos
        return patente.matches("^[A-Z0-9]{6,8}$");
    }
}
