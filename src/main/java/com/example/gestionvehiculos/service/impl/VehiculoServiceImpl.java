package com.example.gestionvehiculos.service.impl;

import com.example.gestionvehiculos.dto.VehiculoDTO;
import com.example.gestionvehiculos.dto.vehiculo.VehiculoCreateDTO;
import com.example.gestionvehiculos.entity.Vehiculo;
import com.example.gestionvehiculos.enums.TipoVehiculo;
import com.example.gestionvehiculos.exception.DuplicateResourceException;
import com.example.gestionvehiculos.exception.ResourceNotFoundException;
import com.example.gestionvehiculos.repository.VehiculoRepository;
import com.example.gestionvehiculos.repository.ClienteRepository;
import com.example.gestionvehiculos.entity.Cliente;
import com.example.gestionvehiculos.service.VehiculoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class VehiculoServiceImpl implements VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;

    @Override
    public VehiculoDTO crear(VehiculoCreateDTO vehiculoCreateDTO) {
        if (vehiculoRepository.existsByPatente(vehiculoCreateDTO.getPatente())) {
            throw new DuplicateResourceException("La patente ya existe: " + vehiculoCreateDTO.getPatente());
        }

        Vehiculo vehiculo = convertirCreateDTOaEntidad(vehiculoCreateDTO);

        // Asociar cliente
        Long clienteId = vehiculoCreateDTO.getClienteId();
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con ID: " + clienteId));
        vehiculo.setCliente(cliente);

        vehiculo.setFechaAlta(LocalDate.now());
        Vehiculo guardado = vehiculoRepository.save(vehiculo);

        return convertirEntidadaDTO(guardado);
    }

    @Override
    public VehiculoDTO actualizar(Long id, VehiculoDTO vehiculoDTO) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con ID: " + id));

        if (!vehiculo.getPatente().equals(vehiculoDTO.getPatente()) && 
            vehiculoRepository.existsByPatente(vehiculoDTO.getPatente())) {
            throw new DuplicateResourceException("La patente ya existe: " + vehiculoDTO.getPatente());
        }

        vehiculo.setPatente(vehiculoDTO.getPatente());
        vehiculo.setMarca(vehiculoDTO.getMarca());
        vehiculo.setModelo(vehiculoDTO.getModelo());
        vehiculo.setTipoVehiculo(vehiculoDTO.getTipoVehiculo());

        Vehiculo actualizado = vehiculoRepository.save(vehiculo);
        return convertirEntidadaDTO(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vehículo no encontrado con ID: " + id);
        }
        vehiculoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDTO obtenerPorId(Long id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con ID: " + id));
        return convertirEntidadaDTO(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculoDTO> obtenerTodos() {
        return vehiculoRepository.findAll().stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculoDTO obtenerPorPatente(String patente) {
        Vehiculo vehiculo = vehiculoRepository.findByPatente(patente)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con patente: " + patente));
        return convertirEntidadaDTO(vehiculo);
    }


    @Override
    @Transactional(readOnly = true)
    public List<VehiculoDTO> obtenerPorTipo(TipoVehiculo tipo) {
        return vehiculoRepository.findByTipoVehiculo(tipo).stream()
                .map(this::convertirEntidadaDTO)
                .collect(Collectors.toList());
    }


    private VehiculoDTO convertirEntidadaDTO(Vehiculo vehiculo) {
        VehiculoDTO dto = new VehiculoDTO();
        dto.setId(vehiculo.getId());
        dto.setPatente(vehiculo.getPatente());
        dto.setMarca(vehiculo.getMarca());
        dto.setModelo(vehiculo.getModelo());
        dto.setTipoVehiculo(vehiculo.getTipoVehiculo());
        dto.setFechaAlta(vehiculo.getFechaAlta());
        return dto;
    }

    private Vehiculo convertirDTOaEntidad(VehiculoDTO dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(dto.getPatente());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        vehiculo.setTipoVehiculo(dto.getTipoVehiculo());
        return vehiculo;
    }

    private Vehiculo convertirCreateDTOaEntidad(VehiculoCreateDTO dto) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(dto.getPatente());
        vehiculo.setMarca(dto.getMarca());
        vehiculo.setModelo(dto.getModelo());
        // estado se asigna por defecto en el servicio
        vehiculo.setTipoVehiculo(dto.getTipoVehiculo());
        return vehiculo;
    }
}
