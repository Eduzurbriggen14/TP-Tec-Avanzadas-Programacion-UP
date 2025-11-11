## 1. USUARIOS (Crear via POST /api/auth/register)

### Usuario 1: ADMIN
```json
{
  "dni": "20111222",
  "nombre": "Juan",
  "apellido": "Perez",
  "correo": "juan.perez@admin.com",
  "telefono": "1145678901",
  "userName": "jperez",
  "passwd": "password123",
  "rol": "ADMIN",
  "activo": true
}
```
**Credenciales:** jperez / password123

---

### Usuario 2: ADMINISTRATIVO
```json
{
  "dni": "25333444",
  "nombre": "Maria",
  "apellido": "Gonzalez",
  "correo": "maria.gonzalez@administrativo.com",
  "telefono": "1156789012",
  "userName": "mgonzalez",
  "passwd": "password123",
  "rol": "ADMINISTRATIVO",
  "activo": true
}
```
**Credenciales:** mgonzalez / password123

---

### Usuario 3: INSPECTOR
```json
{
  "dni": "30555666",
  "nombre": "Carlos",
  "apellido": "Rodriguez",
  "correo": "carlos.rodriguez@inspeccion.com",
  "telefono": "1167890123",
  "userName": "crodriguez",
  "passwd": "password123",
  "rol": "INSPECTOR",
  "activo": true
}
```
**Credenciales:** crodriguez / password123

---

## 2. CLIENTES (/api/clientes) hay que ser admin o administrativo


### Cliente 1: Laura Martinez
```json
{
  "dni": "35111222",
  "nombre": "Laura",
  "apellido": "Martinez",
  "correo": "laura.martinez@gmail.com",
  "telefono": "1178901234"
}
```

---

### Cliente 2: Roberto Fernandez
```json
{
  "dni": "40222333",
  "nombre": "Roberto",
  "apellido": "Fernandez",
  "correo": "roberto.fernandez@outlook.com",
  "telefono": "1189012345"
}
```

---

### Cliente 3: Ana Lopez
```json
{
  "dni": "38444555",
  "nombre": "Ana",
  "apellido": "Lopez",
  "correo": "ana.lopez@yahoo.com",
  "telefono": "1190123456"
}
```

---

### Cliente 4: Diego Sanchez
```json
{
  "dni": "42666777",
  "nombre": "Diego",
  "apellido": "Sanchez",
  "correo": "diego.sanchez@hotmail.com",
  "telefono": "1101234567"
}
```

---

### Cliente 5: Lucia Romero
```json
{
  "dni": "36888999",
  "nombre": "Lucia",
  "apellido": "Romero",
  "correo": "lucia.romero@gmail.com",
  "telefono": "1112345678"
}
```

---

### Cliente 6: Eduardo Zurbriggen
```json
{
  "dni": "33599902",
  "nombre": "Eduardo",
  "apellido": "zurbriggen",
  "correo": "zurbriggen14@gmail.com",
  "telefono": "3512742684"
}
```

---
## 3. VEHÍCULOS (/api/vehiculos)



### Vehículo 1: Honda Civic (Laura Martinez)
```json
{
  "patente": "DEF456",
  "marca": "Honda",
  "modelo": "Civic",
  "tipoVehiculo": "AUTO",
  "clienteId": 1
}
```

---

### Vehículo 2: Toyota Corolla (Laura Martinez)
```json
{
  "patente": "ABC123",
  "marca": "Toyota",
  "modelo": "Corolla",
  "tipoVehiculo": "AUTO",
  "clienteId": 1
}
```

---


### Vehículo 3: Ford Ranger (Roberto Fernandez)
```json
{
  "patente": "GHI789",
  "marca": "Ford",
  "modelo": "Ranger",
  "tipoVehiculo": "CAMIONETA",
  "clienteId": 2
}
```

---

### Vehículo 4: VW Gol (Ana Lopez)
```json
{
  "patente": "JKL012",
  "marca": "Volkswagen",
  "modelo": "Gol",
  "tipoVehiculo": "AUTO",
  "clienteId": 3
}
```

