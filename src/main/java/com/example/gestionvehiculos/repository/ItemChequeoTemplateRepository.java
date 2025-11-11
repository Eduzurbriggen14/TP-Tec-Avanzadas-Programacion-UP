package com.example.gestionvehiculos.repository;

import com.example.gestionvehiculos.entity.ItemChequeoTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemChequeoTemplateRepository extends JpaRepository<ItemChequeoTemplate, Long> {
    List<ItemChequeoTemplate> findAllByOrderByOrdenAsc();
}
