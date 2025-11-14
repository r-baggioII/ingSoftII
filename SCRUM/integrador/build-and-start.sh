#!/bin/bash

# Script para compilar y iniciar Greedy Cars con Docker
# Uso: ./build-and-start.sh

echo "================================================"
echo "   GREEDY CARS - Compilación e Inicio"
echo "================================================"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Directorios
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Puertos a abrir
PORTS=(9000 8080 8081 3307)

# Asegurar permisos de ejecución en Maven Wrappers
echo -e "${GREEN}Verificando permisos de Maven Wrappers...${NC}"
chmod +x "$SCRIPT_DIR/greedy_cars/mvnw" 2>/dev/null
chmod +x "$SCRIPT_DIR/gredy_cars_client/gredy_cars_client/mvnw" 2>/dev/null
chmod +x "$SCRIPT_DIR/greedy_institucional/mvnw" 2>/dev/null
echo -e "${GREEN}✓ Permisos configurados${NC}"
echo ""

echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Configurando Firewall (Puertos)...${NC}"
echo -e "${GREEN}================================================${NC}"

# Función para abrir puertos con UFW
configure_ufw() {
    if command -v ufw &> /dev/null; then
        echo -e "${YELLOW}Configurando UFW...${NC}"
        for port in "${PORTS[@]}"; do
            sudo ufw allow $port/tcp 2>/dev/null && \
                echo -e "${GREEN}  ✓ Puerto $port/tcp abierto en UFW${NC}" || \
                echo -e "${YELLOW}  ⚠ No se pudo abrir puerto $port en UFW (puede requerir permisos)${NC}"
        done
    else
        echo -e "${YELLOW}  UFW no está instalado, omitiendo...${NC}"
    fi
}

# Función para abrir puertos con iptables
configure_iptables() {
    if command -v iptables &> /dev/null; then
        echo -e "${YELLOW}Configurando iptables...${NC}"
        for port in "${PORTS[@]}"; do
            # Verificar si la regla ya existe
            sudo iptables -C INPUT -p tcp --dport $port -j ACCEPT 2>/dev/null
            if [ $? -ne 0 ]; then
                sudo iptables -I INPUT -p tcp --dport $port -j ACCEPT 2>/dev/null && \
                    echo -e "${GREEN}  ✓ Puerto $port/tcp abierto en iptables${NC}" || \
                    echo -e "${YELLOW}  ⚠ No se pudo abrir puerto $port en iptables (puede requerir permisos)${NC}"
            else
                echo -e "${GREEN}  ✓ Puerto $port/tcp ya está abierto en iptables${NC}"
            fi
        done
        # Guardar reglas (intentar con diferentes métodos según la distribución)
        if command -v iptables-save &> /dev/null; then
            sudo iptables-save > /dev/null 2>&1 || true
        fi
    else
        echo -e "${YELLOW}  iptables no está disponible, omitiendo...${NC}"
    fi
}

# Configurar firewalls
configure_ufw
configure_iptables

echo ""

# Verificar que existe .env
if [ ! -f "$SCRIPT_DIR/.env" ]; then
    echo -e "${YELLOW}Advertencia: No existe archivo .env${NC}"
    echo -e "${YELLOW}Copiando .env.example a .env...${NC}"
    cp "$SCRIPT_DIR/.env.example" "$SCRIPT_DIR/.env"
    echo -e "${GREEN}✓ Archivo .env creado. Por favor, revisa y ajusta las variables si es necesario.${NC}"
    echo ""
fi

echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Compilando Backend (greedy_cars)...${NC}"
echo -e "${GREEN}================================================${NC}"

cd "$SCRIPT_DIR/greedy_cars" || exit 1

# Compilar backend usando Maven Wrapper con Docker
docker run --rm \
    -v "$SCRIPT_DIR/greedy_cars":/app \
    -v ~/.m2:/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17 \
    ./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el backend${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Compilando Frontend Cliente...${NC}"
echo -e "${GREEN}================================================${NC}"

cd "$SCRIPT_DIR/gredy_cars_client/gredy_cars_client" || exit 1

# Compilar frontend cliente usando Maven Wrapper con Docker
docker run --rm \
    -v "$SCRIPT_DIR/gredy_cars_client/gredy_cars_client":/app \
    -v ~/.m2:/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17 \
    ./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el frontend cliente${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Compilando Sitio Institucional...${NC}"
echo -e "${GREEN}================================================${NC}"

cd "$SCRIPT_DIR/greedy_institucional" || exit 1

# Compilar sitio institucional usando Maven Wrapper con Docker
docker run --rm \
    -v "$SCRIPT_DIR/greedy_institucional":/app \
    -v ~/.m2:/root/.m2 \
    -w /app \
    maven:3.9-eclipse-temurin-17 \
    ./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al compilar el sitio institucional${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   Iniciando servicios con Docker Compose...${NC}"
echo -e "${GREEN}================================================${NC}"

cd "$SCRIPT_DIR" || exit 1

# Detener contenedores existentes si hay alguno
docker-compose down 2>/dev/null

# Iniciar servicios
docker-compose up -d

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ Error al iniciar los servicios${NC}"
    exit 1
fi

echo ""
echo -e "${GREEN}Esperando a que los servicios inicien...${NC}"
sleep 10

echo ""
echo -e "${YELLOW}================================================${NC}"
echo -e "${YELLOW}   Verificando creación de usuarios...${NC}"
echo -e "${YELLOW}================================================${NC}"
echo ""
echo -e "${YELLOW}Buscando mensajes sobre creación de usuario 'jefe' en los logs...${NC}"
echo ""

# Verificar logs del backend para el usuario jefe
docker-compose logs greedy_cars | grep -E "(jefe|Usuario jefe|Persona jefe|Error al crear)" || \
    echo -e "${RED}⚠️  NO SE ENCONTRARON LOGS SOBRE EL USUARIO JEFE${NC}"

echo ""
echo -e "${YELLOW}Logs completos de inicialización de usuarios:${NC}"
docker-compose logs greedy_cars | grep -E "(✅|❌|ℹ️)" || \
    echo -e "${RED}⚠️  NO SE ENCONTRARON MENSAJES DE INICIALIZACIÓN${NC}"

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   ✓ SISTEMA INICIADO CORRECTAMENTE${NC}"
echo -e "${GREEN}================================================${NC}"
echo ""
echo -e "📍 URLs de acceso:"
echo -e "   ${GREEN}Backend API:${NC}        http://localhost:9000/greedy_cars"
echo -e "   ${GREEN}Frontend Cliente:${NC}   http://localhost:8081"
echo -e "   ${GREEN}Sitio Institucional:${NC} http://localhost:8080"
echo -e "   ${GREEN}Base de Datos:${NC}      localhost:3307"
echo ""
echo -e "📝 Ver logs:"
echo -e "   ${YELLOW}docker-compose logs -f greedy_cars${NC}"
echo -e "   ${YELLOW}docker-compose logs -f gredy_cars_client${NC}"
echo -e "   ${YELLOW}docker-compose logs -f greedy_institucional${NC}"
echo -e "   ${YELLOW}docker-compose logs -f greedy_cars_db${NC}"
echo ""
echo -e "⏹ Para detener el sistema:"
echo -e "   ${YELLOW}docker-compose down${NC}"
echo ""
