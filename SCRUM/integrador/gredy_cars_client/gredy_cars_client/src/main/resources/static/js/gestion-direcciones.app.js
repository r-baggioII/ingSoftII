// Backend-connected management UI (single page)
(function () {
  const csrfToken = document.querySelector('meta[name="_csrf"]')?.content || '';
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content || 'X-CSRF-TOKEN';

  async function api(path, options = {}) {
    const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
    if (['POST', 'PUT', 'DELETE'].includes((options.method || 'GET').toUpperCase())) headers[csrfHeader] = csrfToken;
    const res = await fetch(path, Object.assign({}, options, { headers }));
    if (!res.ok) throw new Error((await res.text()) || (res.status + ' ' + res.statusText));
    if (res.status === 204) return null;
    const ct = res.headers.get('content-type') || '';
    return ct.includes('application/json') ? res.json() : res.text();
  }

  let cache = { paises: [], provincias: [], departamentos: [], localidades: [], direcciones: [] };

  function setOptions(select, items, getValue, getText, placeholder) {
    if (!select) return;
    select.innerHTML = '';
    const first = document.createElement('option');
    first.value = '';
    first.textContent = placeholder;
    select.appendChild(first);
    items.forEach(it => {
      const o = document.createElement('option');
      o.value = getValue(it);
      o.textContent = getText(it);
      select.appendChild(o);
    });
  }

  function renderPaises() {
    const tbody = document.getElementById('pais-table');
    if (!tbody) return;
    tbody.innerHTML = '';
    cache.paises.forEach(p => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${p.nombre || ''}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-secondary" data-action="edit-pais" data-id="${p.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger ms-1" data-action="del-pais" data-id="${p.id}">Eliminar</button>
        </td>`;
      tbody.appendChild(tr);
    });
    setOptions(document.getElementById('provincia-pais-select'), cache.paises, p => p.id, p => p.nombre, 'País...');
    setOptions(document.getElementById('direccion-pais'), cache.paises, p => p.id, p => p.nombre, 'País');
  }

  function renderProvincias() {
    const tbody = document.getElementById('provincia-table');
    if (!tbody) return;
    tbody.innerHTML = '';
    cache.provincias.forEach(pr => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${pr.nombre || ''}</td>
        <td>${(pr.pais && pr.pais.nombre) || ''}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-secondary" data-action="edit-prov" data-id="${pr.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger ms-1" data-action="del-prov" data-id="${pr.id}">Eliminar</button>
        </td>`;
      tbody.appendChild(tr);
    });
    setOptions(document.getElementById('departamento-provincia-select'), cache.provincias, r => r.id, r => r.nombre, 'Provincia...');
    setOptions(document.getElementById('direccion-provincia'), cache.provincias, r => r.id, r => r.nombre, 'Provincia');
  }

  function renderDepartamentos() {
    const tbody = document.getElementById('departamento-table');
    if (!tbody) return;
    tbody.innerHTML = '';
    cache.departamentos.forEach(dep => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${dep.nombre || ''}</td>
        <td>${(dep.provincia && dep.provincia.nombre) || ''}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-secondary" data-action="edit-dep" data-id="${dep.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger ms-1" data-action="del-dep" data-id="${dep.id}">Eliminar</button>
        </td>`;
      tbody.appendChild(tr);
    });
    setOptions(document.getElementById('localidad-departamento-select'), cache.departamentos, d => d.id, d => d.nombre, 'Depto...');
    setOptions(document.getElementById('direccion-departamento'), cache.departamentos, d => d.id, d => d.nombre, 'Departamento');
  }

  function renderLocalidades() {
    const tbody = document.getElementById('localidad-table');
    if (!tbody) return;
    tbody.innerHTML = '';
    cache.localidades.forEach(loc => {
      const dep = loc.departamento || {};
      const prov = dep.provincia || {};
      const pais = prov.pais || {};
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${loc.nombre || ''}</td>
        <td>${dep.nombre || ''}</td>
        <td>${prov.nombre || ''}</td>
        <td>${pais.nombre || ''}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-secondary" data-action="edit-loc" data-id="${loc.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger ms-1" data-action="del-loc" data-id="${loc.id}">Eliminar</button>
        </td>`;
      tbody.appendChild(tr);
    });
  }

  function renderDirecciones() {
    const tbody = document.getElementById('direccion-table');
    if (!tbody) return;
    tbody.innerHTML = '';
    cache.direcciones.forEach(d => {
      const loc = d.localidad || {};
      const dep = (loc && loc.departamento) || {};
      const prov = (dep && dep.provincia) || {};
      const pais = (prov && prov.pais) || {};
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${(d.calle || '') + ' ' + (d.numeracion || '')}</td>
        <td>${loc.nombre || ''}</td>
        <td>${dep.nombre || ''}</td>
        <td>${prov.nombre || ''}</td>
        <td>${pais.nombre || ''}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-secondary" data-action="edit-dir" data-id="${d.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger ms-1" data-action="del-dir" data-id="${d.id}">Eliminar</button>
        </td>`;
      tbody.appendChild(tr);
    });
  }

  async function loadPaises() { cache.paises = await api('/gestion/api/paises'); renderPaises(); }
  async function loadProvincias(paisId) { cache.provincias = paisId ? await api(`/gestion/api/provincias/pais/${paisId}`) : await api('/gestion/api/provincias'); renderProvincias(); }
  async function loadDepartamentos(provinciaId) { cache.departamentos = provinciaId ? await api(`/gestion/api/departamentos/provincia/${provinciaId}`) : await api('/gestion/api/departamentos'); renderDepartamentos(); }
  async function loadLocalidades(departamentoId) { cache.localidades = departamentoId ? await api(`/gestion/api/localidades/departamento/${departamentoId}`) : await api('/gestion/api/localidades'); renderLocalidades(); }
  async function loadDirecciones() { cache.direcciones = await api('/gestion/api/direcciones'); renderDirecciones(); }

  function resetForm(form) { if (!form) return; form.reset(); const hid = form.querySelector('input[type="hidden"][name="id"], input[type="hidden"][id$="-id"]'); if (hid) hid.value = ''; }

  document.addEventListener('DOMContentLoaded', () => {
    loadPaises().then(() => Promise.all([loadProvincias(), loadDepartamentos(), loadLocalidades(), loadDirecciones()]));

    // PAISES
    const paisForm = document.getElementById('pais-form');
    paisForm.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const id = document.getElementById('pais-id').value || '';
      const nombre = paisForm.querySelector('input[name="nombre"]').value.trim();
      if (!nombre) return;
      const payload = { nombre };
      if (id) await api(`/gestion/api/paises/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/paises', { method: 'POST', body: JSON.stringify(payload) });
      await loadPaises();
      resetForm(paisForm);
    });
    document.getElementById('pais-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-pais') { if (confirm('¿Eliminar país?')) { await api(`/gestion/api/paises/${id}`, { method: 'DELETE' }); await loadPaises(); } }
      if (btn.dataset.action === 'edit-pais') {
        const p = cache.paises.find(x => String(x.id) === String(id));
        if (p) { document.getElementById('pais-id').value = p.id; paisForm.querySelector('input[name="nombre"]').value = p.nombre || ''; }
      }
    });

    // PROVINCIAS
    const provinciaForm = document.getElementById('provincia-form');
    provinciaForm.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const id = document.getElementById('provincia-id').value || '';
      const nombre = provinciaForm.querySelector('input[name="nombre"]').value.trim();
      const idPais = document.getElementById('provincia-pais-select').value;
      if (!nombre || !idPais) return;
      const payload = { nombre, pais: { id: Number(idPais) } };
      if (id) await api(`/gestion/api/provincias/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/provincias', { method: 'POST', body: JSON.stringify(payload) });
      await loadProvincias();
      resetForm(provinciaForm);
    });
    document.getElementById('provincia-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-prov') { if (confirm('¿Eliminar provincia?')) { await api(`/gestion/api/provincias/${id}`, { method: 'DELETE' }); await loadProvincias(); } }
      if (btn.dataset.action === 'edit-prov') {
        const pr = cache.provincias.find(x => String(x.id) === String(id));
        if (pr) {
          document.getElementById('provincia-id').value = pr.id;
          provinciaForm.querySelector('input[name="nombre"]').value = pr.nombre || '';
          document.getElementById('provincia-pais-select').value = pr.pais?.id || '';
        }
      }
    });

    // DEPARTAMENTOS
    const departamentoForm = document.getElementById('departamento-form');
    departamentoForm.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const id = document.getElementById('departamento-id').value || '';
      const nombre = departamentoForm.querySelector('input[name="nombre"]').value.trim();
      const idProvincia = document.getElementById('departamento-provincia-select').value;
      if (!nombre || !idProvincia) return;
      const payload = { nombre, provincia: { id: Number(idProvincia) } };
      if (id) await api(`/gestion/api/departamentos/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/departamentos', { method: 'POST', body: JSON.stringify(payload) });
      await loadDepartamentos();
      resetForm(departamentoForm);
    });
    document.getElementById('departamento-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-dep') { if (confirm('¿Eliminar departamento?')) { await api(`/gestion/api/departamentos/${id}`, { method: 'DELETE' }); await loadDepartamentos(); } }
      if (btn.dataset.action === 'edit-dep') {
        const dep = cache.departamentos.find(x => String(x.id) === String(id));
        if (dep) {
          document.getElementById('departamento-id').value = dep.id;
          departamentoForm.querySelector('input[name="nombre"]').value = dep.nombre || '';
          document.getElementById('departamento-provincia-select').value = dep.provincia?.id || '';
        }
      }
    });

    // LOCALIDADES
    const localidadForm = document.getElementById('localidad-form');
    localidadForm.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const id = document.getElementById('localidad-id').value || '';
      const nombre = document.getElementById('localidad-nombre').value.trim();
      const codigoPostal = document.getElementById('localidad-cp').value.trim();
      const idDepartamento = document.getElementById('localidad-departamento-select').value;
      if (!nombre || !idDepartamento) return;
      const payload = { nombre, codigoPostal, departamento: { id: Number(idDepartamento) } };
      if (id) await api(`/gestion/api/localidades/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/localidades', { method: 'POST', body: JSON.stringify(payload) });
      await loadLocalidades();
      resetForm(localidadForm);
    });
    document.getElementById('localidad-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-loc') { if (confirm('¿Eliminar localidad?')) { await api(`/gestion/api/localidades/${id}`, { method: 'DELETE' }); await loadLocalidades(); } }
      if (btn.dataset.action === 'edit-loc') {
        const loc = cache.localidades.find(x => String(x.id) === String(id));
        if (loc) {
          document.getElementById('localidad-id').value = loc.id;
          document.getElementById('localidad-nombre').value = loc.nombre || '';
          document.getElementById('localidad-cp').value = loc.codigoPostal || '';
          document.getElementById('localidad-departamento-select').value = loc.departamento?.id || '';
        }
      }
    });

    // DIRECCIONES
    const direccionForm = document.getElementById('direccion-form');
    direccionForm.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const id = document.getElementById('direccion-id').value || '';
      const payload = {
        calle: document.getElementById('direccion-calle').value.trim(),
        numeracion: document.getElementById('direccion-numeracion').value.trim(),
        barrio: document.getElementById('direccion-barrio').value.trim(),
        pisoCasa: document.getElementById('direccion-casa').value.trim(),
        puertaManzana: document.getElementById('direccion-manzana').value.trim(),
        observacion: document.getElementById('direccion-referencia').value.trim(),
        localidad: { id: Number(document.getElementById('direccion-localidad').value) }
      };
      if (!payload.calle || !payload.numeracion || !payload.localidad.id) return;
      if (id) await api(`/gestion/api/direcciones/${id}`, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/direcciones', { method: 'POST', body: JSON.stringify(payload) });
      await loadDirecciones();
      resetForm(direccionForm);
    });
    document.getElementById('direccion-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-dir') { if (confirm('¿Eliminar dirección?')) { await api(`/gestion/api/direcciones/${id}`, { method: 'DELETE' }); await loadDirecciones(); } }
      if (btn.dataset.action === 'edit-dir') {
        const d = cache.direcciones.find(x => String(x.id) === String(id));
        if (d) {
          document.getElementById('direccion-id').value = d.id;
          document.getElementById('direccion-calle').value = d.calle || '';
          document.getElementById('direccion-numeracion').value = d.numeracion || '';
          document.getElementById('direccion-barrio').value = d.barrio || '';
          document.getElementById('direccion-manzana').value = d.puertaManzana || '';
          document.getElementById('direccion-casa').value = d.pisoCasa || '';
          document.getElementById('direccion-referencia').value = d.observacion || '';
          const locId = d.localidad?.id || '';
          if (locId) document.getElementById('direccion-localidad').value = String(locId);
        }
      }
    });

    // Dirección dependent selects
    const dirPaisSel = document.getElementById('direccion-pais');
    const dirProvSel = document.getElementById('direccion-provincia');
    const dirDepSel = document.getElementById('direccion-departamento');
    const dirLocSel = document.getElementById('direccion-localidad');

    dirPaisSel.addEventListener('change', async () => {
      const pid = dirPaisSel.value;
      const provs = pid ? await api(`/gestion/api/provincias/pais/${pid}`) : [];
      setOptions(dirProvSel, provs, p => p.id, p => p.nombre, 'Provincia');
      setOptions(dirDepSel, [], d => d.id, d => d.nombre, 'Departamento');
      setOptions(dirLocSel, [], l => l.id, l => l.nombre, 'Localidad');
    });
    dirProvSel.addEventListener('change', async () => {
      const prid = dirProvSel.value;
      const deps = prid ? await api(`/gestion/api/departamentos/provincia/${prid}`) : [];
      setOptions(dirDepSel, deps, d => d.id, d => d.nombre, 'Departamento');
      setOptions(dirLocSel, [], l => l.id, l => l.nombre, 'Localidad');
    });
    dirDepSel.addEventListener('change', async () => {
      const did = dirDepSel.value;
      const locs = did ? await api(`/gestion/api/localidades/departamento/${did}`) : [];
      setOptions(dirLocSel, locs, l => l.id, l => l.nombre, 'Localidad');
    });
  });
})();

