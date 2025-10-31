// Minimal JS stub for gestion-usuarios UI interactions
document.addEventListener('DOMContentLoaded', function () {
  const personaTipoSelect = document.getElementById('personaTipoSelect');
  const clientePanel = document.getElementById('cliente-panel');
  const empleadoPanel = document.getElementById('empleado-panel');

  function updatePersonaPanels() {
    const val = personaTipoSelect.value;
    if (val === 'CLIENTE') {
      clientePanel.classList.remove('d-none');
      empleadoPanel.classList.add('d-none');
    } else if (val === 'EMPLEADO') {
      empleadoPanel.classList.remove('d-none');
      clientePanel.classList.add('d-none');
    } else {
      clientePanel.classList.add('d-none');
      empleadoPanel.classList.add('d-none');
    }
  }

  if (personaTipoSelect) {
    personaTipoSelect.addEventListener('change', updatePersonaPanels);
    updatePersonaPanels();
  }

  // Reset button basic behavior
  const resetBtn = document.getElementById('usuario-reset');
  if (resetBtn) {
    resetBtn.addEventListener('click', function () {
      const form = document.getElementById('usuario-form');
      if (form) form.reset();
      updatePersonaPanels();
      // repoblar select de nacionalidades tras reset
      populateNacionalidades();
    });
  }

  // Placeholder: wire up submit handler (to be implemented with API/backend)
  const usuarioForm = document.getElementById('usuario-form');
  if (usuarioForm) {
    usuarioForm.addEventListener('submit', function (e) {
      e.preventDefault();
      // collect minimal data and log (replace with real logic)
      const data = {
        nombreUsuario: document.getElementById('nombreUsuario').value,
        rol: document.getElementById('usuarioRol').value,
        nacionalidad: document.getElementById('nacionalidadSelect')?.value || null,
      };
      console.log('Guardar usuario (stub):', data);
      alert('Función de guardar usuario (stub). Implementar la llamada al backend.');
    });
  }

  // Poblar el select de nacionalidades si la función está disponible
  function populateNacionalidades() {
    const select = document.getElementById('nacionalidadSelect');
    if (!select) return;
    // intentar usar la API de gestion-nacionalidades (localStorage stub)
    if (window.listarNacionalidadActiva) {
      const list = window.listarNacionalidadActiva();
      // limpiar
      select.innerHTML = '<option value="">-- Seleccione --</option>';
      list.forEach(n => {
        const opt = document.createElement('option');
        opt.value = n.id;
        opt.textContent = n.nombre;
        select.appendChild(opt);
      });
    }
  }

  // poblar al inicio
  populateNacionalidades();
});
