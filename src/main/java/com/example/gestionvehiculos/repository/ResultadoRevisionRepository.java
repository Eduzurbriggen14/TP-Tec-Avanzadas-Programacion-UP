package com.example.gestionvehiculos.repository;

import com.example.gestionvehiculos.entity.ResultadoRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultadoRevisionRepository extends JpaRepository<ResultadoRevision, Long> {
}
