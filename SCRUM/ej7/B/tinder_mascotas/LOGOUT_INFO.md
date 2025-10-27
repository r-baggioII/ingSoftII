# Solución al problema de Logout

## Problema identificado
Los enlaces `<a href="/logout">Salir</a>` en tus templates hacían logout por GET, pero Spring Security 6 requiere POST con CSRF token por defecto.

## Solución implementada
Modifiqué `SeguridadWeb.java` para aceptar logout tanto por GET como por POST:

```java
.logoutRequestMatcher(request -> 
    request.getServletPath().equals("/logout") && 
    (request.getMethod().equals("POST") || request.getMethod().equals("GET"))
)
```

Ahora tus enlaces GET funcionarán correctamente.

## Mejora recomendada (más segura)
Para mayor seguridad, conviene usar POST con formulario. Puedes reemplazar tus enlaces de logout con este código:

### Opción 1: Formulario inline (recomendado)
```html
<form method="post" action="/logout" style="display:inline;">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
    <button type="submit" class="nav-link" style="background:none;border:none;color:inherit;cursor:pointer;">
        Salir
    </button>
</form>
```

### Opción 2: Link con JavaScript
```html
<a class="nav-link" href="#" onclick="event.preventDefault(); document.getElementById('logout-form').submit();">
    Salir
</a>
<form id="logout-form" method="post" action="/logout" style="display:none;">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
</form>
```

### Opción 3: Mantener GET (actual - funciona pero menos seguro)
```html
<a class="nav-link" href="/logout" th:href="@{/logout}">Salir</a>
```

## Archivos modificados
- `src/main/java/com/example/tinder_mascotas/SeguridadWeb.java`
  - Agregado import `SecurityContextLogoutHandler`
  - Agregado `logoutRequestMatcher` que acepta GET y POST
  - Agregado `addLogoutHandler` para limpieza explícita del contexto

## Verificación
1. Reinicia la aplicación
2. Haz login
3. Haz clic en "Salir"
4. Ya NO deberías ver "intente nuevamente"
5. Intenta acceder a `/inicio` directamente - deberías ser redirigido a `/login`

## Templates con enlaces de logout
- `inicio.html` - línea 40
- `votos_explorar.html` - línea 27
- `votos_propios.html` - línea 27
- `votos_recibidos.html` - línea 27

Si quieres implementar la mejora de seguridad (POST), avísame y actualizo estos templates.
