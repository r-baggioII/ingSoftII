# Historias de Usuario — SocialApp 

**Resumen de estimación:** **15 horas** (suma de todas las historias)

---

## HU-01 — Registro / Iniciar sesión
**Como** usuario quiero poder registrarme e iniciar sesión en la app para tener una cuenta personal y usar la red social desde mi celular.

**Criterios de aceptación**
- Existe pantalla de registro con `username`, `email` y `contraseña`.
- Existe pantalla de inicio de sesión con `email/username` + `contraseña`.
- Después de iniciar sesión, el usuario llega al feed.
- Las contraseñas se validan (mínimo 8 caracteres).

**Estimación:** 2 horas

---

## HU-02 — Crear publicación (imagen + comentario)
**Como** usuario quiero crear una publicación que contenga una imagen y un comentario para compartir contenido con mis contactos.

**Criterios de aceptación**
- Interfaz para seleccionar foto (o tomar foto) y escribir un texto/caption.
- La publicación aparece en mi feed con la imagen y el texto.
- Se valida que la imagen exista; el texto puede ser opcional.

**Estimación:** 4 horas

---

## HU-03 — Ver feed de publicaciones
**Como** usuario quiero ver un feed con publicaciones de otros usuarios (ordenadas por fecha) para navegar contenido en mi celular.

**Criterios de aceptación**
- El feed muestra lista de publicaciones con imagen, autor, fecha y caption.
- Paginación / infinite scroll simulado (al menos carga inicial + "cargar más").
- Al tocar una publicación se abre su detalle.

**Estimación:** 1 hora

---

## HU-04 — Dar like a una publicación
**Como** usuario quiero indicar que me gusta una publicación para expresar aprobación.

**Criterios de aceptación**
- Botón/ícono de like en cada publicación.
- Al tocar cambia estado (like/unlike) y el contador se actualiza.
- Solo puede contabilizarse un like por usuario (en backend); en prototipo se simula toggle.

**Estimación:** 1 hora

---

## HU-05 — Comentar en una publicación
**Como** usuario quiero comentar en publicaciones de otros para interactuar.

**Criterios de aceptación**
- En la vista de detalle / en el feed se puede escribir un comentario.
- Los comentarios aparecen listados con autor y fecha.
- Se puede borrar/editar comentario propio (opcional en prototipo; se implementa añadir y mostrar).

**Estimación:** 1 hora

---

## HU-06 — Mensajería privada entre usuarios
**Como** usuario quiero enviar mensajes privados a otro usuario para conversar de forma directa.

**Criterios de aceptación**
- Existe pantalla de conversaciones (lista) y pantalla de chat con mensajes ordenados por fecha.
- Se puede escribir y enviar un mensaje; aparece en la conversación.
- Cada conversación tiene (simulado) exactamente dos participantes.

**Estimación:** 4 horas

---

## HU-07 — Ver perfil de usuario
**Como** usuario quiero ver mi propio perfil y el de otros para ver sus publicaciones y foto de perfil.

**Criterios de aceptación**
- Página de perfil con foto, username y lista de publicaciones del usuario.
- Botón para editar foto/biografía (edición real es opcional).

**Estimación:** 2 horas

---


- Total estimado: **15 horas**.

## Tablero a usar: Trello