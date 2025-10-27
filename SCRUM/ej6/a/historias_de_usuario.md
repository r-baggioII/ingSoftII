# Historias de usuario

Este documento contiene las historias de usuario para las funcionalidades relacionadas con facturación en el prototipo de pantallas Bootstrap. Cada historia incluye descripción, criterios de aceptación y estimación en horas.

---

## HIST-001 — Factura de Cliente (Venta)
- Como: Usuario administrativo / vendedor
- Quiero: Generar y guardar una factura de cliente cuando se realiza una venta
- Para: Registrar la venta, calcular totales e impuestos y entregar comprobante al cliente

### Criterios de aceptación
- Se puede crear una factura con cliente, lista de artículos (cantidad, precio unitario), descuentos, impuestos (IVA) y totales.
- El sistema calcula subtotales, impuestos y total automáticamente.
- La factura se guarda en la base de datos y tiene un identificador único y fecha.
- Se puede visualizar la factura en pantalla y descargarla/emitirla en formato PDF.

Estimación: 2 horas

---

## HIST-002 — Factura de Proveedor (Compra)
- Como: Usuario administrativo / responsable de compras
- Quiero: Registrar facturas de proveedor para las compras realizadas
- Para: Llevar control de cuentas por pagar y stock (si aplica)

### Criterios de aceptación
- Se puede registrar factura de proveedor con proveedor, ítems comprados, cantidades, precios y totales.
- Se guarda la factura con referencia al proveedor y fecha de emisión.
- Posibilidad de vincular la factura a una orden de compra existente (si aplica).

Estimación: 2 horas

---

## ABM — Proveedores (CRUD básico)
- Como: Usuario administrativo / responsable de compras
- Quiero: Crear, listar, editar y eliminar proveedores desde un CRUD sencillo
- Para: Mantener la base de proveedores usada en compras y facturación

### Criterios de aceptación
- Se puede crear un proveedor con nombre/razón social, CUIT, dirección, contacto y condiciones de pago.
- Se puede listar, filtrar, editar y marcar proveedores como inactivos en lugar de eliminarlos físicamente.
- Validaciones básicas (CUIT requerido/formato mínimo, nombre requerido).

Estimación: 1 hora

---

## ABM — Sucursales (CRUD básico)
- Como: Usuario administrativo
- Quiero: Gestionar sucursales (puntos de venta / almacenes) con un CRUD sencillo
- Para: Asociar facturas, stock y operaciones a una sucursal específica

### Criterios de aceptación
- Se puede crear una sucursal con nombre, dirección, teléfono y responsable.
- Se puede listar, editar y desactivar sucursales.
- Las facturas y movimientos pueden registrar la sucursal asociada (campo referencial).

Estimación: 1 hora

---

## ABM — Productos (CRUD básico)
- Como: Usuario administrativo / inventario
- Quiero: Crear, listar, editar y desactivar productos con información mínima
- Para: Usar los productos en facturación y control de stock

### Criterios de aceptación
- Se puede crear un producto con código, descripción, precio unitario y unidad de medida.
- Se puede listar, editar y marcar productos como inactivos.
- Validaciones básicas (código único, descripción requerida, precio >= 0).

Estimación: 1 hora


## HIST-005 — ABM simple de Clientes (CRUD básico)
- Como: Usuario administrativo
- Quiero: Crear, listar, editar y eliminar clientes desde un CRUD sencillo
- Para: Mantener la base de clientes usada en facturación

### Criterios de aceptación
- Se puede crear un cliente con nombre, documento (DNI/CUIT), dirección y teléfono.
- Se puede listar todos los clientes, editar campos y eliminar registros (o marcarlos como inactivos).
- Validaciones básicas (nombre requerido, formato mínimo para documento).

Estimación: 1 hora

---


## Resumen de estimaciones
- FACT-001 (Factura de Cliente): 3 h
- FACT-002 (Factura de Proveedor): 3 h
- ABM Clientes (CRUD simple): 1 h
- ABM Proveedores (CRUD simple): 1 h
- ABM Sucursales (CRUD simple): 1 h
- ABM Productos (CRUD simple): 1 h

Total estimado: 10 horas

---

## Supuestos y notas
- Se asume que existe una base de datos y un sistema de autenticación básico.
- Las integraciones externas (p. ej. generación avanzada de PDF, envío por correo, recepción electrónica de facturas) no están incluidas en las estimaciones y se estimarán aparte si se requieren.
- Si el equipo requiere granularidad mayor (desglosar UI/Backend/APIs), se pueden dividir las historias en sub-tareas y ajustar estimaciones.
