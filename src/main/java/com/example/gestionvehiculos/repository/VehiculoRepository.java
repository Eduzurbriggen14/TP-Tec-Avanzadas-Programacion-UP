package com.example.gestionvehiculos.repository;

import com.example.gestionvehiculos.entity.Vehiculo;
import com.example.gestionvehiculos.enums.TipoVehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    
    Optional<Vehiculo> findByPatente(String patente);
    
    // findByEstadoMantenimiento removed: estadoMantenimiento no forma parte del API
    
    List<Vehiculo> findByTipoVehiculo(TipoVehiculo tipo);
    
    List<Vehiculo> findByMarca(String marca);
    
    boolean existsByPatente(String patente);
}
