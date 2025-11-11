# Sistema de Gestión de Turnos y Revisiones Vehiculares

## 📋 Descripción del Sistema

Sistema implementado para gestionar turnos de revisión vehicular anual con sistema de puntuación de 8 items.

## 🔧 Funcionalidades Implementadas

### 1. Gestión de Turnos

#### Solicitar un Turno
```http
POST /api/turnos/solicitar
Authorization: Bearer {token}
Content-Type: application/json

{
  "patente": "ABC123",
  "fechaTurno": "2025-10-20",
  "horaTurno": "10:00",
  "observaciones": "Primera revisión del año"
}
```

#### Ver Turnos Disponibles
```http
GET /api/turnos/disponibles
Authorization: Bearer {token}
```

#### Confirmar Turno
```http
PUT /api/turnos/{id}/confirmar
Authorization: Bearer {token}
```

#### Cancelar Turno
```http
PUT /api/turnos/{id}/cancelar
Authorization: Bearer {token}
```

#### Buscar Turnos por Patente
```http
GET /api/turnos/patente/ABC123
Authorization: Bearer {token}
```

---

### 2. Sistema de Revisión con Puntuación

#### Crear Revisión con 8 Items de Chequeo
```http
POST /api/revisiones
Authorization: Bearer {token}
Content-Type: application/json

{
  "patente": "ABC123",
  "turnoId": 1,
  "resumen": "Revisión técnica vehicular anual",
  "itemsChequeo": [
    {
      "nombreItem": "Frenos",
      "puntuacion": 9,
      "observaciones": "En buen estado"
    },
    {
      "nombreItem": "Neumáticos",
      "puntuacion": 8,
      "observaciones": "Desgaste normal"
    },
    {
      "nombreItem": "Luces",
      "puntuacion": 10,
      "observaciones": "Funcionan correctamente"
    },
    {
      "nombreItem": "Dirección",
      "puntuacion": 9,
      "observaciones": "Sin holguras"
    },
    {
      "nombreItem": "Suspensión",
      "puntuacion": 7,
      "observaciones": "Ligero desgaste"
    },
    {
      "nombreItem": "Sistema eléctrico",
      "puntuacion": 10,
      "observaciones": "Todo funcional"
    },
    {
      "nombreItem": "Motor",
      "puntuacion": 8,
      "observaciones": "Sin ruidos anormales"
    },
    {
      "nombreItem": "Carrocería",
      "puntuacion": 9,
      "observaciones": "Buen estado general"
    }
  ],
  "observaciones": "Vehículo en condiciones óptimas para circular"
}
```

**Respuesta:**
```json
{
  "id": 1,
  "usuarioRevisor": "jmartinez",
  "patente": "ABC123",
  "turnoId": 1,
  "fechaRevision": "2025-10-16",
  "resumen": "Revisión técnica vehicular anual",
  "itemsChequeo": [...],
  "puntajeTotal": 70,
  "estadoResultado": "SEGURO",
  "observaciones": "Vehículo en condiciones óptimas para circular"
}
```

#### Ver Revisiones por Patente
```http
GET /api/revisiones/patente/ABC123
Authorization: Bearer {token}
```

#### Ver Revisión por ID
```http
GET /api/revisiones/{id}
Authorization: Bearer {token}
```

---

## 📊 Reglas de Puntuación

### Cálculo Automático

1. **Puntaje Total**: Suma de las 8 puntuaciones (máximo 80 puntos)

2. **Estado del Resultado**:
   - **SEGURO**: Puntaje >= 80 puntos y ningún ítem < 5
   - **RECHEQUEAR**: 
     - Puntaje < 40 puntos, O
     - Algún ítem con puntuación < 5 puntos

3. **Observaciones Obligatorias**:
   - Si el puntaje total es < 40, el campo `observaciones` es obligatorio
   - Debe indicar los problemas encontrados

### Ejemplos de Escenarios

#### ✅ Vehículo SEGURO
- Total: 80 puntos (todos los items ≥ 5)
- Estado: SEGURO
- Observaciones: Opcionales

#### ⚠️ Vehículo RECHEQUEAR (por puntaje bajo)
- Total: 35 puntos
- Estado: RECHEQUEAR
- Observaciones: **OBLIGATORIAS** (debe detallar los problemas)

#### ⚠️ Vehículo RECHEQUEAR (por item crítico)
- Total: 72 puntos
- Frenos: 4 puntos (< 5)
- Estado: RECHEQUEAR
- Observaciones: Recomendadas (detallar problema de frenos)

---

## 🔐 Permisos por Rol

### ADMIN
- ✅ Todos los endpoints
- ✅ Ver todos los turnos
- ✅ Ver todas las revisiones
- ✅ Eliminar revisiones

