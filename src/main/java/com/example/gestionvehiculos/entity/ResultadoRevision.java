package com.example.gestionvehiculos.entity;

import com.example.gestionvehiculos.enums.EstadoRevision;
import com.example.gestionvehiculos.converter.EstadoRevisionConverter;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "resultados_revision")
public class ResultadoRevision {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado_revision")
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String resultado;
    
    @Convert(converter = EstadoRevisionConverter.class)
    @Column(nullable = false, name = "estado_final_auto", columnDefinition = "INT")
    private EstadoRevision estadoFinalAuto;
    
    @OneToOne(mappedBy = "resultadoRevision")
    private RevisionVehiculo revision;
    
    public String obtenerResultadoRevision() {
        return String.format("Resultado: %s - Estado del vehículo: %s", 
            resultado, estadoFinalAuto);
    }
}
