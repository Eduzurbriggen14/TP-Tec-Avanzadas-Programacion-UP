package com.example.gestionvehiculos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GestionVehiculosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionVehiculosApplication.class, args);
    }
}
