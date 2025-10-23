# 🔧 GUÍA DE DEBUGGING - Sistema Mecánico

## 📋 Pasos para Diagnosticar el Error de Login

### 1. Reinicia la Aplicación con los Nuevos Logs
```bash
cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/ej5/G
mvn clean compile
mvn spring-boot:run
```

### 2. Observa los Logs al Iniciar

Cuando la aplicación inicie, verás algo como esto:

```
╔════════════════════════════════════════════════════════════════╗
║       🚀 APLICACIÓN INICIADA - DEBUG DE USUARIOS             ║
╚════════════════════════════════════════════════════════════════╝

📊 Total de usuarios en la base de datos: 6
─────────────────────────────────────────────────────────────────

👤 Usuario: admin-001
   📧 Email: admin@mecanico.com
   👔 Nombre: Administrador Principal
   🎭 Rol: ADMIN
   ❌ Eliminado: false
   🔑 Password (primeros 30): $2a$10$N9qo8uLOickgx2ZMRZoMye...
```

**✅ SI VES ESTO**: Los usuarios están cargados correctamente.
**❌ SI NO VES USUARIOS**: Ejecuta `mysql -u root -padminAdmin mecanico_db < poblar_bd.sql`

### 3. Intenta Hacer Login

Usa estas credenciales:
- **Email**: `admin@mecanico.com`
- **Password**: `password123`

### 4. Revisa los Logs Durante el Login

Deberías ver en la consola:

```
========================================
🔍 loadUserByUsername llamado
📧 Email recibido: [admin@mecanico.com]
👤 Usuario encontrado: SÍ
   - ID: admin-001
   - Nombre: Administrador Principal
   - Email: admin@mecanico.com
   - Rol: ADMIN
   - Eliminado: false
   - Password (primeros 20 chars): $2a$10$N9qo8uLOickgx
✅ Permisos asignados: [ROLE_ADMIN]
✅ Usuario guardado en sesión
========================================
```

### 5. Diagnóstico Según los Logs

#### Caso A: "👤 Usuario encontrado: NO"
**Problema**: La consulta no encuentra el usuario en la BD.
**Solución**:
```bash
# Verifica que el usuario existe
mysql -u root -padminAdmin mecanico_db -e "SELECT id, email, eliminado FROM Usuario WHERE email = 'admin@mecanico.com';"

# Si no existe, repobla
mysql -u root -padminAdmin mecanico_db < poblar_bd.sql
```

#### Caso B: Usuario encontrado pero login falla
**Problema**: BCryptPasswordEncoder no está validando correctamente.
**Verificación**:
```bash
# Verifica que el hash de la password sea correcto
mysql -u root -padminAdmin mecanico_db -e "SELECT LEFT(password, 30) FROM Usuario WHERE email = 'admin@mecanico.com';"
```
Debe mostrar: `$2a$10$N9qo8uLOickgx2ZMRZoMye`

#### Caso C: "🔍 loadUserByUsername" NO aparece
**Problema**: Spring Security no está llamando al UserDetailsService.
**Solución**: Verifica que el formulario de login envíe a `/logincheck`

### 6. Verifica las Consultas SQL

Los logs mostrarán las consultas SQL ejecutadas:
```sql
Hibernate: 
    SELECT u 
    FROM Usuario u 
    WHERE u.email = ? 
      AND u.eliminado = false
```

### 7. Comprueba Spring Security

Verás logs de Spring Security como:
```
DEBUG o.s.s.w.a.UsernamePasswordAuthenticationFilter : 
    Request is to process authentication
DEBUG o.s.s.authentication.ProviderManager : 
    Authenticating request with DaoAuthenticationProvider
```

## 🔍 Comandos Útiles para Debug

### Ver usuarios en la BD
```bash
mysql -u root -padminAdmin mecanico_db -e "SELECT id, nombre, email, rol, eliminado FROM Usuario;"
```

### Probar la query manualmente
```bash
mysql -u root -padminAdmin mecanico_db -e "SELECT * FROM Usuario WHERE email = 'admin@mecanico.com' AND eliminado = 0;"
```

### Ver logs en tiempo real
```bash
# En otra terminal mientras la app corre:
tail -f nohup.out
```

## 📝 Información de Debugging Actualizada

### Cambios Realizados:
1. ✅ Agregado `StartupDebugger` - Muestra usuarios al iniciar
2. ✅ Logs detallados en `loadUserByUsername`
3. ✅ Cambiado `FALSE` a `false` en queries (mejor compatibilidad)
4. ✅ Agregado `PasswordEncoder` explícito en Security
5. ✅ Agregado `AuthenticationManager` bean
6. ✅ Logging nivel DEBUG para Hibernate y Security

### URLs para Probar:
- Login: http://localhost:9000/usuario/login
- Después de login exitoso: http://localhost:9000/usuario/inicio

## 🆘 Si Nada Funciona

Ejecuta estos comandos paso a paso y envía los resultados:

```bash
# 1. Verifica la BD
mysql -u root -padminAdmin mecanico_db -e "SELECT COUNT(*) as total FROM Usuario;"

# 2. Verifica el usuario admin
mysql -u root -padminAdmin mecanico_db -e "SELECT id, email, LEFT(password,30) as pass FROM Usuario WHERE email='admin@mecanico.com';"

# 3. Reinicia todo
cd /home/rocio/Documentos/GitHub/ingSoftII/SCRUM/ej5/G
mvn clean
mysql -u root -padminAdmin mecanico_db < poblar_bd.sql
mvn spring-boot:run
```

Luego intenta login y copia TODA la salida de la consola donde dice:
- Los usuarios al inicio (del StartupDebugger)
- Los logs del intento de login (con 🔍 loadUserByUsername)
