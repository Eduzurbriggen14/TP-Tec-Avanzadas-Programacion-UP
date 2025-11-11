package com.example.gestionvehiculos.dto.revision;

import com.example.gestionvehiculos.dto.ItemChequeoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionCreateDTO {
    
    @NotBlank(message = "La patente es obligatoria")
    private String patente;
    
    private Long turnoId;
    
    @NotBlank(message = "El resumen es obligatorio")
    private String resumen;
    
    // itemsChequeo ahora es opcional: se pueden obtener del template y luego enviar completados
    @Valid
    private List<ItemChequeoDTO> itemsChequeo;
    
    private String observaciones;
}
