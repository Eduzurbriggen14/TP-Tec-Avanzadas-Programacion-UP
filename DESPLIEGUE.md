# Gestión Vehículos — Despliegue y operación

Este documento explica cómo compilar, ejecutar y desplegar la aplicación "Gestion Vehículos" (Spring Boot, Maven). Incluye instrucciones para desarrollo local (jar), contenedores (Docker / Docker Compose), ejecución como servicio en Linux (systemd) y opciones de CI/CD y despliegue en la nube.

Contenido resumido
- Requisitos
- Build local (jar) y ejecución
- Variables de entorno y `.env.example`
- Docker: Dockerfile recomendado
- Docker Compose (mejoras)
- Systemd (ejemplo seguro)
- CI/CD: recomendaciones y workflow (resumen)
- Despliegue en AWS (ECR + ECS/Fargate) — guía rápida
- Seguridad, migraciones, monitoring y checklist para producción

---

## Requisitos
- Java 17+
- Maven 3.6+
- Docker (20+) y Docker Compose (v2+ / compose plugin) para desarrollo con contenedores
- MySQL (o usar el servicio de `docker-compose` incluido)

## Build local (jar)

1) Compilar (rápido, sin tests):

```bash
mvn -DskipTests package
```

2) Compilar con tests (CI / verificación):

```bash
mvn -B -U clean verify
```

3) Ejecutar el jar localmente (ejemplo con opciones de JVM y perfil):

```bash
export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/gestion_vehiculos?useSSL=false&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME="root"
export SPRING_DATASOURCE_PASSWORD="secret"
export JWT_SECRET="<una-clave-secreta-larga-base64>"
export SPRING_PROFILES_ACTIVE=prod

JAVA_OPTS="-Xms512m -Xmx1g -Duser.timezone=UTC"
java $JAVA_OPTS -jar target/gestion-vehiculos-1.0.0.jar
```

La aplicación escucha por defecto en el puerto `8080`.

## Variables de entorno / `.env.example`

- Archivo ` .env.example` en el repo con variables de ejemplo, NUNCA commits con valores reales.
- En producción usa un gestor de secretos (AWS Secrets Manager, HashiCorp Vault, GitHub Secrets, etc.).

Ejemplo mínimo (`.env.example`):

```
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/gestion_vehiculos?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=secret
SPRING_JPA_HIBERNATE_DDL_AUTO=validate
JWT_SECRET=<valor-secreto-muy-largo>
SPRING_PROFILES_ACTIVE=prod
```

Nota: en producción evita `hibernate.ddl-auto=update`; usa migraciones (Flyway/Liquibase).

## Docker — Dockerfile recomendado (multi-stage, seguro)

Recomiendo usar un Dockerfile multi-stage para que la imagen final sea más pequeña y reproducible, y ejecutar como usuario no-root:

```dockerfile
# Build stage
FROM maven:3.9.0-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy
RUN groupadd -r app && useradd -r -g app app
WORKDIR /app
ARG JAR_FILE=target/gestion-vehiculos-*.jar
COPY --from=build /workspace/${JAR_FILE} app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

Beneficios: menor tamaño de imagen, cache más eficiente durante CI/CD y ejecución con menor privilegio (más seguro).


### Construir y ejecutar localmente

```bash
docker build -t gestion-vehiculos:latest .
docker run --rm -e SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/gestion_vehiculos" -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=secret -p 8080:8080 gestion-vehiculos:latest
```

## Docker Compose (mejorado para desarrollo)

Ejemplo con healthchecks y reinicios automáticos (sólo para desarrollo):

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
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 30s
      timeout: 10s
      retries: 5

  mailhog:
    image: mailhog/mailhog
    ports:
      - "1025:1025"
      - "8025:8025"

  app:
    build: .
    depends_on:
      db:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/gestion_vehiculos?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: secret
      JWT_SECRET: "replace-with-a-strong-secret"
    ports:
      - "8080:8080"
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 5
    restart: unless-stopped

volumes:
  db-data:
```

Notas:
- No expongas el puerto de la DB en entornos de producción.
- Usa `depends_on` para ordenar arranque en dev; en producción usa readiness/health probes del orquestador.

Arrancar (desarrollo):

```bash
docker compose up --build -d
```

