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


## Endpoints y verificación rápida
- Login (obtener token): `POST /api/auth/login` (usuario/clave)
- Slots disponibles (date-only):

```
GET /api/turnos/disponibles?fecha=YYYY-MM-DD&includeOccupied=true
Authorization: Bearer <JWT>
```

## Tests
- Ejecutar pruebas unitarias y de integración locales:

```bash
mvn test
```

## ☁️ Despliegue en AWS (Recomendado: ECS + Fargate)


### Componentes de AWS
* **ECR (Elastic Container Registry):** Un registro de Docker privado (como Docker Hub) para alojar tu imagen `gestion-vehiculos:latest`.
* **RDS (Relational Database Service):** Una base de datos MySQL gestionada. **Crítico:** No uses la DB de Docker Compose en producción.
* **ECS (Elastic Container Service):** El servicio que orquesta (inicia, detiene, monitorea) tus contenedores.
* **Fargate:** Un motor de cómputo "serverless" para ECS, para que no tengas que administrar las máquinas virtuales (EC2) subyacentes.
* **AWS Secrets Manager:** El lugar seguro para guardar tu `JWT_SECRET` y las credenciales de la base de datos.

---

### a. Pasos de Despliegue Manual (Primera vez)

1.  **Crear Base de Datos (RDS):**
    * Ve al servicio RDS en la consola de AWS.
    * Crea una base de datos (ej. MySQL).
    * **Importante:** Configura el "Security Group" de esta base de datos para permitir tráfico entrante en el puerto `3306` desde el Security Group de tu futuro servicio ECS.
    * Guarda el **host (endpoint)**, **usuario** y **contraseña**.

2.  **Guardar Secretos (Secrets Manager):**
    * Ve a AWS Secrets Manager.
    * Crea un nuevo secreto (Elige "Otro tipo de secretos").
    * Crea claves-valor para:
        * `SPRING_DATASOURCE_URL`: (El endpoint de tu RDS, ej: `jdbc:mysql://tu-rds-host.aws.com:3306/gestion_vehiculos`)
        * `SPRING_DATASOURCE_USERNAME`: (Tu usuario de RDS)
        * `SPRING_DATASOURCE_PASSWORD`: (Tu contraseña de RDS)
        * `JWT_SECRET`: (Tu clave JWT larga y segura)

3.  **Crear Repositorio de Imagen (ECR):**
    * Ve al servicio ECR.
    * Crea un nuevo repositorio (ej. `gestion-vehiculos`).
    * Toma nota de la **URI** del repositorio (se verá como `TU_AWS_ID.dkr.ecr.REGION.amazonaws.com/gestion-vehiculos`).

4.  **Subir la Imagen Docker a ECR (desde tu PC):**
    ```bash
    # 1. Autentica tu CLI de Docker con ECR
    aws ecr get-login-password --region <tu-region> | docker login --username AWS --password-stdin <TU_AWS_ID.dkr.ecr.REGION.amazonaws.com>
    
    # 2. Construye tu imagen (asegúrate de usar el Dockerfile multi-stage)
    docker build -t gestion-vehiculos:latest .
    
    # 3. Etiqueta la imagen para ECR
    docker tag gestion-vehiculos:latest <TU_URI_DE_ECR>:latest
    
    # 4. Súbela
    docker push <TU_URI_DE_ECR>:latest
    ```

  ### Guía rápida: comandos útiles desde tu máquina

  Si querés un conjunto de comandos directo para CI/manual deploy:

  ```bash
  # 1) Autenticar Docker en ECR
  aws ecr get-login-password --region <tu-region> | docker login --username AWS --password-stdin <TU_AWS_ID>.dkr.ecr.<tu-region>.amazonaws.com

  # 2) Construir y etiquetar (usa el commit hash como tag en CI)
  IMAGE_TAG=$(git rev-parse --short HEAD)
  docker build -t gestion-vehiculos:${IMAGE_TAG} .
  docker tag gestion-vehiculos:${IMAGE_TAG} <TU_URI_DE_ECR>:${IMAGE_TAG}

  # 3) Push
  docker push <TU_URI_DE_ECR>:${IMAGE_TAG}

  # 4) (Opcional) actualizar servicio ECS para forzar redeploy con nueva imagen
  aws ecs update-service --cluster <ECS_CLUSTER_NAME> --service <ECS_SERVICE_NAME> --force-new-deployment --region <tu-region>
  ```

  Notas:
  - En lugar de `--force-new-deployment` es más limpio crear una nueva task definition con la nueva imagen y actualizar el servicio.
  - Asegúrate que la Task Role permita leer secretos desde Secrets Manager si usás secretos.


