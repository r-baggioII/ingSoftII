(function () {
  function normaliseContextPath(path) {
    if (!path || path === '/') return '';
    return path.endsWith('/') ? path.slice(0, -1) : path;
  }

  function buildUrl(path) {
    const raw = document.body ? document.body.dataset.contextPath || '' : '';
    const base = normaliseContextPath(raw);
    if (!path) return base || '';
    const clean = path.startsWith('/') ? path : '/' + path;
    return base ? (base + clean) : clean;
  }

  document.addEventListener('DOMContentLoaded', function () {
    const botones = document.querySelectorAll('[data-action="enviar-saludo-socios"]');
    if (!botones.length) {
      return;
    }

    botones.forEach(function (boton) {
      const tarjeta = boton.closest('[data-correo-socios-card]');
      const estado = tarjeta ? tarjeta.querySelector('[data-role="estado-envio-socios"]') : null;

      boton.addEventListener('click', async function () {
        boton.disabled = true;
        if (estado) {
          estado.textContent = 'Enviando correos...';
        }
        try {
          const respuesta = await fetch(buildUrl('/api/admin/correos/socios/saludo'), {
            method: 'POST',
            headers: { 'X-Requested-With': 'XMLHttpRequest' },
            credentials: 'same-origin'
          });

          let data = null;
          try {
            data = await respuesta.json();
          } catch (jsonError) {
            data = null;
          }

          if (!respuesta.ok) {
            const mensaje = data && data.mensaje ? data.mensaje : 'No se pudo enviar el correo.';
            if (estado) {
              estado.textContent = mensaje;
            }
            return;
          }

          const enviados = data && typeof data.enviados === 'number' ? data.enviados : 0;
          if (estado) {
            estado.textContent = enviados > 0
              ? `Saludo enviado a ${enviados} socio${enviados === 1 ? '' : 's'}.`
              : 'No había socios para enviar el saludo.';
          }
        } catch (error) {
          if (estado) {
            estado.textContent = 'Ocurrió un error al enviar el correo.';
          }
        } finally {
          boton.disabled = false;
        }
      });
    });
  });
})();