## Systemd (ejemplo más seguro para Linux)

Usar un archivo de entorno separado evita poner secrets en el unit file. Crea `/etc/default/gestion-vehiculos` con variables y permisos restringidos.

`/etc/systemd/system/gestion-vehiculos.service` ejemplo:

```
[Unit]
Description=Gestion Vehiculos Spring Boot App
After=network.target

[Service]
User=app
WorkingDirectory=/opt/gestion-vehiculos
EnvironmentFile=/etc/default/gestion-vehiculos
ExecStart=/usr/bin/java $JAVA_OPTS -jar /opt/gestion-vehiculos/gestion-vehiculos-1.0.0.jar
Restart=on-failure
RestartSec=10
LimitNOFILE=65536
TimeoutStartSec=120
ProtectSystem=full
ProtectHome=yes
PrivateTmp=yes

[Install]
WantedBy=multi-user.target
```

Ejemplo `/etc/default/gestion-vehiculos` (fuera del repo, permisos 600):

```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/gestion_vehiculos
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=secret
JWT_SECRET=...
JAVA_OPTS="-Xms512m -Xmx1g -Duser.timezone=UTC"
```

## CI/CD — recomendaciones prácticas

- En PRs: ejecutar `mvn -B -U clean verify` (unit + integration tests). Usar Testcontainers para tests que necesiten DB.
- En `main` (merge): ejecutar pipeline que haga: build+tests → construir imagen Docker (tag por SHA) → escanear imagen (Trivy) → push a registry (GHCR/ECR) → desplegar (staging/prod) con aprobación para producción.

Ejemplo breve del flujo de Actions:
- jobs: build (setup-java 17, mvn verify), docker-build (buildx), scan (Trivy), push (solo main), deploy (si aplica).

Para publicar imágenes recomendamos GHCR (GitHub Container Registry) o ECR (si vas a AWS). Configurar secrets en GitHub repo: `GHCR_TOKEN` o `AWS_*` según corresponda.

## Despliegue en AWS (ECR + ECS/Fargate) — guía rápida

Resumen de pasos:
1. Crear RDS (MySQL) y snapshots/backup.
2. Guardar secrets en AWS Secrets Manager.
3. Crear repositorio ECR y subir imagen etiquetada por SHA.
4. Crear Task Definition en ECS (Fargate) y usar Secrets Manager para variables sensibles.
5. Crear Service con ALB y configurar health checks.

En CI: autenticar con ECR, build, tag con `${{ github.sha }}`, push y actualizar task definition (o usar `amazon-ecs-deploy-task-definition` action).

## Seguridad, migraciones y observabilidad

- Migraciones: no uses `hibernate.ddl-auto=update` en producción. Añade Flyway o Liquibase y mantén scripts versionados.
- Secrets: rotación periódica, no hardcodear, usar IAM roles o secrets managers.
- TLS: termina TLS en el ALB o reverse-proxy; no expongas la app directamente sin TLS.
- Logs: centraliza logs (CloudWatch, ELK) y configura niveles de logging adecuados.
- Monitoring: exponer Actuator (`/actuator/health`, `/actuator/prometheus`) y añadir alertas.
- Scanning: escanear imágenes con Trivy o similar en el pipeline.

## Tests y entorno CI

- Usa Testcontainers para tests de integración que requieren MySQL.
- Divide jobs en CI para rapidez: unit-tests (rápidos) e integration-tests (más lentos).

## Checklist antes de pasar a producción

- [ ] Usar gestor de secretos para JWT y credenciales (NO .env con secretos en repo)
- [ ] Añadir Flyway / Liquibase y probar migraciones en staging
- [ ] Configurar health/readiness probes y políticas de restart
- [ ] Ejecutar integración con Testcontainers en CI
- [ ] Escanear imágenes con Trivy antes de push
- [ ] Etiquetar imágenes con SHA y política de retención
- [ ] Configurar TLS en ALB / reverse proxy
- [ ] Definir backup policy para la base de datos

---

Si querés, aplico estas mejoras directamente en el repo (ya lo hice), o genero una versión más corta del archivo si preferís un README más breve. ¿Querés que deje además ejemplos concretos de GitHub Actions para GHCR o ECR dentro del MD?

