#!/bin/bash

# Script para construir y desplegar todas las aplicaciones con Docker
set -e

echo "🏗️  Iniciando construcción y despliegue de aplicaciones Greedy Cars..."

# Verificar si Docker y Docker Compose están instalados
if ! command -v docker &> /dev/null; then
    echo "❌ Docker no está instalado. Por favor, instale Docker primero."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose no está instalado. Por favor, instale Docker Compose primero."
    exit 1
fi

# Verificar si existe el archivo .env
if [ ! -f .env ]; then
    echo "⚠️  Archivo .env no encontrado. Creando desde .env.example..."
    cp .env.example .env
    echo "📝 Por favor, edite el archivo .env con sus configuraciones antes de continuar."
    echo "   Variables importantes a configurar:"
    echo "   - MERCADOPAGO_ACCESS_TOKEN"
    echo "   - JWT_SECRET"
    echo "   - SPRING_MAIL_PASSWORD"
    read -p "Presione Enter después de configurar el archivo .env..."
fi

# Compilar las aplicaciones Java
echo "🔨 Compilando aplicaciones Java..."

echo "   - Compilando greedy_cars..."
cd greedy_cars
if [ ! -f "target/greedy_cars.war" ]; then
    mvn clean package -DskipTests
else
    echo "     WAR ya existe, omitiendo compilación."
fi
cd ..

echo "   - Compilando greedy_institucional..."
cd greedy_institucional
if [ ! -f "target/greedy_institucional.war" ]; then
    mvn clean package -DskipTests
else
    echo "     WAR ya existe, omitiendo compilación."
fi
cd ..

echo "   - Compilando gredy_cars_client..."
cd gredy_cars_client/gredy_cars_client
if [ ! -f "target/gredy_cars_client.war" ]; then
    mvn clean package -DskipTests
else
    echo "     WAR ya existe, omitiendo compilación."
fi
cd ../..

# Construir imágenes Docker
echo "🐳 Construyendo imágenes Docker..."
docker-compose build

# Detener y eliminar contenedores existentes
echo "🛑 Deteniendo contenedores existentes..."
docker-compose down

# Iniciar los servicios
echo "🚀 Iniciando servicios..."
docker-compose up -d

# Esperar a que los servicios estén listos
echo "⏳ Esperando a que los servicios se inicien..."
sleep 30

# Verificar el estado de los contenedores
echo "📊 Verificando estado de los contenedores..."
docker-compose ps

echo ""
echo "✅ Despliegue completado!"
echo ""
echo "🌐 URLs de acceso:"
echo "   - API Greedy Cars:      http://localhost:9000/greedy_cars"
echo "   - Sitio Institucional: http://localhost:8080/greedy_institucional"
echo "   - App Cliente:         http://localhost:8081/gredy_cars_client"
echo "   - Base de Datos:       localhost:3307"
echo ""
echo "📋 Comandos útiles:"
echo "   - Ver logs:           docker-compose logs -f [nombre_servicio]"
echo "   - Detener servicios:   docker-compose down"
echo "   - Reiniciar:          docker-compose restart"
echo "   - Ver estado:         docker-compose ps"
echo ""
echo "🔧 Para acceder a la base de datos:"
echo "   - Host: localhost"
echo "   - Puerto: 3307"
echo "   - Usuario: greedy_cars"
echo "   - Contraseña: adminAdmin"
echo "   - Base de datos: greedy_cars_db"