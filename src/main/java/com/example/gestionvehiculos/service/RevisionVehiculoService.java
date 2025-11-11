package com.example.gestionvehiculos.service;

import com.example.gestionvehiculos.dto.revision.RevisionCreateDTO;
import com.example.gestionvehiculos.dto.revision.RevisionResponseDTO;
import java.time.LocalDate;
import java.util.List;

public interface RevisionVehiculoService {
    
    /**
     * Crea una revisión con chequeo de items y cálculo automático de puntaje
     */
    RevisionResponseDTO crear(RevisionCreateDTO revisionDTO, String userName);
    
    /**
     * Elimina una revisión
     */
    void eliminar(Long id);
    
    /**
     * Obtiene una revisión por ID
     */
    RevisionResponseDTO obtenerPorId(Long id);
    
    /**
     * Obtiene todas las revisiones
     */
    List<RevisionResponseDTO> obtenerTodos();
    
    /**
     * Obtiene revisiones por patente del vehículo
     */
    List<RevisionResponseDTO> obtenerPorPatente(String patente);
    
    /**
     * Obtiene revisiones por rango de fechas
     */
    List<RevisionResponseDTO> obtenerPorRangoFechas(LocalDate inicio, LocalDate fin);
}