---

### Vehículo 5: Fiat Cronos (Ana Lopez)
```json
{
  "patente": "MNO345",
  "marca": "Fiat",
  "modelo": "Cronos",
  "tipoVehiculo": "AUTO",
  "clienteId": 3
}
```

---

### Vehículo 6: Chevrolet S10 (Diego Sanchez)
```json
{
  "patente": "PQR678",
  "marca": "Chevrolet",
  "modelo": "S10",
  "tipoVehiculo": "CAMIONETA",
  "clienteId": 4
}
```

---

### Vehículo 7: Peugeot 208 (Lucia Romero)
```json
{
  "patente": "STU901",
  "marca": "Peugeot",
  "modelo": "208",
  "tipoVehiculo": "AUTO",
  "clienteId": 5
}
```

---

### Vehículo 8: Audi S3 (Eduardo Zurbriggen)
```json
{
  "patente": "AH290ZZ",
  "marca": "Audi",
  "modelo": "S3",
  "tipoVehiculo": "AUTO",
  "clienteId": 6
}
```

---


## 4. TURNOS (/api/turnos)

### Turno 1: Laura - Toyota Corolla (PASADO - CONFIRMADO)
```json
{
  "fechaTurno": "2025-10-01",
  "horaTurno": "09:00:00",
  "observaciones": "Primera revision del vehiculo",
  "clienteId": 1,
  "vehiculoId": 1
}
```
**Luego confirmar:** PUT /api/turnos/{turno_id}/confirmar

---

### Turno 2: Roberto - Ford Ranger (PASADO - CONFIRMADO)
```json
{
  "fechaTurno": "2025-10-05",
  "horaTurno": "11:00:00",
  "observaciones": "Revision anual",
  "clienteId": 2,
  "vehiculoId": 3
}
```
**Luego confirmar:** PUT /api/turnos/{turno_id}/confirmar

---

### Turno 3: Ana - VW Gol (PASADO - CONFIRMADO)
```json
{
  "fechaTurno": "2025-10-10",
  "horaTurno": "15:00:00",
  "observaciones": null,
  "clienteId": 3,
  "vehiculoId": 4
}
```
**Luego confirmar:** PUT /api/turnos/{turno_id}/confirmar

---

### Turno 4: Laura - Honda Civic (FUTURO - CONFIRMADO)
```json
{
  "fechaTurno": "2025-10-25",
  "horaTurno": "10:00:00",
  "observaciones": "Vehiculo nuevo, primera VTV",
  "clienteId": 1,
  "vehiculoId": 2
}
```
**Luego confirmar:** PUT /api/turnos/{turno_id}/confirmar

---

### Turno 5: Ana - Fiat Cronos (FUTURO - CONFIRMADO)
```json
{
  "fechaTurno": "2025-10-28",
  "horaTurno": "14:00:00",
  "observaciones": null,
  "clienteId": 3,
  "vehiculoId": 5
}
```
**Luego confirmar:** PUT /api/turnos/{turno_id}/confirmar

---

### Turno 6: Diego - Chevrolet S10 (FUTURO - PENDIENTE)
```json
{
  "fechaTurno": "2025-10-30",
  "horaTurno": "09:30:00",
  "observaciones": "Solicitud reciente",
  "clienteId": 4,
  "vehiculoId": 6
}
```
**NO confirmar - dejar PENDIENTE**

---

### Turno 7: Lucia - Peugeot 208 (FUTURO - PENDIENTE)
```json
{
  "fechaTurno": "2025-11-02",
  "horaTurno": "11:30:00",
  "observaciones": null,
  "clienteId": 5,
  "vehiculoId": 7
}
```
**NO confirmar - dejar PENDIENTE**

---


