# Refactorización: Direcciones con Coordenadas Google Maps

## 🎯 Cambios Implementados

### 1. **Entidad Direccion** 
Se agregaron dos nuevos campos opcionales:
- `latitud` (String, máximo 32 caracteres)
- `longitud` (String, máximo 32 caracteres)

**Métodos agregados:**
- `hasGeoPoint()`: verifica si tiene coordenadas válidas
- `getGoogleMapsUrl()`: genera URL de Google Maps

### 2. **Interfaz de Usuario Actualizada**

#### **Empresas** (`/empresas`)
- ✅ Nueva columna "Ubicación" con botón de mapa
- ✅ Botón verde con icono `fa-map-marker`
- ✅ Abre Google Maps en nueva pestaña

#### **Proveedores** (`/proveedores`)
- ✅ Nueva columna "Ubicación" con botón de mapa
- ✅ Funcionalidad idéntica a empresas

#### **Personas** (`/personas`) - **NUEVO**
- ✅ Controlador, servicio y templates creados
- ✅ Lista con columna "Ubicación" y botón de mapa
- ✅ Formulario para crear/editar personas
- ✅ Menú lateral actualizado

### 3. **Funcionalidad del Botón de Mapa**
- **Formato URL**: `https://www.google.com/maps?q=latitud,longitud`
- **Ejemplo**: `https://www.google.com/maps?q=-32.88970575178735,-68.84457510855037`
- **Comportamiento**: Solo aparece si la dirección tiene coordenadas válidas
- **Target**: `_blank` (nueva pestaña)

## 🗺️ Ejemplos de Coordenadas

### Argentina - Puntos de Referencia
- **Plaza Independencia, Mendoza**: `-32.88970575178735,-68.84457510855037`
- **Obelisco, Buenos Aires**: `-34.6037389,-58.3815704`
- **Centro de Córdoba**: `-31.4200833,-64.1887761`

### Cómo Obtener Coordenadas
1. Ir a [Google Maps](https://maps.google.com)
2. Buscar ubicación
3. Clic derecho en el punto exacto
4. Seleccionar "¿Qué hay aquí?"
5. Copiar coordenadas que aparecen abajo

## 🚀 Navegación

| Sección | URL | Descripción |
|---------|-----|-------------|
| Empresas | `/empresas` | Lista con botones de mapa |
| Proveedores | `/proveedores` | Lista con botones de mapa |
| Personas | `/personas` | **NUEVO** - Lista con botones de mapa |

## 🔧 Base de Datos

### Auto-migración
Hibernate crea automáticamente las nuevas columnas:
```sql
ALTER TABLE direccion ADD COLUMN latitud VARCHAR(32);
ALTER TABLE direccion ADD COLUMN longitud VARCHAR(32);
```

### Datos de Prueba
Ver archivo `ejemplo_coordenadas.sql` para ejemplos de INSERT/UPDATE.

## ✅ Testing
- Tests unitarios incluidos en `DireccionTest.java`
- Verifican funcionalidad de coordenadas y URLs
- Build exitoso confirmado

## 📱 Uso en Producción
1. **Ejecutar la aplicación**: `./mvnw spring-boot:run`
2. **Agregar direcciones** con coordenadas a empresas/proveedores/personas
3. **Ver botones de mapa** aparecer automáticamente
4. **Clic en botón** abre Google Maps con la ubicación exacta

¡La funcionalidad está lista para usar! 🎉