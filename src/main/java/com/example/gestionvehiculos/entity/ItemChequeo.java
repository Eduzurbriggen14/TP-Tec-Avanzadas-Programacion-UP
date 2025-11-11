package com.example.gestionvehiculos.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items_chequeo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemChequeo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revision_id", nullable = false)
    private RevisionVehiculo revision;
    
    @Column(name = "nombre_item", nullable = false, length = 100)
    private String nombreItem;
    
    @Min(1)
    @Max(10)
    @Column(nullable = false)
    private Integer puntuacion;
    
    @Column(length = 500)
    private String observaciones;
    
    public boolean validarPuntuacion() {
        return puntuacion != null && puntuacion >= 1 && puntuacion <= 10;
    }
}
