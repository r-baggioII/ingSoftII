Scope and roles
Based on the class diagram and code, features in scope:
Usuarios (CRUD, roles ADMINISTRATIVO/PROFESOR/SOCIO)
Socios (CRUD, asociación a Usuario)
Empleados (CRUD)
Empresas/Sucursales/Direcciones (CRUD de País/Provincia/Departamento/Localidad/Dirección)
CuotaMensual (emitir/listar/actualizar estado)
ValorCuota (historial y vigente)
Facturación (Factura/DetalleFactura, FormaDePago)
Pagos con Mercado Pago (socio)
Deuda (resumen: total y meses adeudados)
Campañas/promociones por email (Mensaje/Promoción)
Saludos de cumpleaños (email programado)
Perfiles (permisos):
Administrador: todo
Empleado: todo excepto gestión de usuarios y configuración de valor de cuota
Socio: ver/pagar cuotas (Mercado Pago) y ver informe de deuda
Pantallas ya presentes:
login.html
dashboard-admin.html
dashboard-empleado.html
dashboard-socio.html
panel-entidades.html (CRUD centralizado)
factura.html
To integrate smoothly, we’ll deep-link from dashboards to panel-entidades with an entity selector (query param) and enforce role access in server and UI.

1) Login
Functions to integrate

Ingresar con nombreUsuario/clave.
Guardar sesión (HttpSession) con usuario y rol.
Redirigir por rol:
ADMINISTRATIVO → /dashboard/admin
PROFESOR → /dashboard/empleado
SOCIO → /dashboard/socio
Controllers/Endpoints

POST /login → LoginControlador.procesarLogin (ya existente)
GET /logout → invalidar sesión
Manual test

Prepara 3 usuarios (uno por rol).
Iniciar sesión con credenciales válidas y verificar redirección a dashboard correcto.
Probar credenciales inválidas → ver mensaje de error.
Click “Cerrar Sesión” → vuelve a inicio/login.
2) Dashboard Administrador
Objetivo: Punto central para navegar a todos los módulos.

2.1. Gestión de Usuarios (A)

Funciones: Alta, Consulta, Modificación, Baja. Asignar RolUsuario.
UI: Tarjeta “Gestión de Usuarios” → abrir panel-entidades preseleccionado en “usuarios”.
Endpoints:
GET/POST/PUT/DELETE /api/usuarios (existe UsuarioControlador)
Cambios de integración:
Deep-link: añadir soporte a panel-entidades para leer ?entity=usuarios y seleccionar la entidad al cargar (pequeña mejora en panel-entidades.js).
Manual test:
Crear un usuario con rol SOCIO; editar su rol; intentar borrarlo; listar activos/inactivos; buscar por nombre.
2.2. Socios (A)

Funciones: Alta/Consulta/Mod/Baja; vincular usuario (opcional en esta etapa si UI lo permite).
UI: Tarjeta “Socios” → panel-entidades?entity=socios.
Endpoints: /api/v1/socios (existe SocioControlador).
Manual test:
Crear socio con datos válidos; editar; dar de baja; filtrar; buscar por DNI.
2.3. Empleados

Funciones: Alta/Consulta/Mod/Baja.
UI: Tarjeta “Empleados” → panel-entidades?entity=empleados.
Endpoints: /api/v1/empleados (existe EmpleadoControlador).
Manual test:
Crear empleado (tipos según enum en código); editar; listar.
2.4. Empresas y Sucursales

Funciones: CRUD de empresa; CRUD de sucursal con dirección completa.
UI: Tarjeta → panel-entidades?entity=empresas y panel-entidades?entity=sucursales
Endpoints:
Empresas: /api/empresas (existe EmpresaControlador)
Sucursales: /api/v1/sucursales (SucursalControlador)
Manual test:
Crear empresa, luego sucursal enlazada con dirección; ver listado con empresa y dirección formateada.
2.5. Direcciones (Países, Provincias, Departamentos, Localidades, Direcciones)

