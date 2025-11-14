Link a docs ->  https://docs.google.com/document/d/1Ajt8lF5SQiMqg2sWt1hUcqmT5VeyyQmZhHknsU5En-M/edit?usp=sharing


**Presupuesto**							Mendoza, 21 de Octubre de 2025

**Universidad Nacional de Cuyo**

De nuestra consideración:

Acorde a lo detallado oportunamente hemos desarrollado la presente propuesta que ponemos a su disposición para su evaluación. En la misma encontrarán condiciones sujetas a lo solicitado.

Desde ya nos encontramos a su disposición por cualquier consulta o necesidad de ampliación que pudiera surgir.

Atte. GreedyTeam

**1\) Situación Actual**

**1.1) Alcance de las Necesidades**

Software de gestión integral para un sistema de publicaciones académicas que permita administrar las operaciones diarias del establecimiento. El sistema facilitará el registro de publicaciones, control de usuarios (investigadores y administrativos), gestión de borradores, y organización de múltiples publicaciones. La solución está diseñada para optimizar los procesos operativos y mantener un control preciso de todas las transacciones comerciales del negocio.

**2\) CARACTERÍSTICAS DE LA SOLUCIÓN A DESARROLLAR**  
**2.1) Características Técnicas**

* Arquitectura en cuatro capas  
* Compatible con cualquier base de datos relacional moderna  
* Garantía de integridad de la información

**2.2) Sistema con Tecnología Java 100%**

* Interfaz visual intuitiva para el usuario  
* Multiplataforma: se ejecuta en cualquier sistema operativo  
* Lógica de negocio centralizada

**2.3) Soportado en Bases de Datos Relacionales**

* Asegura integridad de la información  
* Facilita la extracción y consulta de datos  
* Permite acceso remoto seguro  
* Base sólida para futuras aplicaciones de e-business, CRM y análisis de datos

**2.4) Herramientas de Desarrollo**

* Visual Studio Code  
* Servidor de Base de Datos MySQL

Tanto el lenguaje de programación como el motor de bases de datos son herramientas ampliamente utilizadas, difundidas y documentadas. La mayoría de proveedores de alojamiento ofrecen esta tecnología y cualquier empresa de desarrollo de software tiene acceso a extensa documentación sobre las mismas.

Esto facilita tanto el cambio de proveedor de alojamiento como el de empresa de desarrollo de software si el cliente así lo estima oportuno. Su carácter de Software Libre elimina cualquier costo en licencias de uso o desarrollo.

**3\) PROPUESTA TÉCNICA**  
**3.1) Necesidades del Cliente e Historias de Usuario**

A continuación se detallan las funcionalidades que compondrán el sistema de publicaciones académicas, presentadas como historias de usuario:

**HIST-001 — Crear Personas**

\*   **Como**. Administrativo  
\*   **Quiero:** poder crear nuevas personas, tanto investigadores como otros administrativos  
\*   **Para:** que puedan comenzar a crear publicaciones.

**Criterios de Aceptación**

\*   **Creación Exitosa:** Dado un Admin en el formulario de "Crear Usuario", cuando complete los campos obligatorios (nombre, email, rol) y guarde, el nuevo usuario se creará en el sistema. El nuevo usuario debe poder iniciar sesión con las credenciales creadas.  
\*   **Asignación de Permisos:** Cuando el nuevo usuario inicie sesión, debe ver las herramientas exactas de su rol.  
    \*   Si se crea como "Investigador", debe tener acceso a "Crear Publicación".  
    \*   Si se crea como "Administrativo", debe tener acceso a "Crear Publicación" Y a "Gestión de Usuarios".  
\*   **Validación de Email Único:** Si el Admin intenta crear un usuario con un email que ya existe en la base de datos, el sistema debe mostrar un error claro (ej. "El email ya está en uso") y no debe crear al usuario duplicado.  
\*   **Validación de Campos Obligatorios:** Si el Admin intenta guardar un nuevo usuario sin completar un campo obligatorio (como el "Rol" o el "Email"), el sistema debe mostrar un error en esa página y no procesar la solicitud.

