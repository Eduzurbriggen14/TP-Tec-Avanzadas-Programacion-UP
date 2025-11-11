# Dockerfile multi-etapa: compila con Maven y ejecuta con JRE 17

FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copiar pom y descargar dependencias primero (capa cacheable)
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copiar el código fuente y compilar el paquete
COPY src src
RUN mvn -B -DskipTests package

# Imagen de ejecución
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY --from=build /workspace/target/*.jar app.jar

# Puerto expuesto por la aplicación Spring Boot
EXPOSE 8080

# Comando por defecto: ejecutar el JAR
ENTRYPOINT ["java","-jar","/app/app.jar"]