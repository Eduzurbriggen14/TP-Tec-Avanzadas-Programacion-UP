package com.example.gestionvehiculos.dto.revision;

import com.example.gestionvehiculos.dto.ItemChequeoDTO;
import com.example.gestionvehiculos.enums.EstadoRevision;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RevisionResponseDTO {
    
    private Long id;
    private String usuarioRevisor;
    private String patente;
    private Long turnoId;
    private LocalDate fechaRevision;
    private String resumen;
    private List<ItemChequeoDTO> itemsChequeo;
    private Integer puntajeTotal;
    private EstadoRevision estadoResultado;
    private String observaciones;
    // Resultado resumen y estado final autogenerado
    private Long resultadoRevisionId;
    private String resultadoRevisionTexto;
    private EstadoRevision estadoFinalAuto;
}
