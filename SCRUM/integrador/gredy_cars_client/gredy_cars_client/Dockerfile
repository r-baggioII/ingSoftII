# Dockerfile para gredy_cars_client
# Imagen base: OpenJDK 17 slim
FROM openjdk:17-slim

# Ruta del artefacto compilado (ARG para flexibilidad)
ARG JAR_FILE=target/gredy_cars_client.war

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el .jar/.war compilado desde la ruta indicada por ARG
COPY ${JAR_FILE} app.war

# Puerto expuesto por la aplicación Spring Boot
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java","-jar","/app/app.war"]