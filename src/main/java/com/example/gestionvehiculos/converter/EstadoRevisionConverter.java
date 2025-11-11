package com.example.gestionvehiculos.converter;

import com.example.gestionvehiculos.enums.EstadoRevision;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Convierte {@link EstadoRevision} a Integer en la base de datos y viceversa.
 * Mapeo por defecto propuesto:
 * SEGURO -> 1
 * CONDICIONADO -> 2
 * RECHEQUEAR -> 3
 */
@Converter(autoApply = false)
public class EstadoRevisionConverter implements AttributeConverter<EstadoRevision, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EstadoRevision attribute) {
        if (attribute == null) return null;
        switch (attribute) {
            case SEGURO:
                return 1;
            case CONDICIONADO:
                return 2;
            case RECHEQUEAR:
                return 3;
            default:
                throw new IllegalArgumentException("Unknown EstadoRevision: " + attribute);
        }
    }

    @Override
    public EstadoRevision convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;
        switch (dbData) {
            case 1:
                return EstadoRevision.SEGURO;
            case 2:
                return EstadoRevision.CONDICIONADO;
            case 3:
                return EstadoRevision.RECHEQUEAR;
            default:
                throw new IllegalArgumentException("Unknown EstadoRevision id: " + dbData);
        }
    }
}
