package com.example.gestionvehiculos.service;

import com.example.gestionvehiculos.dto.VehiculoDTO;
import com.example.gestionvehiculos.dto.vehiculo.VehiculoCreateDTO;
import com.example.gestionvehiculos.enums.TipoVehiculo;
import java.util.List;

public interface VehiculoService {
    
    VehiculoDTO crear(VehiculoCreateDTO vehiculoCreateDTO);

    default VehiculoDTO crear(VehiculoDTO vehiculoDTO) {
        // compat shim: convertir VehiculoDTO a VehiculoCreateDTO y delegar
        VehiculoCreateDTO create = new VehiculoCreateDTO();
        create.setPatente(vehiculoDTO.getPatente());
        create.setMarca(vehiculoDTO.getMarca());
        create.setModelo(vehiculoDTO.getModelo());
        create.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());
        return crear(create);
    }
    
    VehiculoDTO actualizar(Long id, VehiculoDTO vehiculoDTO);
    
    void eliminar(Long id);
    
    VehiculoDTO obtenerPorId(Long id);
    
    List<VehiculoDTO> obtenerTodos();
    
    VehiculoDTO obtenerPorPatente(String patente);
    
    List<VehiculoDTO> obtenerPorTipo(TipoVehiculo tipo);
    
}
