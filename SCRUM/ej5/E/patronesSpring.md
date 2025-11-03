## Patrones de Diseño Implementados

A continuación, se describen los patrones de diseño clave y su representación específica dentro de la arquitectura de Spring Security.

### Cadena de Responsabilidad (Chain of Responsibility)

Este es el patrón arquitectónico central para la seguridad web en Spring.

* **Representación:** Se implementa a través del `SecurityFilterChain`. Una solicitud HTTP entrante no es manejada por un único objeto monolítico, sino que atraviesa una cadena de filtros (como `CsrfFilter`, `UsernamePasswordAuthenticationFilter`, `AuthorizationFilter`).
* **Funcionamiento:** Cada filtro inspecciona la solicitud y decide si puede manejarla, si debe denegar el acceso (interrumpiendo la cadena) o si debe delegar la solicitud al siguiente filtro en la cadena.

### Estrategia (Strategy)

Este patrón se utiliza para definir una familia de algoritmos (en este caso, de autenticación), encapsular cada uno y hacerlos intercambiables.

* **Representación:** El `AuthenticationManager` (específicamente la implementación `ProviderManager`) actúa como el contexto. No contiene lógica de autenticación en sí mismo, sino que gestiona una lista de "estrategias" implementadas como `AuthenticationProvider`.
* **Funcionamiento:** Existen múltiples estrategias (ej. `DaoAuthenticationProvider` para bases de datos, `LdapAuthenticationProvider` para LDAP, `JwtAuthenticationProvider` para tokens JWT). El `ProviderManager` delega el intento de autenticación al proveedor (estrategia) que sea capaz de manejar el tipo de credencial presentada.

### Proxy

El patrón Proxy proporciona un intermediario (un sustituto) para otro objeto, permitiendo controlar el acceso al mismo. Es la base de la seguridad a nivel de método.

* **Representación:** Se implementa mediante la Programación Orientada a Aspectos (AOP) de Spring.
* **Funcionamiento:** Cuando se anota un método (p.ej., `@PreAuthorize("hasRole('ADMIN')")`), Spring no inyecta el bean del servicio directamente, sino un *proxy* que lo envuelve. Este proxy intercepta la llamada, verifica los permisos del usuario actual contra el `SecurityContext` y, solo si la verificación es exitosa, delega la ejecución al método real del objeto.

### Constructor (Builder)

Este patrón se utiliza para simplificar la creación de objetos complejos, permitiendo una configuración fluida y paso a paso.

* **Representación:** Es el mecanismo estándar para la configuración moderna de Spring Security basada en Java, utilizando el objeto `HttpSecurity`.
* **Funcionamiento:** En lugar de instanciar y configurar manualmente un objeto `SecurityFilterChain` complejo, el desarrollador utiliza métodos encadenados en el *builder* `HttpSecurity` (ej. `.authorizeHttpRequests(...)`, `.formLogin(...)`, `.csrf(...)`). Finalmente, se invoca el método `build()` para que el *builder* ensamble el objeto final configurado.

### Singleton (con estrategia Thread-Local)

Este patrón se utiliza para gestionar el contexto de seguridad global del usuario autenticado.

* **Representación:** El `SecurityContextHolder` es el componente que almacena el `SecurityContext` (el cual contiene el objeto `Authentication` del usuario).
* **Funcionamiento:** Para manejar la concurrencia en aplicaciones web, el `SecurityContextHolder` no es un Singleton global simple. Por defecto, utiliza una estrategia `ThreadLocal`. Esto asegura que cada hilo de ejecución (cada solicitud HTTP) tenga su propia copia aislada del `SecurityContext`, previniendo que las sesiones de usuarios concurrentes interfieran entre sí.