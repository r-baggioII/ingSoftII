(function(){
  const $ = window.jQuery;

  // --- context path helpers (copy from panel-entidades style) ---
  function normaliseContextPath(path){
    if(!path) return '';
    if(path === '/') return '';
    return path.endsWith('/') ? path.slice(0, -1) : path;
  }
  const rawContextPath = document.body ? (document.body.dataset.contextPath || '') : '';
  const contextPath = normaliseContextPath(rawContextPath);
  function buildUrl(path){
    if(!path) return contextPath || '';
    const clean = path.startsWith('/') ? path : '/' + path;
    return contextPath ? `${contextPath}${clean}` : clean;
  }

  // --- utils ---
  async function requestJson(url, opts) {
    const res = await fetch(url, opts);
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) return res.json();
    return null;
  }
  async function sendJson(url, method, payload) {
    return requestJson(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload || {})
    });
  }
  function formatLabel(v){
    if(!v) return '';
    return v.toString().toLowerCase().replace(/_/g,' ').replace(/\b\w/g,c=>c.toUpperCase());
  }

  // --- state ---
  const state = {
    registros: [],
    editingId: null,
    selectedUsuarioId: null,
    socios: [],
    empleados: []
  };

  // --- DOM ---
  const dom = {
    form: document.getElementById('usuario-form'),
    reset: document.getElementById('usuario-reset'),
    searchInput: document.getElementById('usuario-search'),
    refreshBtn: document.getElementById('usuario-refresh'),
    tableBody: document.getElementById('usuario-table-body'),
    tipoVinculo: document.querySelector("select[name='tipoVinculo']"),
    vinculoSocioWrap: document.getElementById('vinculo-socio'),
    vinculoEmpleadoWrap: document.getElementById('vinculo-empleado'),
    socioSelect: document.querySelector("select[name='socioId']"),
    empleadoSelect: document.querySelector("select[name='empleadoId']")
  };

  // --- init ---
  document.addEventListener('DOMContentLoaded', () => {
    attachEvents();
    loadVinculoOptions();
    listar();
  });

  function attachEvents(){
    if (dom.tipoVinculo) {
      dom.tipoVinculo.addEventListener('change', onTipoVinculoChange);
    }
    if (dom.form) dom.form.addEventListener('submit', onFormSubmit);
    if (dom.reset) dom.reset.addEventListener('click', resetForm);
    if (dom.refreshBtn) dom.refreshBtn.addEventListener('click', render);
    if (dom.searchInput) dom.searchInput.addEventListener('input', render);
    if (dom.tableBody) dom.tableBody.addEventListener('click', onTableClick);
  }

  async function loadVinculoOptions(){
    try {
      const [socios, empleados] = await Promise.all([
        requestJson(buildUrl('/api/v1/socios/activos')).catch(()=>[]),
        requestJson(buildUrl('/api/v1/empleados/activos')).catch(()=>[])
      ]);
      state.socios = Array.isArray(socios)? socios : [];
      state.empleados = Array.isArray(empleados)? empleados : [];
      fillSelect(dom.socioSelect, state.socios.map(s=>({value: s.id, label: `${s.nombre||''} ${s.apellido||''} - ${s.numeroDocumento||''}`.trim()})));
      fillSelect(dom.empleadoSelect, state.empleados.map(e=>({value: e.id, label: `${e.nombre||''} ${e.apellido||''} - ${e.numeroDocumento||''}`.trim()})));
    } catch(e){
      console.warn('No se pudieron cargar opciones de vínculo', e);
    }
  }

  function fillSelect(select, options){
    if(!select) return;
    const frag = document.createDocumentFragment();
    const first = document.createElement('option');
    first.value = '';
    first.textContent = 'Seleccione...';
    frag.appendChild(first);
    for(const opt of (options||[])){
      const o = document.createElement('option');
      o.value = opt.value;
      o.textContent = opt.label;
      frag.appendChild(o);
    }
    select.innerHTML='';
    select.appendChild(frag);
  }

  function onTipoVinculoChange(){
    const v = dom.tipoVinculo.value;
    toggle(dom.vinculoSocioWrap, v === 'SOCIO');
    toggle(dom.vinculoEmpleadoWrap, v === 'EMPLEADO');
  }
  function toggle(el, show){ if(!el) return; el.classList[show?'remove':'add']('d-none'); }

  async function listar(){
    try {
  state.registros = await requestJson(buildUrl('/api/usuarios')) || [];
      render();
    } catch(e){
      console.error('Error listando usuarios', e);
    }
  }

  function render(){
    const q = (dom.searchInput?.value||'').toLowerCase().trim();
    const rows = (state.registros||[]).filter(u => !q || (u.nombreUsuario||'').toLowerCase().includes(q));
    const tbody = dom.tableBody;
    if(!tbody) return;
    tbody.innerHTML = rows.map(u => `
      <tr data-id="${u.id}">
        <td>${escapeHtml(u.nombreUsuario||'')}</td>
        <td>${escapeHtml(formatLabel(u.rol)||'')}</td>
        <td>${u.eliminado ? 'Inactivo' : 'Activo'}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary js-edit" data-id="${u.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger js-del" data-id="${u.id}">Borrar</button>
        </td>
      </tr>
    `).join('');
  }

  function onTableClick(e){
    const btn = e.target.closest('button');
    if(!btn) return;
    const id = btn.getAttribute('data-id');
    if(btn.classList.contains('js-edit')){
      const item = state.registros.find(x=>x.id===id);
      if(item) fillFormForEdit(item);
    } else if(btn.classList.contains('js-del')){
      if(confirm('¿Eliminar este usuario?')){
        eliminar(id);
      }
    }
  }

  function fillFormForEdit(item){
    if(!dom.form) return;
    state.editingId = item.id;
    dom.form.nombreUsuario.value = item.nombreUsuario || '';
    dom.form.clave.value = '';
    dom.form.rol.value = item.rol || 'SOCIO';
    // No inferimos vínculo actual (se maneja desde persona)
    if(dom.tipoVinculo){ dom.tipoVinculo.value = 'NINGUNO'; onTipoVinculoChange(); }
  }

  function resetForm(){
    state.editingId = null;
    if(!dom.form) return;
    dom.form.reset();
    if(dom.tipoVinculo){ dom.tipoVinculo.value = 'NINGUNO'; onTipoVinculoChange(); }
  }

  async function onFormSubmit(e){
    e.preventDefault();
    const f = dom.form;
    const payload = {
      nombreUsuario: f.nombreUsuario.value.trim(),
      clave: f.clave.value.trim(),
      rol: f.rol.value
    };
    try {
      let createdOrId = state.editingId;
      if(state.editingId){
        await sendJson(buildUrl(`/api/usuarios/${state.editingId}`), 'PUT', payload);
      } else {
        const created = await sendJson(buildUrl('/api/usuarios'), 'POST', payload);
        createdOrId = created?.id;
      }
      // Vínculo opcional
      if(createdOrId && dom.tipoVinculo){
        const tipo = f.tipoVinculo.value;
        if(tipo === 'SOCIO' && f.socioId.value){
          await sendJson(buildUrl(`/api/v1/socios/${f.socioId.value}/usuario`), 'POST', { usuarioId: createdOrId });
        } else if(tipo === 'EMPLEADO' && f.empleadoId.value){
          await sendJson(buildUrl(`/api/v1/empleados/${f.empleadoId.value}/usuario`), 'POST', { usuarioId: createdOrId });
        }
      }
      await listar();
      resetForm();
    } catch(err){
      console.error('Error al guardar usuario', err);
      alert('No se pudo guardar el usuario');
    }
  }

  async function eliminar(id){
    try {
  await requestJson(buildUrl(`/api/usuarios/${id}`), { method: 'DELETE' });
      await listar();
    } catch(e){
      console.error('Error al eliminar', e);
      alert('No se pudo eliminar');
    }
  }

  function escapeHtml(s){
    return (s||'').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[c]));
  }
})();
