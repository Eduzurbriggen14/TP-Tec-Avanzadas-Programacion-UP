# Guía de Seguridad - Sistema de Gestión de Vehículos

## 🔐 Resumen de Seguridad

El sistema implementa autenticación **JWT (JSON Web Tokens)** con **Spring Security** y control de acceso basado en **roles**.

## 🎭 Roles y Permisos

### 1. ADMIN (Administrador)
- ✅ **Acceso completo** a todos los recursos
- ✅ Crear, editar y eliminar **usuarios**
- ✅ Crear, editar y eliminar **vehículos**
- ✅ Gestionar **clientes**
- ✅ Gestionar **mantenimientos** y **revisiones**

### 2. ADMINISTRATIVO
- ✅ Ver usuarios (no crear/eliminar)
- ✅ Gestionar **clientes** (CRUD completo)
- ✅ Ver **vehículos** (solo lectura)

### 3. INSPECTOR
- ✅ Crear y gestionar **revisiones de vehículos**
- ✅ Crear y gestionar **mantenimientos**
- ✅ Ver **vehículos** (solo lectura)
- ❌ No puede gestionar clientes
- ❌ No puede gestionar usuarios

## 🔑 Flujo de Autenticación

### 1. Registro de Usuario

```bash
POST /api/auth/register
Content-Type: application/json

{
  "nombre": "Juan",
  "apellido": "Pérez",
  "correo": "juan@example.com",
  "telefono": "123456789",
  "userName": "jperez",
  "passwd": "password123",
  "rol": "ADMIN",
  "activo": true
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqcGVyZXoiLCJpYXQiOjE2OTczNjAwMDAsImV4cCI6MTY5NzQ0NjQwMH0.signature",
  "type": "Bearer",
  "userName": "jperez",
  "rol": "ADMIN",
  "nombre": "Juan",
  "apellido": "Pérez"
}
```

### 2. Iniciar Sesión

```bash
POST /api/auth/login
Content-Type: application/json

{
  "userName": "jperez",
  "password": "password123"
}
```

**Respuesta:** Igual que el registro

### 3. Usar el Token

Agrega el token en el header **Authorization** de todas las peticiones:

```bash
GET /api/vehiculos
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 🛡️ Características de Seguridad

### Encriptación de Contraseñas
- ✅ Todas las contraseñas se encriptan con **BCrypt**
- ✅ No se almacenan contraseñas en texto plano
- ✅ Hash con factor de coste 10

### Tokens JWT
- ✅ Firmados con algoritmo **HS256**
- ✅ Duración: **24 horas** (86400000 ms)
- ✅ Contienen: username, fecha de emisión, fecha de expiración
- ✅ Secreto configurable en `application.properties`

### Protección de Endpoints
- ✅ **CORS** habilitado para permitir peticiones desde frontends
- ✅ **CSRF** deshabilitado (API REST stateless)
- ✅ Sesiones **stateless** (sin estado en servidor)
- ✅ Validación automática de tokens en cada petición


## 🔒 Buenas Prácticas Implementadas

### 1. Validación del Token
- El filtro `JwtAuthenticationFilter` valida cada petición
- Extrae el username del token
- Verifica que el token no esté expirado
- Valida la firma del token

### 2. Manejo de Errores de Autenticación
- Respuestas estandarizadas con `JwtAuthenticationEntryPoint`
- HTTP 401 para accesos no autorizados
- Mensajes claros de error

### 3. Inyección de Dependencias Segura
- Uso de `@RequiredArgsConstructor` de Lombok
- Dependencias finales e inmutables
- No exposición de componentes internos

### 4. Configuración Externalizada
- Secret JWT configurable por environment
- Tiempo de expiración configurable
- Perfiles separados (dev/prod)

## 🧪 Testing de Seguridad

### Probar Endpoint Sin Token

```bash
curl -X GET http://localhost:8080/api/vehiculos
```

**Respuesta esperada:** HTTP 401 Unauthorized

### Probar con Token Inválido

```bash
curl -X GET http://localhost:8080/api/vehiculos \
  -H "Authorization: Bearer token_invalido"
```

**Respuesta esperada:** HTTP 401 Unauthorized

### Probar con Token Válido

```bash
# Primero hacer login
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userName":"admin","password":"admin123"}' \
  | jq -r '.token')

# Usar el token
curl -X GET http://localhost:8080/api/vehiculos \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:** HTTP 200 OK con datos

### Probar Permisos por Rol

```bash
# Usuario INSPECTOR intentando gestionar alquileres
curl -X GET http://localhost:8080/api/alquileres \
  -H "Authorization: Bearer $TOKEN_INSPECTOR"
```

**Respuesta esperada:** HTTP 403 Forbidden

## 📝 Configuración

### application.properties

```properties
# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000

# Security
spring.security.filter.order=5
```

### Variables de Entorno (Producción)

```bash
export JWT_SECRET=tu_secreto_muy_largo_y_seguro_aqui
export JWT_EXPIRATION=86400000
```

## 🚨 Consideraciones de Seguridad en Producción

1. **Cambiar el JWT Secret** en producción
2. Usar **HTTPS** siempre
3. Implementar **refresh tokens** para sesiones largas
4. Agregar **rate limiting** para prevenir ataques de fuerza bruta
5. Implementar **logout** con blacklist de tokens
6. Monitorear intentos fallidos de autenticación
7. Implementar **2FA** (autenticación de dos factores) para usuarios admin

## 📚 Referencias

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/)
- [OWASP Security Best Practices](https://owasp.org/)

---

**Nota:** Este documento describe el sistema de seguridad implementado. Para uso en producción, considera implementar las mejoras sugeridas.