### MANTENIMIENTO
- ✅ Crear revisiones
- ✅ Ver sus propias revisiones
- ✅ Ver todos los turnos
- ✅ Ver todas las revisiones

### ADMINISTRATIVO
- ✅ Solicitar turnos
- ✅ Confirmar/cancelar turnos
- ✅ Ver turnos por patente
- ✅ Ver revisiones por patente

### Cualquier Usuario Autenticado
- ✅ Solicitar turnos
- ✅ Ver turnos disponibles
- ✅ Ver sus propios turnos

---

## 🧪 Casos de Prueba

### Test 1: Vehículo Aprobado
```json
{
  "patente": "XYZ789",
  "resumen": "Revisión anual - Vehículo en excelente estado",
  "itemsChequeo": [
    {"nombreItem": "Frenos", "puntuacion": 10},
    {"nombreItem": "Neumáticos", "puntuacion": 10},
    {"nombreItem": "Luces", "puntuacion": 10},
    {"nombreItem": "Dirección", "puntuacion": 10},
    {"nombreItem": "Suspensión", "puntuacion": 10},
    {"nombreItem": "Sistema eléctrico", "puntuacion": 10},
    {"nombreItem": "Motor", "puntuacion": 10},
    {"nombreItem": "Carrocería", "puntuacion": 10}
  ]
}
```
**Resultado**: SEGURO (80 puntos)

### Test 2: Vehículo Rechazado por Puntaje Bajo
```json
{
  "patente": "DEF456",
  "resumen": "Revisión anual - Múltiples problemas detectados",
  "itemsChequeo": [
    {"nombreItem": "Frenos", "puntuacion": 5, "observaciones": "Pastillas desgastadas"},
    {"nombreItem": "Neumáticos", "puntuacion": 3, "observaciones": "Neumáticos lisos"},
    {"nombreItem": "Luces", "puntuacion": 6},
    {"nombreItem": "Dirección", "puntuacion": 4, "observaciones": "Holgura en volante"},
    {"nombreItem": "Suspensión", "puntuacion": 3, "observaciones": "Amortiguadores gastados"},
    {"nombreItem": "Sistema eléctrico", "puntuacion": 5},
    {"nombreItem": "Motor", "puntuacion": 6},
    {"nombreItem": "Carrocería", "puntuacion": 5}
  ],
  "observaciones": "RECHEQUEAR: Neumáticos lisos críticos, suspensión deficiente, dirección con holgura. Requiere reparación urgente."
}
```
**Resultado**: RECHEQUEAR (37 puntos) - Observaciones obligatorias incluidas

### Test 3: Vehículo Rechazado por Item Crítico
```json
{
  "patente": "GHI123",
  "resumen": "Revisión anual - Problema crítico en frenos",
  "itemsChequeo": [
    {"nombreItem": "Frenos", "puntuacion": 3, "observaciones": "CRÍTICO: Frenos muy desgastados"},
    {"nombreItem": "Neumáticos", "puntuacion": 9},
    {"nombreItem": "Luces", "puntuacion": 10},
    {"nombreItem": "Dirección", "puntuacion": 9},
    {"nombreItem": "Suspensión", "puntuacion": 8},
    {"nombreItem": "Sistema eléctrico", "puntuacion": 10},
    {"nombreItem": "Motor", "puntuacion": 9},
    {"nombreItem": "Carrocería", "puntuacion": 10}
  ],
  "observaciones": "A pesar del buen estado general, los frenos requieren atención inmediata"
}
```
**Resultado**: RECHEQUEAR (68 puntos) - Por item crítico < 5

---

## 🚀 Flujo Completo de Uso

1. **Usuario solicita turno** con su patente
2. **Sistema muestra turnos disponibles** para selección
3. **Usuario confirma** el turno seleccionado
4. **Personal de mantenimiento realiza la revisión**:
   - Completa los 8 items de chequeo
   - El sistema calcula automáticamente el puntaje
   - El sistema determina SEGURO o RECHEQUEAR
   - Si requiere rechequeo y puntaje < 40, valida observaciones
5. **El turno se marca como REALIZADO**
6. **El usuario puede consultar** el resultado por patente

---

## 📝 Notas Importantes

- Los 8 items de chequeo son **obligatorios**
- Cada item debe puntuarse de **1 a 10**
- Si el puntaje total es **< 40**, las observaciones son **obligatorias**
- Un turno confirmado se marca automáticamente como **REALIZADO** al crear la revisión
- Las revisiones están vinculadas opcionalmente a un turno
- Solo usuarios con rol **INSPECTOR** o **ADMIN** pueden crear revisiones