**Estimación:** 1 hora  
**Costo:** $70.000 ARS

**HIST-002 — Eliminar Personas**

\*   **Como:** Administrativo  
\*   **Quiero:** poder desactivar la cuenta de un usuario  
\*   **Para:** revocar su acceso al sistema.

**Criterios de Aceptación**

\*   **Desactivación Exitosa:** Dado un Admin que selecciona la opción "Desactivar" en el perfil de un usuario activo, el sistema debe pedir una confirmación. Tras confirmar, el estado del usuario debe cambiar a "Inactivo".  
\*   **Revocación de Acceso:** Un usuario cuya cuenta ha sido "Desactivada" no debe poder iniciar sesión en el sistema. Si lo intenta, debe ver un mensaje claro.  
\*   **Integridad de Datos:** Si se desactiva a un "Investigador" o "Administrativo", sus publicaciones pasadas no deben borrarse del sistema. Las publicaciones deben seguir siendo visibles públicamente y deben mostrar el nombre del autor.  
\*   **Opción de Reactivación:** El Admin debe tener la capacidad de encontrar a los usuarios "Inactivos". Al encontrarlo, debe tener un botón para "Reactivar" la cuenta, permitiendo que el usuario vuelva a iniciar sesión.

**Estimación:** 1 hora  
**Costo:** $70.000 ARS

**HIST-003 — Modificar Personas**

\*   **Como:** Administrativo  
\*   **Quiero:** poder editar la información de un usuario existente  
\*   **Para:** mantener los datos del sistema actualizados, corregir errores o gestionar sus roles.

**Criterios de Aceptación**

\*   **Modificación Exitosa de Datos:** Dado un Admin que busca y selecciona a un usuario, cuando modifique datos y guarde los cambios, la información actualizada debe reflejarse inmediatamente en el perfil del usuario y en la lista principal de usuarios.  
\*   **Cambio de Rol y Permisos:** Cuando el Admin edite a un usuario y cambie su rol, los permisos de ese usuario deben actualizarse al instante. En su próximo inicio de sesión, el usuario verá la interfaz correspondiente a su nuevo rol.  
\*   **Restablecimiento de Contraseña:** El Admin no debe poder ver la contraseña actual del usuario. Sin embargo, debe existir una función que permita al Admin enviar un enlace de reseteo al email del usuario o asignar una contraseña temporal segura.  
\*   **Validación de Email en Edición:** Si el Admin cambia el email de un usuario "A" al email de un usuario "B" (que ya existe), el sistema debe mostrar un error de duplicidad y no permitir guardar el cambio.

**Estimación:** 1 hora  
**Costo:** $70.000 ARS

**HIST-004 — Editar Publicaciones**

\*   **Como:** Investigador o Administrativo  
\*   **Quiero:** poder editar mis propias publicaciones después de haberlas creado  
\*   **Para:** corregir errores, actualizar información o añadir nuevas imágenes.

**Criterios de Aceptación**

\*   **Permiso de Edición**: Al ver mis propias publicaciones, debo ver un botón de "Editar" que no es visible para otros usuarios (excepto el Admin).  
\*   **Formulario Precargado**: Al hacer clic en "Editar", debo ser llevado al mismo formulario de creación, pero con todos los campos (título, contenido, imagen) ya cargados con la información existente.  
\*   **Actualización**: Al "Guardar Cambios", el sistema debe actualizar la publicación existente, no crear una nueva.  
\*   **Actualización de Fecha**: El sistema podría mostrar una fecha de "Última actualización" junto a la fecha de creación original.

**Estimación**: 2 horas  
**Costo**: $140.000 ARS

**HIST-005 — Crear Publicación**

\*   **Como**: Administrativo o Investigador  
\*   **Quiero**: poder crear publicaciones con mi nombre como autor  
\*   **Para**: que otros usuarios puedan verla.

**Criterios de Aceptación**

