package com.example.gestionvehiculos.service;

import com.example.gestionvehiculos.dto.cliente.ClienteCreateDTO;
import com.example.gestionvehiculos.dto.cliente.ClienteResponseDTO;
import com.example.gestionvehiculos.dto.cliente.ClienteUpdateDTO;

import java.util.List;

public interface ClienteService {
    
    ClienteResponseDTO crear(ClienteCreateDTO createDTO);
    
    ClienteResponseDTO actualizar(Long id, ClienteUpdateDTO updateDTO);
    
    void eliminar(Long id);
    
    ClienteResponseDTO obtenerPorId(Long id);
    
    ClienteResponseDTO obtenerPorDni(String dni);
    
    List<ClienteResponseDTO> obtenerTodos();
}
