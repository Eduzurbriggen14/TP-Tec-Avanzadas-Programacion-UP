package com.example.gestionvehiculos.dto.revision;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionVehiculoResponseDTO {
    
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long vehiculoId;
    private String vehiculoPatente;
    private LocalDate fechaRevision;
    private String resumen;
}
