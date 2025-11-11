
package com.example.gestionvehiculos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "items_chequeo_template")
@Immutable // make the entity read-only to Hibernate after insert
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemChequeoTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_item", nullable = false, length = 100, updatable = false)
    private String nombreItem;

    @Column(name = "orden", nullable = false, updatable = false)
    private Integer orden;
}
