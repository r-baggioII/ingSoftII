# TEST_PROMOCIONES

## 1. Alta de promoción (rol JEFE)
1. Iniciar sesión como usuario con rol JEFE.
2. Navegar a `/gestion/promociones`.
3. Completar formulario (código único, % descuento, vigencia, segmentación) y guardar.
4. Verificar mensaje de éxito y aparición en tabla.
5. Confirmar por base o API `/api/promociones` que el registro existe.

## 2. Envío de correo al crear promoción
1. Configurar SMTP válido (o usar logs) y asegurarse de que los destinatarios tienen `recibirPromociones=true`.
2. Crear nueva promoción aplicable a todos.
3. Revisar logs de backend (`NotificacionCorreoService`) o casilla para cada destinatario.
4. Repetir creando promo segmentada y validar que solo los clientes seleccionados la reciben.

## 3. Scheduler semanal
1. Ajustar `greedy.mail.promociones.cron` a `*/1 * * * * *` en `application.properties` (temporal).
2. Levantar backend; verificar que cada minuto se ejecuta `enviarPromocionesActivas` en logs.
3. Restaurar cron original al finalizar.

## 4. Alquiler con promoción aplicada
1. En UI de gestión de alquileres seleccionar cliente con promoción vigente.
2. Revisar panel “Promociones vigentes” para confirmar código.
3. Crear alquiler ingresando el código.
4. Verificar mensaje de éxito y que en la tabla de alquileres aparezca tarjeta con código/montos.
5. En la tabla de facturas del mismo cliente confirmar que el detalle refleja promoción y totales.

## 5. Alquiler con código inválido
1. Crear alquiler ingresando un código inexistente o vencido.
2. Asegurarse de recibir alerta de error en UI (mensaje del backend) y que el formulario mantiene datos.

## 6. Facturas con promoción aplicada
1. Abrir `/gestion/facturas`, seleccionar cliente con facturas generadas.
2. Confirmar que cada tarjeta muestra bloque con monto original, descuento y total cuando hay promoción.
3. Repetir en `/cliente/mis-facturas` y validar información equivalente.

## 7. Opt-in / Opt-out
1. Registrar nuevo cliente marcando/desmarcando “Deseo recibir promociones”.
2. Consultar API `/api/clientes/{id}` o UI para confirmar el flag.
3. Crear promoción aplicable a todos y verificar que solo los clientes con opt-in reciben correo.