Funciones: CRUD de todos los niveles geográficos y direcciones.
UI: Tarjeta “Gestión Geográfica” → panel-entidades?entity=direcciones (el panel ya soporta subtipos y dependencias).
Endpoints:
v1: /api/v1/paises|provincias|departamentos|localidades (ya hay controladores V1)
agrupados: /api/direcciones/* (para formularios dependientes en Sucursales)
Manual test:
Crear País, luego Provincia (depende País), Departamento (depende Provincia), Localidad (depende Departamento); luego crear Dirección con cascada de selects.
2.6. Cuotas mensuales (B, C)

Funciones: Crear cuota para un socio (mes, año, valor); listar; actualizar estado; ver deuda por socio (sumatoria de ADEUDADAS).
UI: Tarjeta “Cuotas mensuales” → panel-entidades?entity=cuotas.
Endpoints: /api/cuotas-mensuales (CuotaMensualControlador)
Manual test:
Crear varias cuotas ADEUDADAS para un socio; editar estado a PAGADA; comprobar filtros y búsqueda.
Validar que vencimiento/valor se muestren.
2.7. Valores de cuota (B)

Funciones: Definir valor vigente (historial).
UI: Tarjeta “Valores de cuota” → panel-entidades?entity=valorCuotas (solo Admin).
Endpoints: /api/valor-cuotas (ValorCuotaControlador)
Manual test:
Crear valor vigente; crear otro con fechaDesde posterior; listar activos.
2.8. Facturación

Funciones: Ver facturas creadas (desde pagos); ver detalles; exportar/mostrar.
UI: Tarjeta “Facturación” → simple listado o aprovechar endpoints existentes; link a GET /facturas/{id}/ver (existe FacturaVistaControlador y factura.html).
Manual test:
Tras un pago (ver sección SOCIO), abrir la última factura y validar contenido (total, cuotas incluidas, forma de pago).
2.9. Mensajes y Promociones (D, E)

Funciones:
Campañas promocionales por correo: crear/enviar a segmentación simple (todos los socios activos).
Saludos de cumpleaños: servicio programado diario (Spring Scheduling) + plantillas.
UI: Tarjeta “Mensajes” → página simple o agregar entidad/flujo en panel-entidades (si se expone Mensaje).
Endpoints necesarios:
Exponer MensajeControlador (si falta) con: crear mensaje, listar, enviar; usar spring-boot-starter-mail.
Programar @Scheduled(cron = ...) en MensajeServicio para cumpleaños: buscar socios con fecha de hoy, enviar.
Manual test:
Configurar SMTP en application.properties.
Crear mensaje y enviar a “todos los socios”; validar recepción/logs.
Crear socio con cumpleaños hoy; ejecutar tarea (trigger manual vía endpoint de admin) y validar envío.
2.10. Reportes (opcional, resumidos)

Funciones: totales de socios, empleados, cuotas emitidas/pagadas, deuda total.
UI: Tarjeta “Reportes” → página simple con 4 KPIs (consulta rápida).
Endpoints: pequeños endpoints en servicios de cuotas/socios para counts/sum.
Restricciones/Permisos en Admin:

Ninguna, acceso total.
3) Dashboard Empleado
Objetivo: Módulos operativos, sin gestión de usuarios ni seteo de valor de cuota.

Módulos habilitados

Empresas/Sucursales (consulta/edición)
Empleados (completo)
Socios (completo)
Cuotas (completo: emitir, cambiar estado, registrar pagos en efectivo/transferencia con FormaDePago EFECTIVO/TRANSFE)
Facturación (ver/listar)
Mensajes: permitir enviar campañas y avisos (si se define que empleado puede enviar; si no, solo admin)
Reportes (consulta)
Módulos restringidos

Usuarios (ocultar link y bloquear server-side)
Valores de cuota (ocultar y bloquear server-side)
Endpoints

Idénticos a Admin, control de permisos por rol en middleware/interceptor.
Manual test

Iniciar sesión con usuario PROFESOR (Empleado).
Ver que “Usuarios” y “Valores de cuota” aparecen deshabilitados o no aparecen.
Intentar acceder manualmente vía URL → debe redirigir a login o mostrar 403/redirect según implementación.
Crear una cuota, marcarla pagada (EFECTIVO); validar factura generada si corresponde al flujo (puede ser manual en backoffice).
4) Dashboard Socio
Objetivo: Pagar cuotas con Mercado Pago y ver su deuda.

4.1. Pago de cuotas (B)

Funciones:
Listar cuotas ADEUDADAS de “ese socio” (filtrar por usuario en sesión).
Seleccionar cuotas → POST /api/socio/pagos/preferencia → redirigir al initPoint.
En retorno de éxito, marcar cuotas como PAGADA y crear Factura (ya implementado en MercadoPagoDemoControlador).
Mostrar botón “Ver factura del último pago” (ya cableado).
Endpoints actuales:
GET /api/socio/pagos/cuotas-pendientes (debe ajustarse a filtrar por socio en sesión)
POST /api/socio/pagos/preferencia
GET /api/socio/pagos/success|pending|failure (ya lo maneja el controlador)
Integración pendiente:
El endpoint “cuotas-pendientes” debe retornar solo las cuotas del socio logueado (hoy devuelve todas las cuotas). Ajustar repositorio/consulta usando socioRepositorio.findByUsuarioId... desde sesión.
Mover el access token de Mercado Pago a application.properties (hoy está hardcodeado).
Configurar backUrls al dominio real (no ngrok fijo).
Manual test:
Crear socio + cuotas ADEUDADAS para su DNI.
Iniciar sesión como socio; ver tabla con sus cuotas (no de otros).
Pagar 1-2 cuotas (sandbox preferible); al volver, ver mensaje de éxito y “Ver factura”.
Abrir factura y verificar total y detalles.
Refrescar: cuotas ya no deben aparecer en pendientes.
4.2. Informe de deuda (C)

Funciones: Mostrar “Total adeudado”, “Meses pendientes”, “Último pago”.
Integración:
El JS de la pantalla ya calcula el resumen en base a cuotasPendientes; al filtrar por socio quedará correcto.
Alternativa: endpoint dedicado /api/socio/deuda/resumen con {total, meses, ultimoPago} para escalar a dataset grande.
Manual test:
Con cuotas adeudadas y luego pagándolas, confirmar que el resumen cambia.
Restricciones/Permisos en Socio:

Ocultar/ignorar módulos administrativos.
Asegurar que el socio no pueda abrir /panel/entidades.
5) Navegación y deep-link desde dashboards
Añadir soporte a panel-entidades.html (JS) para leer ?entity=<clave> y seleccionar:
usuarios, socios, empleados, empresas, sucursales, cuotas, valorCuotas, direcciones (y subtipos), paises, provincias, departamentos, localidades
En dashboards, actualizar tarjetas:
Admin:
Usuarios → /panel/entidades?entity=usuarios
Socios → /panel/entidades?entity=socios
Empleados → /panel/entidades?entity=empleados
Empresas → /panel/entidades?entity=empresas
Sucursales → /panel/entidades?entity=sucursales
Cuotas → /panel/entidades?entity=cuotas
Valores de cuota → /panel/entidades?entity=valorCuotas
Geografía → /panel/entidades?entity=direcciones
Empleado: igual pero sin Usuarios ni ValorCuota.
Socio: no deep-link al panel; solo sección de pagos y deuda.
Manual test

Desde cada tarjeta, abrir y validar que el panel cargue la entidad seleccionada.
Refrescar URL con el query param; sigue en la entidad correcta.
6) Autorización y seguridad (server-side)
Implementar un Interceptor/Filter (o Spring Security simple) que:
Requiera sesión para /panel/entidades, /dashboard/, /api/ (salvo /login, /, recursos estáticos).
Chequee rol:
Usuario ADMINISTRATIVO: todo
PROFESOR: bloquear /api/usuarios y /api/valor-cuotas
SOCIO: bloquear todo /panel/entidades y APIs administrativas
Reutilizar LoginControlador.verificarSesion o formalizar con Spring Security si lo prefieres.
Manual test:
Con PROFESOR, intentar abrir /panel/entidades?entity=usuarios → redirigir o 403.
Con SOCIO, intentar /panel/entidades → redirigir o 403.
Sin sesión, abrir /dashboard/admin → redirige a /login.
7) Promociones y cumpleaños (D, E)
7.1. Campañas/promos por correo

UI: Una vista simple (o entidad “Mensajes” en el panel) para:
Crear mensaje: título/texto/tipo PROMOCION
Selección: “todos los socios activos” (simple)
Acción: Enviar → MensajeServicio itera socios y envía por mail.
Endpoints:
POST /api/mensajes (crear)
POST /api/mensajes/{id}/enviar (enviar)
GET /api/mensajes (listar)
Config:
spring.mail.* en properties.
Manual test:
Crear mensaje PROMOCION; enviar; revisar recepción/logs.
7.2. Cumpleaños

Servicio programado diario:
Buscar socios con fechaNacimiento = hoy (mes/día).
Crear y enviar Mensaje tipo CUMPLEAÑOS.
Endpoints auxiliares:
POST /api/mensajes/tareas/cumpleanios/run (para forzar ejecución manual desde admin).
Manual test:
Crear socio con cumpleaños hoy.
Ejecutar endpoint manual y verificar email/log.
8) Facturación y cobros (B)
Flujo Mercado Pago (socio) ya crea Factura y marca cuotas PAGADA.
Flujo de caja (empleado):
En panel-entidades?entity=cuotas, al cambiar estado a PAGADA manualmente:
Crear Factura con FormaDePago EFECTIVO/TRANSFERENCIA.
Si aún no está implementado, agregar en CuotaMensualControlador/FacturaServicio la creación automática al cambiar a PAGADA (solo si no existe factura para esa cuota).
Manual test:
Pagar manualmente una cuota como efectivo; verificar factura creada y que no se duplique con otra acción.
9) Reportes rápidos (opcional de valor)
KPIs en dashboards de Admin/Empleado:

Total Socios Activos
Cuotas ADEUDADAS (cantidad y $)
Cuotas PAGADAS del mes
Facturación del mes
Endpoints simples de agregación en los servicios existentes.
Manual test:
Cargar datos conocidos y verificar números contra consultas directas en DB.
10) Ajustes técnicos necesarios
Panel deep-link: añadir lectura de ?entity= en panel-entidades.js al inicializar, y opcionalmente ?subtype=localidades para geografía.
Socio pagos:
Filtrar por socio en GET /api/socio/pagos/cuotas-pendientes.
Externalizar mercadopago.access-token y mercadopago.back-urls.* en application.properties.
Seguridad:
Interceptor/Filter o Spring Security básico.
Email:
spring.mail.host, username, password, port, properties.mail.smtp.auth/starttls.
Logs/Errores:
Mostrar alertas en panel (ya soportado) con mensajes del backend.
Internacionalización de fechas y moneda (opcional): estandarizar formato ARS en front.
11) Pruebas manuales de punta a punta
Caso feliz Admin:
Login admin → dashboard admin.
Crear País/Provincia/Departamento/Localidad → crear Empresa → crear Sucursal con dirección.
Crear Socio y Usuario SOCIO.
Crear Valor de Cuota vigente.
Emitir Cuotas ADEUDADAS para el Socio.
Ver en Cuotas; actualizar una a PAGADA con EFECTIVO → verificar Factura creada.
Caso feliz Empleado:
Login empleado → dashboard empleado.
Validar que Usuarios/Valores de cuota no están disponibles ni accesibles por URL.
Emitir cuota para un socio y marcar como PAGADA.
Caso feliz Socio:
Login socio → ver deuda y cuotas pendientes.
Seleccionar cuotas → pagar (sandbox) → volver, ver éxito y factura.
Promos y cumpleaños:
Configurar mail → crear mensaje PROMOCION y enviar → revisar recepción.
Crear socio con cumpleaños hoy → ejecutar endpoint de tarea → verificar email.
12) Checklist de permisos y wiring por rol
Admin:
Mostrar todas las tarjetas.
Permitir todas las entidades en panel-entidades.
Empleado:
Ocultar tarjetas: “Gestión de Usuarios”, “Valores de cuota”.
Bloquear por servidor /api/usuarios y /api/valor-cuotas.
Socio:
Solo dashboard-socio y APIs /api/socio/pagos/*.
Bloquear /panel/entidades y APIs admin.
13) Entidades alineadas al diagrama
Usuario (id, nombreUsuario, clave, rol, eliminado) → Login/CRUD.
Persona (madre de Empleado/Socio) → ya modelado en entidades.
Socio (numeroSocio) → CRUD, deuda, pagos.
Empleado (tipoEmpleado) → CRUD.
País/Provincia/Departamento/Localidad/Dirección → CRUD y selects dependientes.
Sucursal/Empresa → CRUD con dirección.
ValorCuota → vigente/historial.
CuotaMensual (mes, año, estado, vencimiento, valor) → emitir/pagar.
Factura/DetalleFactura/FormaDePago → generación tras pago.
Mensaje/Promoción → campañas y cumpleaños.
Completion summary and next steps
What’s ready: Most controllers, services, templates, and the socio Mercado Pago flow exist and are close to integrated.
What to implement/tweak:
Deep-link de panel-entidades por ?entity=.
Filtro por socio en /api/socio/pagos/cuotas-pendientes.
Seguridad por rol (interceptor/security) para panel y APIs.
Mensajería: exponer MensajeControlador + tarea programada cumpleaños + view simple.
Externalizar/configurar Mercado Pago y correo en properties.
Opcional: KPIs rápidos de reportes.
If you want, I can implement the small wiring changes (panel deep-link + filtro por socio + basic role interceptor) in a follow-up.

Requirements coverage:

A Usuarios/Socios CRUD: planned (panel + role checks) with manual tests.
B Cobro cuota mensual: planned (panel + socio MP payment) with tests.
C Deuda: planned (socio summary driven by cuotas pendientes) with tests.
D Campañas por email: planned (controller + mail config) with tests.
E Cumpleaños: planned (scheduled + trigger) with tests.
Rutinas: intentionally ignored as requested.
Planning status:

Auditar pantallas y controladores actuales: completed
Diseñar plan de integración por pantallas: completed
Mapear funciones a controladores/endpoints: completed
Redactar pruebas manuales por función: completed
Checklist de wiring y permisos por rol: completed