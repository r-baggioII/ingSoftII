#!/bin/bash

# Script para detener Greedy Cars
# Uso: ./stop.sh

echo "================================================"
echo "   GREEDY CARS - Deteniendo Sistema"
echo "================================================"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Directorios
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cd "$SCRIPT_DIR" || exit 1

echo -e "${YELLOW}Deteniendo servicios...${NC}"
docker-compose down

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ Sistema detenido correctamente${NC}"
    echo ""
    echo -e "Para eliminar también los datos de la base de datos:"
    echo -e "   ${YELLOW}docker-compose down -v${NC}"
    echo ""
else
    echo ""
    echo -e "${RED}✗ Error al detener el sistema${NC}"
    exit 1
fi
