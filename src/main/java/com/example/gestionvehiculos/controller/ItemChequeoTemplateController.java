package com.example.gestionvehiculos.controller;

import com.example.gestionvehiculos.dto.ItemChequeoDTO;
import com.example.gestionvehiculos.repository.ItemChequeoTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items-chequeo/template")
public class ItemChequeoTemplateController {

    private final ItemChequeoTemplateRepository templateRepository;

    @GetMapping
    public ResponseEntity<List<ItemChequeoDTO>> obtenerTemplates() {
        List<ItemChequeoDTO> items = templateRepository.findAllByOrderByOrdenAsc()
                .stream()
                .map(t -> ItemChequeoDTO.builder()
                        .nombreItem(t.getNombreItem())
                        .puntuacion(null)
                        .observaciones(null)
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
}
