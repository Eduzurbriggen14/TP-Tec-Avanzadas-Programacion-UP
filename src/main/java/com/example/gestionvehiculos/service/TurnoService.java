package com.example.gestionvehiculos.service;

import com.example.gestionvehiculos.dto.turno.TurnoResponseDTO;
import com.example.gestionvehiculos.dto.turno.TurnoSolicitudDTO;
import com.example.gestionvehiculos.dto.turno.TurnoSlotDTO;

import java.time.LocalDateTime;
import java.util.List;
import com.example.gestionvehiculos.dto.turno.TurnoSlotStatusDTO;

public interface TurnoService {
    

    TurnoResponseDTO solicitarTurno(TurnoSolicitudDTO solicitudDTO, String userName);
    

    List<TurnoResponseDTO> obtenerTurnosDisponibles();

    /**
     * Calcula los slots libres en una fecha dada entre un horario de inicio y fin.
     * @param fecha fecha solicitada
     * @param inicio hora de inicio (inclusive)
     * @param fin hora de fin (exclusive)
     * @param duracionMinutos duración del slot en minutos
     */
    List<TurnoSlotDTO> obtenerSlotsDisponibles(LocalDateTime inicio, LocalDateTime fin, int duracionMinutos);

    List<TurnoSlotStatusDTO> obtenerSlotsConEstado(LocalDateTime inicio, LocalDateTime fin, int duracionMinutos);
    

    TurnoResponseDTO confirmarTurno(Long turnoId);
    
    TurnoResponseDTO cancelarTurno(Long turnoId);
    
    List<TurnoResponseDTO> obtenerTodos();
    
    TurnoResponseDTO obtenerPorId(Long id);
    
    List<TurnoResponseDTO> obtenerPorPatente(String patente);
}
