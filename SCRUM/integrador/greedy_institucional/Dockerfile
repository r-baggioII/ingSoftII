# Dockerfile para greedy_institucional
FROM openjdk:17-slim

# Argumento para especificar el archivo WAR
ARG WAR_FILE=target/greedy_institucional.war

# Directorio de trabajo
WORKDIR /app

# Copiar el archivo WAR
COPY ${WAR_FILE} app.war

# Exponer el puerto (usará puerto 8080 por defecto de Spring Boot)
EXPOSE 8080

# Comando de arranque
ENTRYPOINT ["java", "-jar", "/app/app.war"]