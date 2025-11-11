package com.example.gestionvehiculos.entity;

import com.example.gestionvehiculos.enums.EstadoTurno;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "turnos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Turno {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turno")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "estado_turno")
    private EstadoTurno estadoTurno;
    
    @Column(name = "fecha_turno", nullable = false)
    private LocalDate fechaTurno;
    
    @Column(name = "hora_turno", nullable = false)
    private LocalTime horaTurno;
    
    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDate fechaSolicitud;
    
    @Column(length = 500)
    private String observaciones;
    
    public void confirmar() {
        if (this.estadoTurno == EstadoTurno.PENDIENTE) {
            this.estadoTurno = EstadoTurno.CONFIRMADO;
        } else {
            throw new IllegalStateException("Solo se pueden confirmar turnos en estado PENDIENTE");
        }
    }
    
    public void cancelar() {
        if (this.estadoTurno == EstadoTurno.REALIZADO) {
            throw new IllegalStateException("No se puede cancelar un turno ya REALIZADO");
        }
        this.estadoTurno = EstadoTurno.CANCELADO;
    }
    
    public String obtenerResumenRev() {
        return String.format("Turno #%d - Cliente: %s - Vehículo: %s - Fecha: %s %s - Estado: %s",
            id, cliente != null ? cliente.getNombre() : "N/A", 
            vehiculo != null ? vehiculo.getPatente() : "N/A",
            fechaTurno, horaTurno, estadoTurno);
    }
}
