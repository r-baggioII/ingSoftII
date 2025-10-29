# Historias de usuario

Este documento contiene las historias de usuario para las funcionalidades relacionadas con facturación en el prototipo de pantallas Bootstrap. Cada historia incluye descripción, criterios de aceptación y estimación en horas.

---

## HIST-001 — Movimiento de Dinero (Venta)
- Como: Usuario administrativo / vendedor (cajero)
- Quiero: Registrar un movimiento de dinero de tipo venta (MovimientoDineroVenta) cuando se realiza una venta
- Para: Registrar la operación, calcular totales e impuestos y asociar la transacción a una caja y sucursal

### Criterios de aceptación

### Criterios de aceptación
- Se puede crear un movimiento de dinero de tipo venta con referencia a una o varias líneas de Detalle; cada Detalle referencia un Producto, cantidad, precio unitario y subtotal.
- El sistema calcula subtotales, impuestos y total automáticamente (método calcularTotal sobre Detalle).
- El movimiento se guarda en la base de datos con identificador único, fecha, referencia a la caja y sucursal, y el conjunto de Detalles.
- Se puede visualizar el movimiento en pantalla y descargarlo/emitirlo en formato PDF o exportarlo como comprobante.
 - Se puede visualizar el movimiento en pantalla y descargarlo/emitirlo en formato PDF o exportarlo como comprobante.

Estimación: 4 horas

---

## HIST-002 — Movimiento de Dinero (Compra)
- Como: Usuario administrativo / responsable de compras
- Quiero: Registrar un movimiento de dinero de tipo compra (MovimientoDineroCompra) para las compras realizadas
- Para: Llevar control de cuentas por pagar, stock y conciliación de proveedores

### Criterios de aceptación

### Criterios de aceptación
- Se puede crear un movimiento de dinero de tipo compra con referencia a una o varias líneas de Detalle; cada Detalle referencia un Producto, cantidad, precio unitario y subtotal.
- El movimiento se guarda con referencia al proveedor/Persona, fecha de emisión y el conjunto de Detalles.
- Posibilidad de vincular el movimiento a una orden de compra existente y al control de stock cuando aplique (ajuste de stock automático si corresponde).

Estimación: 4 horas

---

## HIST-003 — ABM Proveedores (CRUD básico)
- Como: Usuario administrativo / responsable de compras
- Quiero: Crear, listar, editar y eliminar proveedores desde un CRUD sencillo
- Para: Mantener la base de proveedores usada en compras y facturación

### Criterios de aceptación
- Se puede crear un proveedor con nombre/razón social, CUIT, dirección, contacto y condiciones de pago.
- Se puede listar, filtrar, editar y marcar proveedores como inactivos en lugar de eliminarlos físicamente.
- Validaciones básicas (CUIT requerido/formato mínimo, nombre requerido).

Estimación: 1 hora

---

## HIST-004 — ABM Sucursales (CRUD básico)
- Como: Usuario administrativo
- Quiero: Gestionar sucursales (puntos de venta / almacenes) con un CRUD sencillo
- Para: Asociar facturas, stock y operaciones a una sucursal específica

### Criterios de aceptación
- Se puede crear una sucursal con nombre, dirección, teléfono y responsable.
- Se puede listar, editar y desactivar sucursales.
- Las facturas y movimientos pueden registrar la sucursal asociada (campo referencial).

Estimación: 1 hora

---

## HIST-005 — ABM Productos (CRUD básico)
- Como: Usuario administrativo / inventario
- Quiero: Crear, listar, editar y desactivar productos con información mínima
- Para: Usar los productos en facturación y control de stock

### Criterios de aceptación
- Se puede crear un producto con código, descripción, precio unitario y unidad de medida.
- Se puede listar, editar y marcar productos como inactivos.
- Validaciones básicas (código único, descripción requerida, precio >= 0).

### Notas adicionales sobre Producto
- Los productos deben incluir stock disponible y unidad de medida; el sistema debe soportar ajustes de stock desde movimientos de compra/venta.
- Un Producto será referenciado por Detalle (línea) en los Movimientos de Dinero.

Estimación: 2 horas

---

## HIST-006 — ABM Clientes (CRUD básico)
- Como: Usuario administrativo
- Quiero: Crear, listar, editar y eliminar clientes desde un CRUD sencillo
- Para: Mantener la base de clientes usada en facturación

