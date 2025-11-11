package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.dto.VehiculoDTO;
import com.example.gestionvehiculos.dto.vehiculo.VehiculoCreateDTO;
import com.example.gestionvehiculos.enums.TipoVehiculo;
import com.example.gestionvehiculos.service.VehiculoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.example.gestionvehiculos.security.JwtTokenProvider;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculoController.class)
@AutoConfigureMockMvc
@DisplayName("Tests del Controller de Vehículos")
class VehiculoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehiculoService vehiculoService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private VehiculoDTO vehiculoDTO;
    private VehiculoCreateDTO vehiculoCreateDTO;

    @BeforeEach
    void setUp() {
    vehiculoDTO = new VehiculoDTO();
        vehiculoDTO.setId(1L);
        vehiculoDTO.setPatente("ABC123");
        vehiculoDTO.setMarca("Toyota");
        vehiculoDTO.setModelo("Corolla");
    // estado removed
        vehiculoDTO.setTipoVehiculo(TipoVehiculo.AUTO);

    vehiculoCreateDTO = new VehiculoCreateDTO();
    vehiculoCreateDTO.setPatente("ABC123");
    vehiculoCreateDTO.setMarca("Toyota");
    vehiculoCreateDTO.setModelo("Corolla");
    vehiculoCreateDTO.setTipoVehiculo(TipoVehiculo.AUTO);
    vehiculoCreateDTO.setClienteId(1L);
    }

    @Test
    @DisplayName("Crear vehículo - Requiere autenticación ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testCrearVehiculo_ConAutenticacion() throws Exception {
        // Arrange
    when(vehiculoService.crear(any(VehiculoCreateDTO.class))).thenReturn(vehiculoDTO);

        // Act & Assert
        mockMvc.perform(post("/api/vehiculos")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(vehiculoCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patente").value("ABC123"))
                .andExpect(jsonPath("$.marca").value("Toyota"));
    verify(vehiculoService, times(1)).crear(any(VehiculoCreateDTO.class));
    }

    @Test
    @DisplayName("Obtener todos los vehículos - Acceso público autenticado")
    @WithMockUser
    void testObtenerTodos() throws Exception {
        // Arrange
        List<VehiculoDTO> vehiculos = Arrays.asList(vehiculoDTO);
        when(vehiculoService.obtenerTodos()).thenReturn(vehiculos);

        // Act & Assert
        mockMvc.perform(get("/api/vehiculos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patente").value("ABC123"));

        verify(vehiculoService, times(1)).obtenerTodos();
    }

    @Test
    @DisplayName("Obtener vehículo por ID")
    @WithMockUser
    void testObtenerPorId() throws Exception {
        // Arrange
        when(vehiculoService.obtenerPorId(1L)).thenReturn(vehiculoDTO);

        // Act & Assert
        mockMvc.perform(get("/api/vehiculos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.patente").value("ABC123"));

        verify(vehiculoService, times(1)).obtenerPorId(1L);
    }

    // testObtenerPorEstado removed: API no usa estados de vehículo

    @Test
    @DisplayName("Actualizar vehículo - Requiere ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testActualizarVehiculo() throws Exception {
        // Arrange
        when(vehiculoService.actualizar(eq(1L), any(VehiculoDTO.class))).thenReturn(vehiculoDTO);

        // Act & Assert
        mockMvc.perform(put("/api/vehiculos/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehiculoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patente").value("ABC123"));

        verify(vehiculoService, times(1)).actualizar(eq(1L), any(VehiculoDTO.class));
    }

    @Test
    @DisplayName("Eliminar vehículo - Requiere ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testEliminarVehiculo() throws Exception {
        // Arrange
        doNothing().when(vehiculoService).eliminar(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/vehiculos/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(vehiculoService, times(1)).eliminar(1L);
    }

    // testCambiarEstado removed: API no usa estados de vehículo
}