5.  **Crear el Servicio (ECS):**
    * Ve al servicio ECS y crea un nuevo **"Cluster"** (puedes usar el template "Networking only" para Fargate).
    * Crea una nueva **"Task Definition" (Definición de Tarea):**
        * Elige **Fargate**.
        * **Rol de la Tarea:** Asegúrate que tenga permiso para leer los secretos de Secrets Manager.
        * Añade un "Contenedor":
            * **Imagen:** La URL de tu imagen en ECR (`<TU_URI_DE_ECR>:latest`).
            * **Mapeo de Puerto:** `8080`.
            * **Variables de Entorno:** En la sección "Secrets", enlaza el secreto que creaste en el paso 2.
    * Crea un **"Servicio"** usando la "Task Definition" que acabas de crear:
        * Configura la Red (VPC, subnets) y el Security Group (asegúrate que permita entrar tráfico en el puerto `8080`).
        * **Recomendado:** Configura un "Application Load Balancer" (ALB) para que dirija el tráfico al puerto `8080` de tu servicio.


## CI/CD (GitHub Actions) — Despliegue en AWS ECS

Este workflow automatiza la construcción de la imagen Docker y el despliegue en AWS ECS cada vez que se hace un `push` a la rama `main`.

**Requisito:** Debes configurar los siguientes "Secrets" en tu repositorio de GitHub (Settings > Secrets and variables > Actions):
* `AWS_ACCESS_KEY_ID`: Clave de acceso de un usuario IAM con permisos para ECR y ECS.
* `AWS_SECRET_ACCESS_KEY`: La clave secreta del usuario IAM.
* `AWS_REGION`: La región donde estás desplegando (ej. `us-east-1`).
* `ECR_REPOSITORY_URI`: El nombre de tu repositorio ECR (ej. `gestion-vehiculos`).
* `ECS_CLUSTER_NAME`: El nombre de tu cluster ECS.
* `ECS_SERVICE_NAME`: El nombre de tu servicio ECS.
* `ECS_TASK_DEFINITION_NAME`: El nombre de tu Task Definition (ej. `gestion-vehiculos-task`).

**Archivo `.github/workflows/deploy-aws.yml`:**

```yaml
name: Desplegar en AWS ECS

on:
  push:
    branches:
      - main

jobs:
  build-and-deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout del repositorio
        uses: actions/checkout@v3

      - name: Configurar credenciales de AWS
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ${{ secrets.AWS_REGION }}

      - name: Iniciar sesión en Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v1

      - name: Construir, etiquetar y subir imagen a ECR
        id: build-image
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          ECR_REPOSITORY: ${{ secrets.ECR_REPOSITORY_URI }}
          IMAGE_TAG: ${{ github.sha }} # Usa el hash del commit como tag
        run: |
          docker build -t $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG .
          docker push $ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG
          # Exportar variable de salida moderna
          echo "image=$ECR_REGISTRY/$ECR_REPOSITORY:$IMAGE_TAG" >> $GITHUB_OUTPUT

      - name: Descargar definición de tarea ECS existente
        run: |
          aws ecs describe-task-definition --task-definition ${{ secrets.ECS_TASK_DEFINITION_NAME }} --query taskDefinition > task-definition.json

      - name: Actualizar definición de tarea con nueva imagen
        id: task-def
        uses: aws-actions/amazon-ecs-render-task-definition@v1
        with:
          task-definition: task-definition.json
          container-name: gestion-vehiculos-app #o el nombre que tenga
          image: ${{ steps.build-image.outputs.image }}

      - name: Registrar nueva definición de tarea y desplegar
        uses: aws-actions/amazon-ecs-deploy-task-definition@v1
        with:
          task-definition: ${{ steps.task-def.outputs.task-definition }}
          service: ${{ secrets.ECS_SERVICE_NAME }}
          cluster: ${{ secrets.ECS_CLUSTER_NAME }}
          wait-for-service-stability: true
