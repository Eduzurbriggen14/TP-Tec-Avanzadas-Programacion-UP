package com.example.gestionvehiculos.config;

import com.example.gestionvehiculos.entity.ItemChequeoTemplate;
import com.example.gestionvehiculos.repository.ItemChequeoTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TemplateDataLoader {

    private final ItemChequeoTemplateRepository templateRepository;

    @PostConstruct
    public void loadTemplates() {
        if (templateRepository.count() > 0) return;

        List<ItemChequeoTemplate> defaults = Arrays.asList(
                ItemChequeoTemplate.builder().orden(1).nombreItem("LUCES").build(),
                ItemChequeoTemplate.builder().orden(2).nombreItem("FRENOS").build(),
                ItemChequeoTemplate.builder().orden(3).nombreItem("DIRECCION").build(),
                ItemChequeoTemplate.builder().orden(4).nombreItem("SUSPENSION").build(),
                ItemChequeoTemplate.builder().orden(5).nombreItem("NEUMATICOS").build(),
                ItemChequeoTemplate.builder().orden(6).nombreItem("MOTOR").build(),
                ItemChequeoTemplate.builder().orden(7).nombreItem("ESCAPE").build(),
                ItemChequeoTemplate.builder().orden(8).nombreItem("CHASIS").build()
        );

        templateRepository.saveAll(defaults);
    }
}
