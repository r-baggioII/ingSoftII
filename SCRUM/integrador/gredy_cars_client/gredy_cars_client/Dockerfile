# Dockerfile para gredy_cars_client
# Imagen base: Eclipse Temurin 17 JRE (Alpine para menor tamaño)
FROM eclipse-temurin:17-jre-alpine

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