package com.example.gestionvehiculos.dto.turno;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TurnoSlotDTO {
    private LocalTime inicio;
    private LocalTime fin;
}
