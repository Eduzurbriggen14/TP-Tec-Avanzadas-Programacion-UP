package com.example.gestionvehiculos.enums;

public enum EstadoRevision {
    SEGURO,         // Puntaje >= 80 puntos
    CONDICIONADO,   // Puntaje entre 40 y 79 puntos, PERO SIN ITEMS POR DEBAJO DE 5 PUNTOS
    RECHEQUEAR      // Puntaje < 40 o algún ítem < 5 puntos
}