### Revisión 1: Toyota Corolla - APROBADO
```json
{
  "patente": "ABC123",
  "turnoId": 1,
  "resumen": "Vehículo en excelente estado. Todos los sistemas funcionando correctamente.",
  "itemsChequeo": [
    {"nombreItem": "LUCES",      "puntuacion": 9, "observaciones": "Todas las luces funcionan correctamente"},
    {"nombreItem": "FRENOS",     "puntuacion": 9, "observaciones": "Sistema de frenos en perfecto estado"},
    {"nombreItem": "DIRECCION",  "puntuacion": 8, "observaciones": "Direccion sin holguras"},
    {"nombreItem": "SUSPENSION", "puntuacion": 8, "observaciones": "Amortiguadores en buen estado"},
    {"nombreItem": "NEUMATICOS", "puntuacion": 8, "observaciones": "Profundidad de dibujo adecuada"},
    {"nombreItem": "MOTOR",      "puntuacion": 9, "observaciones": "Motor sin perdidas ni ruidos anormales"},
    {"nombreItem": "ESCAPE",     "puntuacion": 9, "observaciones": "Sistema de escape sin fugas"},
    {"nombreItem": "CHASIS",     "puntuacion": 9, "observaciones": "Estructura sin deformaciones ni corrosion"}
  ],
  "observaciones": "Revisión realizada por inspector; todo aprobado."
}
```

### Revisión 2: - Reprobado (envio de email)
```json
{
    "patente": "AH290ZZ",
    "resumen": "Chequeo rutinario",
    "turnoId": null,
    "observaciones": "Se detectó desgaste importante en frenos",
    "itemsChequeo": [
      {"nombreItem":"Frenos","puntuacion":3,"observaciones":"Pastillas gastadas"},
      {"nombreItem":"Luces","puntuacion":8,"observaciones":""},
      {"nombreItem":"Direccion","puntuacion":7,"observaciones":""},
      {"nombreItem":"Llantas","puntuacion":6,"observaciones":""},
      {"nombreItem":"Embrague","puntuacion":6,"observaciones":""},
      {"nombreItem":"Escape","puntuacion":6,"observaciones":""},
      {"nombreItem":"Suspension","puntuacion":7,"observaciones":""},
      {"nombreItem":"Fugas","puntuacion":7,"observaciones":""}
    ]
  }
```

---

## ========================================
## RESUMEN DE DATOS
## ========================================

### Usuarios: 3
- jperez (ADMIN)
- mgonzalez (ADMINISTRATIVO)
- crodriguez (INSPECTOR)

### Clientes: 5
- Laura Martinez (DNI: 35111222)
- Roberto Fernandez (DNI: 40222333)
- Ana Lopez (DNI: 38444555)
- Diego Sanchez (DNI: 42666777)
- Lucia Romero (DNI: 36888999)

### Vehículos: 7
- 2 de Laura (Toyota, Honda)
- 1 de Roberto (Ford)
- 2 de Ana (VW, Fiat)
- 1 de Diego (Chevrolet)
- 1 de Lucia (Peugeot)

### Turnos: 7
- 3 turnos pasados CONFIRMADOS (para crear revisiones)
- 2 turnos futuros CONFIRMADOS
- 2 turnos futuros PENDIENTES

---

## ========================================
## ORDEN DE CREACIÓN RECOMENDADO
## ========================================

1. **Usuarios** (3) - POST /api/auth/register
2. **Login ADMIN** - POST /api/auth/login (guardar token)
3. **Clientes** (5) - POST /api/clientes (con token ADMIN)
4. **Vehículos** (7) - POST /api/vehiculos (con token ADMIN)
5. **Turnos** (7) - POST /api/turnos (con token ADMIN)
6. **Confirmar turnos** (5) - PUT /api/turnos/{id}/confirmar
7. **Revisiones** (opcionales) - POST /api/revisiones (con token INSPECTOR)

---

## ========================================
## CREDENCIALES PARA TESTING
## ========================================

**Contraseña para todos:** password123

| Usuario | Username | Rol | Email |
|---------|----------|-----|-------|
| Juan Perez | jperez | ADMIN | juan.perez@admin.com |
| Maria Gonzalez | mgonzalez | ADMINISTRATIVO | maria.gonzalez@administrativo.com |
| Carlos Rodriguez | crodriguez | INSPECTOR | carlos.rodriguez@inspeccion.com |