### Criterios de aceptación
- Se puede crear un cliente con nombre, documento (DNI/CUIT), dirección y teléfono.
- Se puede listar todos los clientes, editar campos y eliminar registros (o marcarlos como inactivos).
- Validaciones básicas (nombre requerido, formato mínimo para documento).

Estimación: 1 hora

---

## HIST-007 — ABM Cajas (CRUD básico)
- Como: Usuario administrativo / cajero
- Quiero: Crear, abrir/cerrar y gestionar cajas (punto de cobro) para registrar movimientos diarios
- Para: Llevar control de cierres diarios, conciliación de efectivo y asociación a sucursales

### Criterios de aceptación
- Se puede crear una caja con nombre/código, sucursal asociada y responsable asignado.
- Soporte para abrir/cerrar caja diaria y registrar saldo inicial, ingresos y egresos.
- Las transacciones de venta pueden registrarse contra una caja específica.
- Las cajas pueden marcarse como activas/inactivas.

Estimación: 2 horas

---

## HIST-008 — ABM Usuarios (CRUD y roles) — relacionados con Cajas y Sucursales
- Como: Administrador del sistema
- Quiero: Crear, listar, editar y desactivar usuarios y asignarles sucursales y cajas
- Para: Controlar acceso y responsabilidades (ej. cajeros asignados a cajas en sucursales)

### Criterios de aceptación
- Se puede crear un usuario con nombre, email, rol (admin, vendedor, cajero, etc.) y credenciales básicas.
- Cada usuario puede ser asociado a una o varias sucursales.
- Los usuarios con rol cajero pueden ser asociados a una o varias cajas; al abrir sesión, sólo verán las cajas/sucursales asignadas.
- Soporte para activar/desactivar usuarios y reset de contraseña.
- Validaciones básicas (email requerido, rol válido).

Estimación: 3 horas

---

## HIST-009 — ABM Personas (CRUD básico)
- Como: Usuario administrativo
- Quiero: Crear, listar, editar y desactivar entidades Persona (clientes, proveedores, dueños, etc.)
- Para: Normalizar la información de personas en el sistema y reutilizarla desde movimientos y ABMs

### Criterios de aceptación
- Se puede crear una Persona con nombre, tipo (cliente/proveedor/dueño), documento/identificador, dirección y contacto.
- Se puede listar, buscar por documento o nombre, editar y desactivar Personas.
- Las facturas / movimientos y otras entidades pueden referenciar a una Persona por su identificador.
- Validaciones básicas (nombre requerido, documento con formato mínimo según tipo).

Estimación: 1 hora

---

## HIST-010 — Detalle (línea de movimiento)
- Como: Usuario administrativo / sistema
- Quiero: Definir y gestionar la estructura de Detalle que compone un Movimiento de Dinero
- Para: Representar ítems/líneas de venta y compra que referencian Productos y permitir cálculos por línea

### Criterios de aceptación
- Un Detalle contiene referencia a un Producto, cantidad, precio unitario y subtotal (cantidad * precio unitario - descuentos si aplica).
- Se valida que la cantidad sea > 0 y el precio unitario >= 0.
- Los Detalles están asociados a un Movimiento de Dinero y no existen de forma aislada (se crean dentro del contexto del Movimiento).
- Los Detalles permitirán el cálculo automático del subtotal y serán consumidos por `calcularTotal` para obtener el total del movimiento.

Estimación: 1 hora

---

## Resumen de estimaciones
## Resumen de estimaciones
- HIST-001 (Movimiento de Dinero — Venta): 4 h
- HIST-002 (Movimiento de Dinero — Compra): 4 h
- HIST-003 (ABM Proveedores): 1 h
- HIST-004 (ABM Sucursales): 1 h
- HIST-005 (ABM Productos): 2 h
- HIST-006 (ABM Clientes): 1 h
- HIST-007 (ABM Cajas): 2 h
- HIST-008 (ABM Usuarios): 3 h

- HIST-009 (ABM Personas): 1 h

- HIST-010 (Detalle): 1 h

Total estimado: 20 horas

---

## Supuestos y notas
- Se asume que existe una base de datos y un sistema de autenticación básico.
- Los usuarios deben poder asociarse a sucursales y, cuando aplique, a cajas; la UI y backend deberán soportar esas relaciones.
- Las integraciones externas (p. ej. generación avanzada de PDF, envío por correo, recepción electrónica de facturas) no están incluidas en las estimaciones y se estimarán aparte si se requieren.
