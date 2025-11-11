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
public class RevisionVehiculoUpdateDTO {
    
    private LocalDate fechaRevision;
    
    private String resumen;
    
}
