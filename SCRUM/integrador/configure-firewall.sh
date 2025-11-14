#!/bin/bash

# Script para configurar firewall para Greedy Cars
# Uso: ./configure-firewall.sh

echo "================================================"
echo "   GREEDY CARS - Configuración de Firewall"
echo "================================================"
echo ""

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Puertos necesarios
PORTS=(9000 8080 8081 3307)

echo -e "${GREEN}Puertos a configurar:${NC}"
echo -e "  - ${YELLOW}9000${NC} : Backend API (greedy_cars)"
echo -e "  - ${YELLOW}8080${NC} : Sitio Institucional"
echo -e "  - ${YELLOW}8081${NC} : Frontend Cliente"
echo -e "  - ${YELLOW}3307${NC} : Base de Datos MariaDB"
echo ""

# Función para abrir puertos con UFW
configure_ufw() {
    if command -v ufw &> /dev/null; then
        echo -e "${GREEN}================================================${NC}"
        echo -e "${GREEN}   Configurando UFW...${NC}"
        echo -e "${GREEN}================================================${NC}"
        
        for port in "${PORTS[@]}"; do
            # Verificar si la regla ya existe
            sudo ufw status | grep -q "$port/tcp" 2>/dev/null
            if [ $? -ne 0 ]; then
                sudo ufw allow $port/tcp 2>/dev/null && \
                    echo -e "${GREEN}  ✓ Puerto $port/tcp abierto en UFW${NC}" || \
                    echo -e "${RED}  ✗ Error abriendo puerto $port en UFW${NC}"
            else
                echo -e "${GREEN}  ✓ Puerto $port/tcp ya está abierto en UFW${NC}"
            fi
        done
        
        echo ""
        echo -e "${YELLOW}Estado actual de UFW:${NC}"
        sudo ufw status numbered | grep -E "(9000|8080|8081|3307)" || echo "  No se encontraron reglas específicas"
    else
        echo -e "${YELLOW}UFW no está instalado en este sistema${NC}"
    fi
}

# Función para abrir puertos con iptables
configure_iptables() {
    if command -v iptables &> /dev/null; then
        echo ""
        echo -e "${GREEN}================================================${NC}"
        echo -e "${GREEN}   Configurando iptables...${NC}"
        echo -e "${GREEN}================================================${NC}"
        
        for port in "${PORTS[@]}"; do
            # Verificar si la regla ya existe
            sudo iptables -C INPUT -p tcp --dport $port -j ACCEPT 2>/dev/null
            if [ $? -ne 0 ]; then
                sudo iptables -I INPUT -p tcp --dport $port -j ACCEPT 2>/dev/null && \
                    echo -e "${GREEN}  ✓ Puerto $port/tcp abierto en iptables${NC}" || \
                    echo -e "${RED}  ✗ Error abriendo puerto $port en iptables${NC}"
            else
                echo -e "${GREEN}  ✓ Puerto $port/tcp ya está abierto en iptables${NC}"
            fi
        done
        
        # Intentar guardar reglas
        echo ""
        echo -e "${YELLOW}Intentando guardar reglas de iptables...${NC}"
        
        if [ -f /etc/debian_version ]; then
            # Debian/Ubuntu
            if command -v iptables-save &> /dev/null && command -v netfilter-persistent &> /dev/null; then
                sudo netfilter-persistent save 2>/dev/null && \
                    echo -e "${GREEN}  ✓ Reglas guardadas con netfilter-persistent${NC}" || \
                    echo -e "${YELLOW}  ⚠ No se pudieron guardar con netfilter-persistent${NC}"
            elif command -v iptables-save &> /dev/null; then
                sudo sh -c "iptables-save > /etc/iptables/rules.v4" 2>/dev/null && \
                    echo -e "${GREEN}  ✓ Reglas guardadas en /etc/iptables/rules.v4${NC}" || \
                    echo -e "${YELLOW}  ⚠ No se pudieron guardar las reglas${NC}"
            fi
        elif [ -f /etc/redhat-release ]; then
            # RedHat/CentOS
            if command -v iptables-save &> /dev/null; then
                sudo service iptables save 2>/dev/null && \
                    echo -e "${GREEN}  ✓ Reglas guardadas${NC}" || \
                    echo -e "${YELLOW}  ⚠ No se pudieron guardar las reglas${NC}"
            fi
        else
            echo -e "${YELLOW}  ⚠ Sistema no reconocido, puede que necesites guardar las reglas manualmente${NC}"
        fi
        
        echo ""
        echo -e "${YELLOW}Reglas actuales de iptables para los puertos:${NC}"
        sudo iptables -L INPUT -n --line-numbers | grep -E "(9000|8080|8081|3307)" || echo "  No se encontraron reglas específicas"
    else
        echo -e "${YELLOW}iptables no está disponible en este sistema${NC}"
    fi
}

# Verificar si tiene permisos de root
if [ "$EUID" -eq 0 ]; then
    echo -e "${YELLOW}Ejecutando como root...${NC}"
    echo ""
else
    echo -e "${YELLOW}Se solicitarán permisos sudo para configurar el firewall...${NC}"
    echo ""
fi

# Configurar firewalls
configure_ufw
configure_iptables

echo ""
echo -e "${GREEN}================================================${NC}"
echo -e "${GREEN}   ✓ Configuración de Firewall Completada${NC}"
echo -e "${GREEN}================================================${NC}"
echo ""

# Mostrar resumen
echo -e "${YELLOW}Resumen:${NC}"
echo -e "  Los siguientes puertos deberían estar abiertos:"
for port in "${PORTS[@]}"; do
    echo -e "    - ${GREEN}$port/tcp${NC}"
done

echo ""
echo -e "${YELLOW}Verificación:${NC}"
echo -e "  Desde otra máquina, puedes probar la conectividad con:"
echo -e "    ${GREEN}telnet <ip-del-servidor> 9000${NC}"
echo -e "    ${GREEN}telnet <ip-del-servidor> 8080${NC}"
echo -e "    ${GREEN}telnet <ip-del-servidor> 8081${NC}"
echo -e "    ${GREEN}nc -zv <ip-del-servidor> 9000${NC}"
echo ""

# Mostrar IP del servidor
echo -e "${YELLOW}IP del servidor:${NC}"
ip addr show | grep "inet " | grep -v 127.0.0.1 | awk '{print "    " $2}' || echo "    No se pudo detectar"
echo ""
