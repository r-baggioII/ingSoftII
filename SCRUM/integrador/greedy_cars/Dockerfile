# Dockerfile para greedy_cars
FROM openjdk:17-slim

# Argumento para especificar el archivo WAR
ARG WAR_FILE=target/greedy_cars.war

# Directorio de trabajo
WORKDIR /app

# Copiar el archivo WAR
COPY ${WAR_FILE} app.war

# Exponer el puerto
EXPOSE 9000

# Comando de arranque
ENTRYPOINT ["java", "-jar", "/app/app.war"]