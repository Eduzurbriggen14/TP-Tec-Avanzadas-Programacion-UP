package com.example.gestionvehiculos.repository;

import com.example.gestionvehiculos.entity.RevisionVehiculo;
import com.example.gestionvehiculos.entity.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RevisionVehiculoRepository extends JpaRepository<RevisionVehiculo, Long> {
    
    List<RevisionVehiculo> findByVehiculo(Vehiculo vehiculo);
    
    List<RevisionVehiculo> findByFechaRevisionBetween(LocalDate inicio, LocalDate fin);
    
    List<RevisionVehiculo> findByVehiculoOrderByFechaRevisionDesc(Vehiculo vehiculo);
}