---

## ENDPOINTS SWAGGER
http://localhost:8080/swagger-ui.html

## ========================================
## LOTE ADICIONAL DE DATOS (GENERADO 2025-11-06)
## ========================================

A continuación hay un lote de ejemplo para agregar más registros a la base de datos: clientes, vehículos, turnos y revisiones. Puedes usar los JSON con las rutas REST o las sentencias SQL de ejemplo si prefieres insertar directamente.

---

### Clientes (JSON array)
```json
[
  {"dni":"50111222","nombre":"Marcos","apellido":"Diaz","correo":"marcos.diaz@gmail.com","telefono":"1160000001"},
  {"dni":"50222333","nombre":"Sofia","apellido":"Vega","correo":"sofia.vega@gmail.com","telefono":"1160000002"},
  {"dni":"50333444","nombre":"Pablo","apellido":"Ortiz","correo":"pablo.ortiz@gmail.com","telefono":"1160000003"},
  {"dni":"50444555","nombre":"Marta","apellido":"Suarez","correo":"marta.suarez@gmail.com","telefono":"1160000004"},
  {"dni":"50555666","nombre":"Fernando","apellido":"Iglesias","correo":"fernando.iglesias@gmail.com","telefono":"1160000005"},
  {"dni":"50666777","nombre":"Valeria","apellido":"Marin","correo":"valeria.marin@gmail.com","telefono":"1160000006"},
  {"dni":"50777888","nombre":"Gonzalo","apellido":"Paz","correo":"gonzalo.paz@gmail.com","telefono":"1160000007"},
  {"dni":"50888999","nombre":"Carla","apellido":"Ramos","correo":"carla.ramos@gmail.com","telefono":"1160000008"}
]
```

### Vehículos (JSON array)
```json
[
  {"patente":"AAA111","marca":"Renault","modelo":"Sandero","tipoVehiculo":"AUTO","clienteId":7},
  {"patente":"BBB222","marca":"Honda","modelo":"HR-V","tipoVehiculo":"AUTO","clienteId":8},
  {"patente":"CCC333","marca":"Nissan","modelo":"Kicks","tipoVehiculo":"AUTO","clienteId":9},
  {"patente":"DDD444","marca":"Chevrolet","modelo":"Onix","tipoVehiculo":"AUTO","clienteId":10},
  {"patente":"EEE555","marca":"Ford","modelo":"EcoSport","tipoVehiculo":"CAMIONETA","clienteId":11},
  {"patente":"FFF666","marca":"Volkswagen","modelo":"Voyage","tipoVehiculo":"AUTO","clienteId":12},
  {"patente":"GGG777","marca":"Peugeot","modelo":"Partner","tipoVehiculo":"CAMIONETA","clienteId":13},
  {"patente":"HHH888","marca":"Fiat","modelo":"Argo","tipoVehiculo":"AUTO","clienteId":14}
]
```

### Turnos (JSON array) - ejemplo 12 turnos
```json
[
  {"fechaTurno":"2025-11-10","horaTurno":"09:00:00","observaciones":"Chequeo rapido","clienteId":7,"vehiculoId":9},
  {"fechaTurno":"2025-11-15","horaTurno":"10:00:00","observaciones":"Chequeo rapido","clienteId":8,"vehiculoId":10},
  {"fechaTurno":"2025-11-12","horaTurno":"11:00:00","observaciones":"Chequeo rapido","clienteId":9,"vehiculoId":11},
  {"fechaTurno":"2025-11-17","horaTurno":"12:00:00","observaciones":"Chequeo rapido","clienteId":10,"vehiculoId":12},
  {"fechaTurno":"2025-11-12","horaTurno":"09:00:00","observaciones":"Revision completa","clienteId":11,"vehiculoId":13},
  {"fechaTurno":"2025-10-05","horaTurno":"10:00:00","observaciones":"Revision completa","clienteId":12,"vehiculoId":14},
  {"fechaTurno":"2025-10-22","horaTurno":"11:00:00","observaciones":"Revision completa","clienteId":13,"vehiculoId":16},
  {"fechaTurno":"2025-10-23","horaTurno":"12:00:00","observaciones":"Revision completa","clienteId":14,"vehiculoId":15},
  {"fechaTurno":"2025-12-30","horaTurno":"09:00:00","observaciones":"Turno programado","clienteId":7,"vehiculoId":4},
  {"fechaTurno":"2025-12-12","horaTurno":"10:00:00","observaciones":"Turno programado","clienteId":8,"vehiculoId":6},
  {"fechaTurno":"2025-12-12","horaTurno":"11:00:00","observaciones":"Turno programado","clienteId":9,"vehiculoId":7},
  {"fechaTurno":"2025-12-12","horaTurno":"12:00:00","observaciones":"Turno programado","clienteId":10,"vehiculoId":8}
]
```

