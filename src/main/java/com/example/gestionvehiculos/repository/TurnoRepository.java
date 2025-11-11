package com.example.gestionvehiculos.repository;

import com.example.gestionvehiculos.entity.Cliente;
import com.example.gestionvehiculos.entity.Turno;
import com.example.gestionvehiculos.entity.Vehiculo;
import com.example.gestionvehiculos.enums.EstadoTurno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {
    
    List<Turno> findByVehiculo(Vehiculo vehiculo);
    
    List<Turno> findByEstadoTurno(EstadoTurno estadoTurno);
    
    @Query("SELECT t FROM Turno t WHERE t.fechaTurno >= :fechaDesde AND t.estadoTurno IN :estados ORDER BY t.fechaTurno, t.horaTurno")
    List<Turno> findTurnosDisponibles(
        @Param("fechaDesde") LocalDate fechaDesde, 
        @Param("estados") List<EstadoTurno> estados
    );

    @Query("SELECT t.horaTurno FROM Turno t WHERE t.fechaTurno = :fecha AND t.estadoTurno IN :estados")
    List<java.time.LocalTime> findHorasOcupadasByFechaAndEstadoIn(@Param("fecha") LocalDate fecha,
                                                                 @Param("estados") List<EstadoTurno> estados);

    // Buscar turnos por fecha y estados (útil para calcular slots libres)
    List<Turno> findByFechaTurnoAndEstadoTurnoIn(LocalDate fechaTurno, List<EstadoTurno> estados);
    
    List<Turno> findByCliente(Cliente cliente);
    
    List<Turno> findByClienteId(Long clienteId);
}
