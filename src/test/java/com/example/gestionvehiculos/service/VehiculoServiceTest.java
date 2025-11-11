package com.example.gestionvehiculos.service;

import com.example.gestionvehiculos.dto.VehiculoDTO;
import com.example.gestionvehiculos.dto.vehiculo.VehiculoCreateDTO;
import com.example.gestionvehiculos.entity.Vehiculo;
import com.example.gestionvehiculos.enums.TipoVehiculo;
import com.example.gestionvehiculos.exception.DuplicateResourceException;
import com.example.gestionvehiculos.exception.ResourceNotFoundException;
import com.example.gestionvehiculos.repository.VehiculoRepository;
import com.example.gestionvehiculos.repository.ClienteRepository;
import com.example.gestionvehiculos.entity.Cliente;
import com.example.gestionvehiculos.service.impl.VehiculoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Servicio de Vehículos")
class VehiculoServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private VehiculoServiceImpl vehiculoService;

    private VehiculoCreateDTO vehiculoCreateDTO;
    private Vehiculo vehiculo;

    @BeforeEach
    void setUp() {
    vehiculoCreateDTO = new VehiculoCreateDTO();
    vehiculoCreateDTO.setPatente("ABC123");
    vehiculoCreateDTO.setMarca("Toyota");
    vehiculoCreateDTO.setModelo("Corolla");
    vehiculoCreateDTO.setTipoVehiculo(TipoVehiculo.AUTO);
    vehiculoCreateDTO.setClienteId(1L);

        vehiculo = new Vehiculo();
        vehiculo.setId(1L);
        vehiculo.setPatente("ABC123");
        vehiculo.setMarca("Toyota");
        vehiculo.setModelo("Corolla");
    // estado del vehículo ya no se usa en la API
        vehiculo.setTipoVehiculo(TipoVehiculo.AUTO);
        vehiculo.setFechaAlta(LocalDate.now());
    }

    @Test
    @DisplayName("Crear vehículo exitosamente")
    void testCrearVehiculo_Success() {
    // Arrange
    when(vehiculoRepository.existsByPatente(vehiculoCreateDTO.getPatente())).thenReturn(false);
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        // Mock cliente lookup
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        // Act
        VehiculoDTO resultado = vehiculoService.crear(vehiculoCreateDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals("ABC123", resultado.getPatente());
        assertEquals("Toyota", resultado.getMarca());
        verify(vehiculoRepository, times(1)).existsByPatente("ABC123");
        verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
    }

    @Test
    @DisplayName("Error al crear vehículo con patente duplicada")
    void testCrearVehiculo_PatenteExistente() {
        // Arrange
        when(vehiculoRepository.existsByPatente(vehiculoCreateDTO.getPatente())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> {
            vehiculoService.crear(vehiculoCreateDTO);
        });
        
        verify(vehiculoRepository, times(1)).existsByPatente("ABC123");
        verify(vehiculoRepository, never()).save(any(Vehiculo.class));
    }

    @Test
    @DisplayName("Obtener vehículo por ID exitosamente")
    void testObtenerPorId_Success() {
        // Arrange
        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        // Act
        VehiculoDTO resultado = vehiculoService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("ABC123", resultado.getPatente());
        verify(vehiculoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Error al obtener vehículo inexistente")
    void testObtenerPorId_NoEncontrado() {
        // Arrange
        when(vehiculoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            vehiculoService.obtenerPorId(999L);
        });
        
        verify(vehiculoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Listar todos los vehículos")
    void testObtenerTodos() {
        // Arrange
        Vehiculo vehiculo2 = new Vehiculo();
        vehiculo2.setId(2L);
        vehiculo2.setPatente("XYZ789");
        vehiculo2.setMarca("Honda");
        vehiculo2.setModelo("Civic");
        
        when(vehiculoRepository.findAll()).thenReturn(Arrays.asList(vehiculo, vehiculo2));

        // Act
        List<VehiculoDTO> resultado = vehiculoService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("ABC123", resultado.get(0).getPatente());
        assertEquals("XYZ789", resultado.get(1).getPatente());
        verify(vehiculoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Actualizar vehículo exitosamente")
    void testActualizar_Success() {
        // Arrange
        VehiculoDTO actualizacionDTO = new VehiculoDTO();
    actualizacionDTO.setPatente("ABC123");
    actualizacionDTO.setMarca("Toyota");
    actualizacionDTO.setModelo("Corolla 2024");
    actualizacionDTO.setTipoVehiculo(TipoVehiculo.AUTO);

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));
        when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vehiculo);

        // Act
        VehiculoDTO resultado = vehiculoService.actualizar(1L, actualizacionDTO);

        // Assert
        assertNotNull(resultado);
        verify(vehiculoRepository, times(1)).findById(1L);
        verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
    }

    @Test
    @DisplayName("Eliminar vehículo exitosamente")
    void testEliminar_Success() {
        // Arrange
        when(vehiculoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(vehiculoRepository).deleteById(1L);

        // Act
        vehiculoService.eliminar(1L);

        // Assert
        verify(vehiculoRepository, times(1)).existsById(1L);
        verify(vehiculoRepository, times(1)).deleteById(1L);
    }

}