\*   **Creación y Autoría Automática**: Dado un "Administrativo" o "Investigador" con sesión activa, cuando complete los campos requeridos y publique, el sistema guardará el post asignándole automáticamente como autor.  
\*   **Visibilidad del Autor en el Post**: Cuando cualquier usuario abra la publicación para leerla, deberá ver claramente el nombre del autor en la pantalla.  
\*   **Visibilidad de la Publicación en Listados:** Una vez que el autor haya creado el post exitosamente, otros usuarios deben poder encontrar y ver esa publicación en el listado principal o mediante la búsqueda.  
\*   **Validación de Campos Obligatorios**: Si el autor intenta publicar sin rellenar campos obligatorios, el sistema debe impedir la creación y mostrar un mensaje de error claro.

**Estimación**: 2 horas  
**Costo**: $140.000 ARS

**HIST-006 — Ver Publicaciones**

\*   **Como**: usuario  
\*   **Quiero**: ver las publicaciones  
\*   **Para**: poder mantenerme al tanto de las investigaciones y noticias académicas.

**Criterios de Aceptación**

\*   Que las publicaciones no tengan botón borrar.  
\*   Poder buscarlas por nombre, su autor, tipo de publicación.

**Estimación**: 1 hora  
**Costo**: $70.000 ARS

**HIST-007 — Listar Publicaciones**

\*   **Como:** Investigador o Administrativo  
\*   **Quiero:** tener un panel de control donde pueda ver una lista de mis publicaciones (publicadas y borradores)  
\*   **Para:** gestionar mi contenido de forma rápida sin tener que buscarlo en el sitio público.

**Criterios de Aceptación**

\*   **Vista "Mis Publicaciones":** Después de iniciar sesión, debe haber un enlace a "Mis Publicaciones".  
\*   **Listado y Estado:** Esta lista debe mostrar el título de mi post, la fecha y su estado ("Publicado" o "Borrador").  
\*   **Accesos Directos:** Desde esta lista, debo tener botones de acceso rápido para "Editar", "Eliminar" (si es mío) o "Ver" (público).

**Estimación:** 2 horas  
**Costo:** $140.000 ARS

**HIST-008 — Crear Borradores**

\*   **Como:** Investigador o Administrativo  
\*   **Quiero:** poder guardar una publicación como "Borrador" sin publicarla  
\*   **Para:** poder trabajar en ella en múltiples sesiones antes de que esté lista para todos.

**Criterios de Aceptación**

\*   **Doble Botón:** En el formulario de creación, debe haber dos botones: "Publicar" (la hace pública) y "Guardar Borrador".  
\*   **Invisibilidad Pública:** Si se guarda como "Borrador", la publicación no debe aparecer en la lista pública de noticias.  
\*   **Acceso Privado:** Debo tener una sección en mi panel de control (ej. "Mis Borradores") donde pueda ver, editar y (finalmente) publicar esos borradores.

**Estimación:** 2 horas  
**Costo:** $140.000 ARS

**Documentación, capacitación y garantía**

Junto con el software completo se entregará una documentación técnica sobre el mismo y se capacitará para su uso. También cuenta con una garantía de 3 meses cuyos alcances se detallan en su propio apartado.

**Costo:** $210.000 ARS

**3.1) Resumen de Estimaciones y Costos**

| ID | Historia de Usuario | Horas | Costo (ARS) |
| ----- | ----- | ----- | ----- |
| **HIST-001** | **Crear Personas** | **1** | **$70000** |
| **HIST-002** | **Eliminar Personas** | **1** | **$70000** |
| **HIST-003** | **Modificar Personas** | **1** | **$70000** |
| **HIST-004** | **Editar Publicaciones**\* | **2** | **$140000** |
| **HIST-005** | **Crear Publicación** | **2** | **$140000** |
| **HIST-006** | **Ver Publicaciones** | **1** | **$70000** |
| **HIST-007** | **Listar Publicaciones** | **2** | **$140000** |
| **HIST-008** | **Crear Borradores** | **2** | **$140000** |
| **HIST-009** | **Documentación, Capacitación y Garantía** | **3** | **$210000** |
| **TOTAL** |  | **15** | **$1050000** |

**3.2) Código Fuente y Capacitación**

