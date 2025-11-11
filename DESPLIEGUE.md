# Gestión Vehículos — Despliegue y operación

Este repositorio contiene la aplicación de gestión de turnos y revisiones vehiculares (Spring Boot, Maven).

Este README cubre:
- Requisitos
- Cómo compilar y ejecutar (jar)
- Despliegue con Docker y Docker Compose
- Variables de entorno y ejemplo `.env`
- Ejemplo `systemd` para Linux
- Seguridad y consideraciones de producción
- Pruebas rápidas y verificación de endpoints

---

## Requisitos
- Java 17+
- Maven 3.6+
- Docker & Docker Compose (opcional para despliegue en contenedores)
- MySQL/MariaDB (o usar el servicio en docker-compose incluido)

## Build local (jar)
1. Compilar el proyecto (sin tests para un build rápido):

```bash
mvn -DskipTests package
```

2. El artefacto resultante estará en `target/` (por ejemplo `gestion-vehiculos-1.0.0.jar`).

3. Lanzar la aplicación localmente:

```bash
# variables de entorno recomendadas (ver sección .env)
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/gestion_vehiculos?useSSL=false&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="secret"
export JWT_SECRET="<una-clave-secreta-larga-base64>"

java -jar target/gestion-vehiculos-1.0.0.jar
```

La app escuchará por defecto en el puerto `8080`.

## Variables de entorno / configuración
La aplicación usa `application.properties` y puede sobreescribirse con variables de entorno. Ejemplo mínimo (`.env.example` se sugiere):

```
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/gestion_vehiculos?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=secret
SPRING_JPA_HIBERNATE_DDL_AUTO=update
JWT_SECRET=<valor-secreto-muy-largo>
SPRING_PROFILES_ACTIVE=prod
```

## Docker: Dockerfile (ejemplo)
Puedes construir una imagen Docker con un `Dockerfile` como:

```Dockerfile
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

Construir y ejecutar:

```bash
docker build -t gestion-vehiculos:latest .
docker run -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/gestion_vehiculos" -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=secret -p 8080:8080 gestion-vehiculos:latest
```

## Docker Compose (ejemplo)
Un `docker-compose.yml` simple que incluye MySQL y MailHog:

```yaml
version: '3.8'
services:
  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: secret
      MYSQL_DATABASE: gestion_vehiculos
    volumes:
      - db-data:/var/lib/mysql
    ports:
      - "3306:3306"

  mailhog:
    image: mailhog/mailhog
    ports:
      - "1025:1025"
      - "8025:8025"

  app:
    build: .
    depends_on:
      - db
      - mailhog
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/gestion_vehiculos?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: secret
      JWT_SECRET: "replace-with-a-strong-secret"
    ports:
      - "8080:8080"

volumes:
  db-data:
```

Arrancar todo:

```bash
docker-compose up --build -d
```

Visitar MailHog UI: http://localhost:8025

## Systemd unit example (Linux)
Crear `/etc/systemd/system/gestion-vehiculos.service`:

```
[Unit]
Description=Gestion Vehiculos Spring Boot App
After=syslog.target
After=network.target

[Service]
User=appuser
WorkingDirectory=/opt/gestion-vehiculos
ExecStart=/usr/bin/java -jar /opt/gestion-vehiculos/gestion-vehiculos-1.0.0.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10
Environment=SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gestion_vehiculos
Environment=SPRING_DATASOURCE_USERNAME=root
Environment=SPRING_DATASOURCE_PASSWORD=secret
Environment=JWT_SECRET=<secreto>

[Install]
WantedBy=multi-user.target
```

Habilitar y arrancar:

```bash
sudo systemctl daemon-reload
sudo systemctl enable gestion-vehiculos
sudo systemctl start gestion-vehiculos
sudo journalctl -u gestion-vehiculos -f
```

## CI/CD (GitHub Actions) — outline
- Workflow que corra `mvn -B -DskipTests package`.
- Ejecutar unit tests `mvn test` en PRs.
- Construir y publicar imagen Docker si merge a main.

Si quieres, puedo generar el `workflow` automáticamente.

## Consideraciones de seguridad y producción
- Generar y mantener `JWT_SECRET` seguro (no en el repositorio). Usar vault o secrets manager.
- Poner TLS en el proxy (NGINX) o en la cloud provider.
- Cambiar `spring.jpa.hibernate.ddl-auto` a `validate` o `none` en producción.
- Desactivar `spring.jpa.show-sql` en producción.
- Backups periódicos de la base de datos.
- Limitar tamaño/petición y aplicar rate limiting si es necesario.
- Revisar CORS y endpoints expuestos.

## Endpoints y verificación rápida
- Login (obtener token): `POST /api/auth/login` (usuario/clave)
- Slots disponibles (date-only):

```
GET /api/turnos/disponibles?fecha=YYYY-MM-DD&includeOccupied=true
Authorization: Bearer <JWT>
```

Ejemplo curl (requiere obtener JWT primero):

```bash
curl -H "Authorization: Bearer $TOKEN" "http://localhost:8080/api/turnos/disponibles?fecha=2025-11-11&includeOccupied=true"
```

## Troubleshooting
- Logs: `docker logs <container>` o `journalctl -u gestion-vehiculos`.
- Conexión DB: revisar `SPRING_DATASOURCE_URL` y credenciales.
- Si la app no arranca: revisar excepciones en logs (puede ser falta de migraciones o credenciales incorrectas).

## Tests
- Ejecutar pruebas unitarias y de integración locales:

```bash
mvn test
```

## Próximos pasos (opcional, puedo generarlos):
- `.env.example` y `Dockerfile` + `docker-compose.yml` completos.
- `systemd` unit y GitHub Actions workflow completados.
- Test unitario de `TurnoServiceImpl.obtenerSlotsConEstado`.

---

Si querés, genero ahora los archivos auxiliares (`.env.example`, `Dockerfile`, `docker-compose.yml` y un workflow GitHub Actions). Dime cuáles querés que cree automáticamente y los añado al repo.
