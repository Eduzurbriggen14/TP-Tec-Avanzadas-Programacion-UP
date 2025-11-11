package com.example.gestionvehiculos.service.impl;

import com.example.gestionvehiculos.dto.cliente.ClienteCreateDTO;
import com.example.gestionvehiculos.dto.cliente.ClienteResponseDTO;
import com.example.gestionvehiculos.dto.cliente.ClienteUpdateDTO;
import com.example.gestionvehiculos.entity.Cliente;
import com.example.gestionvehiculos.exception.DuplicateResourceException;
import com.example.gestionvehiculos.exception.ResourceNotFoundException;
import com.example.gestionvehiculos.repository.ClienteRepository;
import com.example.gestionvehiculos.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
    
    private final ClienteRepository clienteRepository;
    
    @Override
    @Transactional
    public ClienteResponseDTO crear(ClienteCreateDTO createDTO) {
        if (clienteRepository.existsByDni(createDTO.getDni())) {
            throw new DuplicateResourceException("Ya existe un cliente con DNI: " + createDTO.getDni());
        }
        
        if (clienteRepository.existsByCorreo(createDTO.getCorreo())) {
            throw new DuplicateResourceException("Ya existe un cliente con el correo: " + createDTO.getCorreo());
        }
        
        Cliente cliente = Cliente.builder()
            .dni(createDTO.getDni())
            .nombre(createDTO.getNombre())
            .apellido(createDTO.getApellido())
            .correo(createDTO.getCorreo())
            .telefono(createDTO.getTelefono())
            .build();
        
        cliente = clienteRepository.save(cliente);
        return convertirAResponseDTO(cliente);
    }
    
    @Override
    @Transactional
    public ClienteResponseDTO actualizar(Long id, ClienteUpdateDTO updateDTO) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        
        if (updateDTO.getNombre() != null) {
            cliente.setNombre(updateDTO.getNombre());
        }
        if (updateDTO.getApellido() != null) {
            cliente.setApellido(updateDTO.getApellido());
        }
        if (updateDTO.getCorreo() != null) {
            if (!updateDTO.getCorreo().equals(cliente.getCorreo()) && 
                clienteRepository.existsByCorreo(updateDTO.getCorreo())) {
                throw new DuplicateResourceException("Ya existe un cliente con el correo: " + updateDTO.getCorreo());
            }
            cliente.setCorreo(updateDTO.getCorreo());
        }
        if (updateDTO.getTelefono() != null) {
            cliente.setTelefono(updateDTO.getTelefono());
        }
        
        cliente = clienteRepository.save(cliente);
        return convertirAResponseDTO(cliente);
    }
    
    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + id));
        return convertirAResponseDTO(cliente);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerPorDni(String dni) {
        Cliente cliente = clienteRepository.findByDni(dni)
            .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con DNI: " + dni));
        return convertirAResponseDTO(cliente);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerTodos() {
        return clienteRepository.findAll()
            .stream()
            .map(this::convertirAResponseDTO)
            .collect(Collectors.toList());
    }
    
    private ClienteResponseDTO convertirAResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
            .id(cliente.getId())
            .dni(cliente.getDni())
            .nombre(cliente.getNombre())
            .apellido(cliente.getApellido())
            .correo(cliente.getCorreo())
            .telefono(cliente.getTelefono())
            .cantidadVehiculos(cliente.getVehiculos() != null ? cliente.getVehiculos().size() : 0)
            .cantidadTurnos(cliente.getTurnos() != null ? cliente.getTurnos().size() : 0)
            .build();
    }
}