### Revisiones (ejemplos)
```json
[
  {"patente":"AAA111","turnoId":1,"resumen":"Frenos OK","itemsChequeo":[{"nombreItem":"FRENOS","puntuacion":9,"observaciones":""}]},
  {"patente":"BBB222","turnoId":2,"resumen":"Necesita rechequeo","itemsChequeo":[{"nombreItem":"FRENOS","puntuacion":3,"observaciones":"Pastillas gastadas"}],"observaciones":"RECHEQUEAR"},
  {"patente":"CCC333","turnoId":3,"resumen":"Aprobado","itemsChequeo":[{"nombreItem":"LUCES","puntuacion":8,"observaciones":""}]}
]
```

### Cómo usar el template de items

1. Obtener la lista de items a chequear (template):

```
GET http://localhost:8080/api/items-chequeo/template
```

Respuesta: array con objetos {"nombreItem": "LUCES", "puntuacion": null, "observaciones": null}

2. Completo puntuacion y texto. De esta forma obtengo los items y no tengo que andar acordandome o inventando cada vez que genero una nueva revision


---
### Revisiones extras
```json
{
  "patente": "AAA111",
  "turnoId": 1,
  "resumen": "Prueba - revision aprobada",
  "itemsChequeo": [
    {"nombreItem":"LUCES","puntuacion":9,"observaciones":""},
    {"nombreItem":"FRENOS","puntuacion":9,"observaciones":""},
    {"nombreItem":"DIRECCION","puntuacion":8,"observaciones":""},
    {"nombreItem":"SUSPENSION","puntuacion":8,"observaciones":""},
    {"nombreItem":"NEUMATICOS","puntuacion":8,"observaciones":""},
    {"nombreItem":"MOTOR","puntuacion":9,"observaciones":""},
    {"nombreItem":"ESCAPE","puntuacion":9,"observaciones":""},
    {"nombreItem":"CHASIS","puntuacion":9,"observaciones":""}
  ],
  "observaciones": "Todo OK"
}
```
---

```json
{
  "patente":"BBB222",
  "turnoId":2,
  "resumen":"Prueba - necesita re-chequeo",
  "itemsChequeo":[
    {"nombreItem":"LUCES","puntuacion":8,"observaciones":"ok"},
    {"nombreItem":"FRENOS","puntuacion":3,"observaciones":"Pastillas gastadas"},
    {"nombreItem":"DIRECCION","puntuacion":7,"observaciones":"pequeño desgaste"},
    {"nombreItem":"SUSPENSION","puntuacion":7,"observaciones":"ok"},
    {"nombreItem":"NEUMATICOS","puntuacion":6,"observaciones":"necesitara recambio en breve"},
    {"nombreItem":"MOTOR","puntuacion":6,"observaciones":"todo ok"},
    {"nombreItem":"ESCAPE","puntuacion":6,"observaciones":"ok"},
    {"nombreItem":"CHASIS","puntuacion":6,"observaciones":"numero correcto"}
  ],
  "observaciones":"Pastillas de freno en mal estado"
}
```
---

