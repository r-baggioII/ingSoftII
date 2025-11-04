// Backend-connected management UI for Direcciones (paises, provincias, departamentos, localidades, direcciones)
(function () {
  const csrfToken = document.querySelector('meta[name=_csrf]')?.content || '';
  const csrfHeader = document.querySelector('meta[name=_csrf_header]')?.content || 'X-CSRF-TOKEN';

  async function api(path, options = {}) {
    const headers = Object.assign({ 'Content-Type': 'application/json' }, options.headers || {});
    if (['POST', 'PUT', 'DELETE'].includes((options.method || 'GET').toUpperCase())) {
      headers[csrfHeader] = csrfToken;
    }
    const res = await fetch(path, Object.assign({}, options, { headers }));
    if (!res.ok) {
      const text = await res.text();
      throw new Error(text || (res.status + ' ' + res.statusText));
    }
    if (res.status === 204) return null;
    const ct = res.headers.get('content-type') || '';
    return ct.includes('application/json') ? res.json() : res.text();
  }

  // Cached lists for display names
  let cache = { paises: [], provincias: [], departamentos: [], localidades: [], direcciones: [] };

  function setOptions(select, items, getValue, getText, placeholder) {
    select.innerHTML = '';
    const opt = document.createElement('option');
    opt.value = '';
    opt.textContent = placeholder;
    select.appendChild(opt);
    items.forEach(it => {
      const o = document.createElement('option');
      o.value = getValue(it);
      o.textContent = getText(it);
      select.appendChild(o);
    });
  }

  function renderPaises() {
    const tbody = document.getElementById('pais-table');
    tbody.innerHTML = '';
    cache.paises.forEach(p => {
      const tr = document.createElement('tr');
      tr.innerHTML = ;
      tbody.appendChild(tr);
    });
    // selects
    const paisSel1 = document.getElementById('provincia-pais-select');
    const paisSel2 = document.getElementById('direccion-pais');
    setOptions(paisSel1, cache.paises, p => p.id, p => p.nombre, 'País...');
    setOptions(paisSel2, cache.paises, p => p.id, p => p.nombre, 'País');
  }

  function renderProvincias() {
    const tbody = document.getElementById('provincia-table');
    tbody.innerHTML = '';
    cache.provincias.forEach(pr => {
      const pais = pr.pais || {};
      const tr = document.createElement('tr');
      tr.innerHTML = ;
      tbody.appendChild(tr);
    });
    const provSel1 = document.getElementById('departamento-provincia-select');
    const provSel2 = document.getElementById('direccion-provincia');
    setOptions(provSel1, cache.provincias, r => r.id, r => r.nombre, 'Provincia...');
    setOptions(provSel2, cache.provincias, r => r.id, r => r.nombre, 'Provincia');
  }

  function renderDepartamentos() {
    const tbody = document.getElementById('departamento-table');
    tbody.innerHTML = '';
    cache.departamentos.forEach(dep => {
      const prov = dep.proxima || dep.provincia || {};
      const tr = document.createElement('tr');
      tr.innerHTML = ;
      tbody.appendChild(tr);
    });
    const depSel1 = document.getElementById('localidad-departamento-select');
    const depSel2 = document.getElementById('direccion-departamento');
    setOptions(depSel1, cache.departamentos, d => d.id, d => d.nombre, 'Depto...');
    setOptions(depSel2, cache.departamentos, d => d.id, d => d.nombre, 'Departamento');
  }

  function renderLocalidades() {
    const tbody = document.getElementById('localidad-table');
    tbody.innerHTML = '';
    cache.localidades.forEach(loc => {
      const dep = loc.departamento || {};
      const prov = (dep && dep.provincia) || {};
      const pais = (prov && prov.pais) || {};
      const tr = document.createElement('tr');
      tr.innerHTML = ;
      tbody.appendChild(tr);
    });
  }

  function renderDirecciones() {
    const tbody = document.getElementById('direccion-table');
    tbody.innerHTML = '';
    cache.direcciones.forEach(d => {
      const loc = d.localidad || {};
      const dep = (loc && loc.departamento) || {};
      const prov = (dep && dep.provincia) || {};
      const pais = (prov && prov.pais) || {};
      const tr = document.createElement('tr');
      tr.innerHTML = ;
      tbody.appendChild(tr);
    });
  }

  async function loadPaises() {
    cache.paises = await api('/gestion/api/paises');
    renderPaises();
  }
  async function loadProvincias(paisId) {
    cache.provincias = paisId ? await api() : await api('/gestion/api/provincias');
    renderProvincias();
  }
  async function loadDepartamentos(provinciaId) {
    cache.departamentos = provinciaId ? await api() : await api('/gestion/api/departamentos');
    renderDepartamentos();
  }
  async function loadLocalidades(departamentoId) {
    cache.localidades = departamentoId ? await api() : await api('/gestion/api/localidades');
    renderLocalidades();
  }
  async function loadDirecciones() {
    cache.direcciones = await api('/gestion/api/direcciones');
    renderDirecciones();
  }

  function resetForm(form) {
    form.reset();
    const hidden = form.querySelector('input[type=hidden][name=id], input[type=hidden][id$=-id]');
    if (hidden) hidden.value = '';
  }

  document.addEventListener('DOMContentLoaded', () => {
    // Initial loads
    loadPaises().then(() => Promise.all([loadProvincias(), loadDepartamentos(), loadLocalidades(), loadDirecciones()]));

    // PAISES
    const paisForm = document.getElementById('pais-form');
    paisForm.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const id = document.getElementById('pais-id').value || '';
      const nombre = paisForm.querySelector('input[name=nombre]').value.trim();
      if (!nombre) return;
      const payload = { nombre };
      if (id) await api(, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/paises', { method: 'POST', body: JSON.stringify(payload) });
      await loadPaises();
      resetForm(paisForm);
    });
    document.getElementById('pais-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-pais') {
        if (confirm('¿Eliminar país?')) { await api(, { method: 'DELETE' }); await loadPaises(); }
      }
      if (btn.dataset.action === 'edit-pais') {
        const p = cache.paises.find(x => String(x.id) === String(id));
        if (p) { document.getElementById('pais-id').value = p.id; paisForm.querySelector('input[name=nombre]').value = p.nombre || ''; }
      }
    });

    // PROVINCIAS
    const provinciaForm = document.getElementById('provincia-form');
    provinciaForm.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const id = document.getElementById('provincia-id').value || '';
      const nombre = provinciaForm.querySelector('input[name=nombre]').value.trim();
      const idPais = document.getElementById('provincia-pais-select').value;
      if (!nombre || !idPais) return;
      const payload = { nombre, pais: { id: Number(idPais) } };
      if (id) await api(, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/provincias', { method: 'POST', body: JSON.stringify(payload) });
      await loadProvincias();
      resetForm(provinciaForm);
    });
    document.getElementById('provincia-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-prov') {
        if (confirm('¿Eliminar provincia?')) { await api(, { method: 'DELETE' }); await loadProvincias(); }
      }
      if (btn.dataset.action === 'edit-prov') {
        const pr = cache.provincias.find(x => String(x.id) === String(id));
        if (pr) {
          document.getElementById('provincia-id').value = pr.id;
          provinciaForm.querySelector('input[name=nombre]').value = pr.nombre || '';
          document.getElementById('provincia-pais-select').value = pr.pais?.id || '';
        }
      }
    });

    // DEPARTAMENTOS
    const departamentoForm = document.getElementById('departamento-form');
    departamentoForm.addEventListener('submit', async (ev) => {
      ev.preventDefault();
      const id = document.getElementById('departamento-id').value || '';
      const nombre = departamentoForm.querySelector('input[name=nombre]').value.trim();
      const idProvincia = document.getElementById('departamento-provincia-select').value;
      if (!nombre || !idProvincia) return;
      const payload = { nombre, provincia: { id: Number(idProvincia) } };
      if (id) await api(, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/departamentos', { method: 'POST', body: JSON.stringify(payload) });
      await loadDepartamentos();
      resetForm(departamentoForm);
    });
    document.getElementById('departamento-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-dep') {
        if (confirm('¿Eliminar departamento?')) { await api(, { method: 'DELETE' }); await loadDepartamentos(); }
      }
      if (btn.dataset.action === 'edit-dep') {
        const dep = cache.departamentos.find(x => String(x.id) === String(id));
        if (dep) {
          document.getElementById('departamento-id').value = dep.id;
          departamentoForm.querySelector('input[name=nombre]').value = dep.nombre || '';
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
      if (id) await api(, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/localidades', { method: 'POST', body: JSON.stringify(payload) });
      await loadLocalidades();
      resetForm(localidadForm);
    });
    document.getElementById('localidad-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-loc') {
        if (confirm('¿Eliminar localidad?')) { await api(, { method: 'DELETE' }); await loadLocalidades(); }
      }
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
        // Map HTML fields to DTO names
        pisoCasa: document.getElementById('direccion-casa').value.trim(),
        puertaManzana: document.getElementById('direccion-manzana').value.trim(),
        observacion: document.getElementById('direccion-referencia').value.trim(),
        localidad: { id: Number(document.getElementById('direccion-localidad').value) }
      };
      if (!payload.calle || !payload.numeracion || !payload.localidad.id) return;
      if (id) await api(, { method: 'PUT', body: JSON.stringify(payload) });
      else await api('/gestion/api/direcciones', { method: 'POST', body: JSON.stringify(payload) });
      await loadDirecciones();
      resetForm(direccionForm);
    });
    document.getElementById('direccion-table').addEventListener('click', async (ev) => {
      const btn = ev.target.closest('button'); if (!btn) return;
      const id = btn.dataset.id;
      if (btn.dataset.action === 'del-dir') {
        if (confirm('¿Eliminar dirección?')) { await api(, { method: 'DELETE' }); await loadDirecciones(); }
      }
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

    // Dependent selects for Dirección
    const dirPaisSel = document.getElementById('direccion-pais');
    const dirProvSel = document.getElementById('direccion-provincia');
    const dirDepSel = document.getElementById('direccion-departamento');
    const dirLocSel = document.getElementById('direccion-localidad');

    dirPaisSel.addEventListener('change', async () => {
      const pid = dirPaisSel.value;
      const provs = pid ? await api() : [];
      setOptions(dirProvSel, provs, p => p.id, p => p.nombre, 'Provincia');
      setOptions(dirDepSel, [], d => d.id, d => d.nombre, 'Departamento');
      setOptions(dirLocSel, [], l => l.id, l => l.nombre, 'Localidad');
    });
    dirProvSel.addEventListener('change', async () => {
      const prid = dirProvSel.value;
      const deps = prid ? await api() : [];
      setOptions(dirDepSel, deps, d => d.id, d => d.nombre, 'Departamento');
      setOptions(dirLocSel, [], l => l.id, l => l.nombre, 'Localidad');
    });
    dirDepSel.addEventListener('change', async () => {
      const did = dirDepSel.value;
      const locs = did ? await api() : [];
      setOptions(dirLocSel, locs, l => l.id, l => l.nombre, 'Localidad');
    });
  });
})();

