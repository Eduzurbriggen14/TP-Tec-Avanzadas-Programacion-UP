package com.example.gestionvehiculos.entity;

import com.example.gestionvehiculos.enums.EstadoRevision;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RevisionVehiculoTest {

    private ItemChequeo item(String nombre, int puntuacion) {
        return ItemChequeo.builder()
                .nombreItem(nombre)
                .puntuacion(puntuacion)
                .observaciones(null)
                .build();
    }

    @Test
    void testEstadoSeguro() {
        RevisionVehiculo r = new RevisionVehiculo();
        List<ItemChequeo> items = new ArrayList<>();
        // 8 items, todos 10 => total 80
        for (int i = 0; i < 8; i++) items.add(item("I" + i, 10));
        r.setItemsChequeo(items);
        r.calcularPuntajeTotal();
        r.determinarEstadoResultado();

        assertEquals(80, r.getPuntajeTotal());
        assertEquals(EstadoRevision.SEGURO, r.getEstadoResultado());
    }

    @Test
    void testEstadoCondicionadoEn40() {
        RevisionVehiculo r = new RevisionVehiculo();
        List<ItemChequeo> items = new ArrayList<>();
        // 8 items, todos 5 => total 40 (5 is not <5)
        for (int i = 0; i < 8; i++) items.add(item("I" + i, 5));
        r.setItemsChequeo(items);
        r.calcularPuntajeTotal();
        r.determinarEstadoResultado();

        assertEquals(40, r.getPuntajeTotal());
        assertEquals(EstadoRevision.CONDICIONADO, r.getEstadoResultado(), "40 debe ser CONDICIONADO");
    }

    @Test
    void testEstadoRechequearBelow40() {
        RevisionVehiculo r = new RevisionVehiculo();
        List<ItemChequeo> items = new ArrayList<>();
        // make total 39: seven 5s (35) + one 4 = 39
        for (int i = 0; i < 7; i++) items.add(item("I" + i, 5));
        items.add(item("I7", 4));
        r.setItemsChequeo(items);
        r.calcularPuntajeTotal();
        r.determinarEstadoResultado();

        assertEquals(39, r.getPuntajeTotal());
        assertEquals(EstadoRevision.RECHEQUEAR, r.getEstadoResultado());
    }

    @Test
    void testEstadoRechequearItemBelow5Overrides() {
        RevisionVehiculo r = new RevisionVehiculo();
        List<ItemChequeo> items = new ArrayList<>();
        // one item 4, others 10 => total 74 but has item <5
        items.add(item("FRENOS", 4));
        for (int i = 1; i < 8; i++) items.add(item("I" + i, 10));
        r.setItemsChequeo(items);
        r.calcularPuntajeTotal();
        r.determinarEstadoResultado();

        assertEquals(74, r.getPuntajeTotal());
        assertEquals(EstadoRevision.RECHEQUEAR, r.getEstadoResultado(), "Ítem <5 obliga a RECHEQUEAR");
    }

    @Test
    void testValidarObservacionesRequerida() {
        RevisionVehiculo r = new RevisionVehiculo();
        List<ItemChequeo> items = new ArrayList<>();
        // total 30 to force <40
        for (int i = 0; i < 8; i++) items.add(item("I" + i, 3)); // 24
        items.set(0, item("I0", 6)); // +3 -> 27
        r.setItemsChequeo(items);
        r.calcularPuntajeTotal();
        r.determinarEstadoResultado();

        assertTrue(r.getPuntajeTotal() < 40);
        // no observaciones -> validarObservaciones false
        r.setObservaciones(null);
        assertFalse(r.validarObservaciones());

        r.setObservaciones("Necesita revisión en frenos y neumáticos");
        assertTrue(r.validarObservaciones());
    }
}