Como parte del proyecto se incluye la entrega del código fuente en todos sus componentes, así como una sesión de capacitación coordinada para explicar el funcionamiento del sistema y sus funcionalidades principales.

**3.3) Cliente Web**

El sistema web podrá visualizarse en los navegadores reconocidos mundialmente que brinden soporte a HTML 5, entre los que se encuentran Mozilla Firefox y Google Chrome en sus versiones más recientes, e Internet Explorer 9 o superior.

**3.4) Soporte Técnico**

Las tareas técnicas como instalaciones adicionales de módulos, reinstalaciones o migraciones no forman parte del mantenimiento estándar ofrecido. El alcance y costo de estos servicios se detallan en la propuesta técnica y comercial.

**3.5) Servicios Adicionales**

Toda solicitud del cliente que no se encuentre contemplada en el relevamiento inicial y los alcances definidos será considerada servicio adicional. Esto implica un cambio en el plan de ejecución, en los alcances del sistema o el desarrollo de nuevos módulos, lo cual conlleva un costo adicional que será presupuestado oportunamente.

**3.6) Garantía y Soporte**  
**Garantía Estándar: Pruebas por parte del cliente**

Incluye una garantía de tres meses luego de implementado el software, donde todas las incidencias informadas por el cliente serán corregidas sin costo adicional. Finalizado este período, se extiende una garantía de dos meses más. Las incidencias reportadas después de este plazo serán evaluadas y cotizadas para determinar su resolución.

Quedan exentas de esta garantía las mejoras propuestas por el cliente, los problemas surgidos por el mal uso de la aplicación, inconvenientes con el servicio de hosting y los derivados del cambio de plataforma. Los gastos derivados de estas situaciones corren por cuenta del cliente.

Se garantiza al cliente los derechos de edición, distribución y reproducción de la aplicación.

**3.7) Exclusiones al Alcance del Proyecto**

Quedan fuera del alcance de esta oferta:

\*   La implementación de funcionalidades que no hayan sido especificadas en los apartados anteriores  
\*   Integraciones con sistemas externos tales como facturación electrónica AFIP, pasarelas de pago o sistemas de envío de correos electrónicos automatizados, las cuales serán cotizadas por separado  
\*   La instalación de equipamiento o sistemas necesarios para la correcta ejecución de la aplicación  
\*   Instalación de infraestructura de red, configuración de comunicaciones, adquisición de hardware y, en general, cualquier servicio asociado que no haya sido incluido en la oferta

**4.1) Costo Total del Proyecto**

**Costo de análisis, desarrollo, implementación, pruebas y capacitación según alcances definidos:**

**TOTAL: $1.050.000 ARS (Un millón cincuenta mil)**

**4.2) Plazo de Entrega**

El plazo de entrega será de **10 a 15 días hábiles** a partir de la recepción del pago inicial correspondiente al anticipo del proyecto.

**4.3) Forma de Pago**

\*   **50% de anticipo para iniciar el desarrollo:** $480.000 ARS (cuatrocientos ochenta mil pesos)  
\*   **50% contra entrega del producto final:** $480.000 ARS (cuatrocientos ochenta mil pesos)

**Métodos de pago aceptados:** Transferencia Bancaria, Mercado Pago

La falta de pago de alguna cuota por parte del cliente podría impactar en el tiempo originalmente establecido, así como en la entrega final del proyecto.

**4.4) Validez de la Oferta**

**15 días corridos** a partir de la fecha de emisión del presente presupuesto.

**4.5) Nota Legal**

La presente propuesta no representa ningún tipo de contrato vinculante entre las partes mencionadas y el cliente. Se ofrece al solo efecto de informar los alcances y costos del eventual desarrollo del sistema de gestión en base a lo solicitado por el cliente.

**5\) Prototipado del sistema.**

Se envía de forma adjunta un link a un prototipo del sistema.

[Prototipado](https://www.figma.com/make/GSEqWJNaNCqBsz7q1D3r3G/News-Publication-System?node-id=0-4&t=KPriY7z0CAul4IVW-1)

