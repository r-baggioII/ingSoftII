// gestion-nacionalidades.js
// Implementa en-memory (localStorage) un CRUD simple de Nacionalidad
(function () {
  const LS_KEY = 'greedy_nacionalidades_v1';

  function loadAll() {
    const raw = localStorage.getItem(LS_KEY);
    return raw ? JSON.parse(raw) : [];
  }

  function saveAll(list) {
    localStorage.setItem(LS_KEY, JSON.stringify(list));
  }

  function generarId() {
    // id simple: timestamp + random
    return Date.now().toString(36) + Math.random().toString(36).slice(2,8);
  }

  // API solicitada
  function crearNacionalidad(nombre) {
    validar(nombre);
    const list = loadAll();
    if (buscarNacionalidadPorNombre(nombre)) throw new Error('Ya existe una nacionalidad con ese nombre');
    const n = { id: generarId(), nombre: nombre.trim(), eliminado: false };
    list.push(n);
    saveAll(list);
    renderTable();
    return n;
  }

  function validar(nombre) {
    if (!nombre || String(nombre).trim().length < 2) throw new Error('Nombre inválido');
  }

  function buscarNacionalidad(id) {
    return loadAll().find(x => x.id === id) || null;
  }

  function buscarNacionalidadPorNombre(nombre) {
    return loadAll().find(x => x.nombre.toLowerCase() === String(nombre).trim().toLowerCase()) || null;
  }

  function modificarNacionalidad(id, nombre) {
    validar(nombre);
    const list = loadAll();
    const idx = list.findIndex(x => x.id === id);
    if (idx === -1) throw new Error('No encontrada');
    list[idx].nombre = nombre.trim();
    saveAll(list);
    renderTable();
    return list[idx];
  }

  function eliminarNacionalidad(id) {
    const list = loadAll();
    const idx = list.findIndex(x => x.id === id);
    if (idx === -1) throw new Error('No encontrada');
    // marcar eliminado
    list[idx].eliminado = true;
    saveAll(list);
    renderTable();
  }

  function listarNacionalidad() {
    return loadAll();
  }

  function listarNacionalidadActiva() {
    return loadAll().filter(x => !x.eliminado);
  }

  // Exportar funciones globalmente para que otras páginas (gestion-usuarios) puedan usarlas
  window.crearNacionalidad = crearNacionalidad;
  window.validarNacionalidad = validar;
  window.buscarNacionalidad = buscarNacionalidad;
  window.buscarNacionalidadPorNombre = buscarNacionalidadPorNombre;
  window.modificarNacionalidad = modificarNacionalidad;
  window.eliminarNacionalidad = eliminarNacionalidad;
  window.listarNacionalidad = listarNacionalidad;
  window.listarNacionalidadActiva = listarNacionalidadActiva;

  // UI wiring for gestion-nacionalidades.html
  function renderTable() {
    const body = document.getElementById('nacionalidad-table-body');
    if (!body) return;
    const rows = listarNacionalidad();
    body.innerHTML = '';
    rows.forEach(n => {
      const tr = document.createElement('tr');
      const nombreTd = document.createElement('td'); nombreTd.textContent = n.nombre;
      const activoTd = document.createElement('td'); activoTd.innerHTML = n.eliminado ? '<span class="badge bg-danger">No</span>' : '<span class="badge bg-success">Sí</span>';
      const actionsTd = document.createElement('td'); actionsTd.className = 'text-end';

      const btnEdit = document.createElement('button'); btnEdit.className = 'btn btn-sm btn-outline-primary me-2'; btnEdit.textContent = 'Editar';
      btnEdit.addEventListener('click', function () {
        const nombreInput = document.getElementById('nacionalidad-nombre');
        nombreInput.value = n.nombre;
        nombreInput.dataset.editId = n.id;
        nombreInput.focus();
      });

      const btnDelete = document.createElement('button'); btnDelete.className = 'btn btn-sm btn-outline-danger'; btnDelete.textContent = n.eliminado ? 'Restaurar' : 'Eliminar';
      btnDelete.addEventListener('click', function () {
        if (n.eliminado) {
          // restaurar
          const list = loadAll();
          const item = list.find(x => x.id === n.id);
          if (item) item.eliminado = false;
          saveAll(list);
          renderTable();
        } else {
          if (!confirm('Eliminar nacionalidad?')) return;
          eliminarNacionalidad(n.id);
        }
      });

      actionsTd.appendChild(btnEdit);
      actionsTd.appendChild(btnDelete);

      tr.appendChild(nombreTd);
      tr.appendChild(activoTd);
      tr.appendChild(actionsTd);
      body.appendChild(tr);
    });
  }

  // Form handling
  document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('nacionalidad-form');
    const nombreInput = document.getElementById('nacionalidad-nombre');
    const resetBtn = document.getElementById('nacionalidad-reset');
    renderTable();

    if (form) {
      form.addEventListener('submit', function (e) {
        e.preventDefault();
        const editId = nombreInput.dataset.editId;
        try {
          if (editId) {
            modificarNacionalidad(editId, nombreInput.value);
            delete nombreInput.dataset.editId;
          } else {
            crearNacionalidad(nombreInput.value);
          }
          nombreInput.value = '';
        } catch (err) {
          alert(err.message || String(err));
        }
      });
    }

    if (resetBtn) {
      resetBtn.addEventListener('click', function () {
        const ni = document.getElementById('nacionalidad-nombre');
        ni.value = '';
        delete ni.dataset.editId;
      });
    }
  });

})();
