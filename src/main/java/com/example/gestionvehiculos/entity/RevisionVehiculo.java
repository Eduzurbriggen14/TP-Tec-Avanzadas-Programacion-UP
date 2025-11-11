package com.example.gestionvehiculos.entity;

import com.example.gestionvehiculos.enums.EstadoRevision;
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
@Table(name = "revisiones_vehiculo")
public class RevisionVehiculo {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehiculo", nullable = false)
    private Vehiculo vehiculo;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_turno")
    private Turno turno;
    
    @Column(name = "fecha_revision", nullable = false)
    private LocalDate fechaRevision;
    
    @Column(nullable = false, length = 1000)
    private String resumen;
    
    @OneToMany(mappedBy = "revision", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemChequeo> itemsChequeo = new ArrayList<>();
    
    @Column(name = "puntaje_total")
    private Integer puntajeTotal;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_resultado", length = 20)
    private EstadoRevision estadoResultado;
    
    @Column(length = 2000)
    private String observaciones;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resultado_revision_id")
    private ResultadoRevision resultadoRevision;
    
    /**
     * Calcula el puntaje total sumando todos los items de chequeo
     */
    public void calcularPuntajeTotal() {
        this.puntajeTotal = itemsChequeo.stream()
            .mapToInt(ItemChequeo::getPuntuacion)
            .sum();
    }
    
    /**
     * Determina el estado del resultado usando porcentajes relativos al
     * puntaje máximo posible (cantidad_items * 10). Reglas:
     * - >= 80%: SEGURO
     * - >= 40% y < 80%: CONDICIONADO
     * - < 40%: RECHEQUEAR
     * Además, si algún ítem tiene puntuación < 5 => RECHEQUEAR (override)
     */
    public void determinarEstadoResultado() {
        boolean tieneItemBajo = itemsChequeo.stream()
            .anyMatch(item -> item.getPuntuacion() < 5);

        int maxTotal = itemsChequeo.size() * 10;
        int porcentaje = 0;
        if (maxTotal > 0 && puntajeTotal != null) {
            porcentaje = (int) Math.round((puntajeTotal.doubleValue() * 100.0) / maxTotal);
        }

        if (tieneItemBajo) {
            this.estadoResultado = EstadoRevision.RECHEQUEAR;
            return;
        }

        if (porcentaje >= 80) {
            this.estadoResultado = EstadoRevision.SEGURO;
        } else if (porcentaje >= 40) {
            this.estadoResultado = EstadoRevision.CONDICIONADO;
        } else {
            this.estadoResultado = EstadoRevision.RECHEQUEAR;
        }
    }
    
    /**
     * Valida que si el puntaje porcentual es < 40%, debe haber observaciones
     */
    public boolean validarObservaciones() {
        int maxTotal = itemsChequeo.size() * 10;
        int porcentaje = 0;
        if (maxTotal > 0 && puntajeTotal != null) {
            porcentaje = (int) Math.round((puntajeTotal.doubleValue() * 100.0) / maxTotal);
        }
        if (porcentaje < 40 && (observaciones == null || observaciones.trim().isEmpty())) {
            return false;
        }
        return true;
    }

    /**
     * Obtiene el puntaje máximo posible según la cantidad de items
     */
    public int getPuntajeMaximo() {
        return itemsChequeo.size() * 10;
    }

    /**
     * Obtiene el puntaje porcentual (0-100) respecto al máximo posible
     */
    public int getPuntajePorcentual() {
        int maxTotal = getPuntajeMaximo();
        if (maxTotal == 0 || puntajeTotal == null) return 0;
        return (int) Math.round((puntajeTotal.doubleValue() * 100.0) / maxTotal);
    }
    
    public String obtenerResumen(String nivel) {
        return String.format("[%s] Revisión del %s - Vehículo: %s - Puntaje: %d - Estado: %s - %s", 
            nivel, fechaRevision, vehiculo != null ? vehiculo.getPatente() : "N/A", 
            puntajeTotal != null ? puntajeTotal : 0,
            estadoResultado != null ? estadoResultado : "N/A",
            resumen);
    }
}
