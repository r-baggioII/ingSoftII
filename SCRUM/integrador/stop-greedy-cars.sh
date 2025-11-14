#!/bin/bash

# Script para detener Greedy Cars - Backend, Frontend y Base de Datos (Docker)
# Uso: ./stop-greedy-cars.sh

echo "================================================"
echo "   GREEDY CARS - Detención del Sistema"
echo "================================================"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}Deteniendo contenedores Docker...${NC}"
echo ""

# Detener Backend
echo -e "${YELLOW}Deteniendo Backend (greedy-cars-backend)...${NC}"
if docker ps -a --format '{{.Names}}' | grep -q "^greedy-cars-backend$"; then
    docker stop greedy-cars-backend 2>/dev/null
    docker rm greedy-cars-backend 2>/dev/null
    echo -e "${GREEN}  ✓ Backend detenido y eliminado${NC}"
else
    echo -e "${GREEN}  ✓ Backend no está corriendo${NC}"
fi

echo ""

# Detener Frontend
echo -e "${YELLOW}Deteniendo Frontend (greedy-cars-frontend)...${NC}"
if docker ps -a --format '{{.Names}}' | grep -q "^greedy-cars-frontend$"; then
    docker stop greedy-cars-frontend 2>/dev/null
    docker rm greedy-cars-frontend 2>/dev/null
    echo -e "${GREEN}  ✓ Frontend detenido y eliminado${NC}"
else
    echo -e "${GREEN}  ✓ Frontend no está corriendo${NC}"
fi

echo ""

# Preguntar si desea detener la base de datos
echo -e "${YELLOW}¿Desea detener también la base de datos? (s/n):${NC}"
read -p "> " respuesta

if [[ "$respuesta" == "s" || "$respuesta" == "S" ]]; then
    echo -e "${YELLOW}Deteniendo Base de Datos (greedy_cars_db)...${NC}"
    if docker ps -a --format '{{.Names}}' | grep -q "^greedy_cars_db$"; then
        docker stop greedy_cars_db 2>/dev/null
        echo -e "${GREEN}  ✓ Base de datos detenida (datos preservados en volumen)${NC}"
        echo -e "${YELLOW}  Para eliminar también los datos: docker rm greedy_cars_db && docker volume rm greedy_cars_db_data${NC}"
    else
        echo -e "${GREEN}  ✓ Base de datos no está corriendo${NC}"
    fi
else
    echo -e "${GREEN}  ✓ Base de datos sigue corriendo${NC}"
fi

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   ✓ SISTEMA DETENIDO CORRECTAMENTE${NC}"
echo -e "${GREEN}================================================${NC}"
echo ""
echo -e "Para ver todos los contenedores: ${YELLOW}docker ps -a${NC}"
echo ""
