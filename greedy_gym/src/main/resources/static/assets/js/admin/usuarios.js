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
    selectedUsuarioId: null
  };

  // --- DOM ---
  const dom = {
    form: document.getElementById('usuario-form'),
    reset: document.getElementById('usuario-reset'),
    searchInput: document.getElementById('usuario-search'),
    refreshBtn: document.getElementById('usuario-refresh'),
    tableBody: document.getElementById('usuario-table-body'),
    rol: document.querySelector("select[name='rol']"),
    datosSocioWrap: document.getElementById('datos-socio'),
    datosEmpleadoWrap: document.getElementById('datos-empleado'),
    socioNuevo: {
      nombre: document.querySelector("input[name='socioNombre']"),
      apellido: document.querySelector("input[name='socioApellido']"),
      fechaNacimiento: document.querySelector("input[name='socioFechaNacimiento']"),
      numeroSocio: document.querySelector("input[name='socioNumero']"),
      tipoDocumento: document.querySelector("select[name='socioTipoDocumento']"),
      numeroDocumento: document.querySelector("input[name='socioNumeroDocumento']"),
      telefono: document.querySelector("input[name='socioTelefono']"),
      correo: document.querySelector("input[name='socioCorreo']")
    },
    empleadoNuevo: {
      nombre: document.querySelector("input[name='empleadoNombre']"),
      apellido: document.querySelector("input[name='empleadoApellido']"),
      fechaNacimiento: document.querySelector("input[name='empleadoFechaNacimiento']"),
      tipoDocumento: document.querySelector("select[name='empleadoTipoDocumento']"),
      numeroDocumento: document.querySelector("input[name='empleadoNumeroDocumento']"),
      telefono: document.querySelector("input[name='empleadoTelefono']"),
      correo: document.querySelector("input[name='empleadoCorreo']"),
      tipoEmpleado: document.querySelector("select[name='empleadoTipo']")
    }
  };

  // --- init ---
  document.addEventListener('DOMContentLoaded', () => {
    attachEvents();
    onRolChange();
    listar();
  });

  function attachEvents(){
    if (dom.rol) {
      dom.rol.addEventListener('change', onRolChange);
    }
    if (dom.form) dom.form.addEventListener('submit', onFormSubmit);
    if (dom.reset) dom.reset.addEventListener('click', resetForm);
    if (dom.refreshBtn) dom.refreshBtn.addEventListener('click', render);
    if (dom.searchInput) dom.searchInput.addEventListener('input', render);
    if (dom.tableBody) dom.tableBody.addEventListener('click', onTableClick);
  }

  function onRolChange(){
    const rol = dom.rol ? dom.rol.value : 'NINGUNO';
    const esSocio = rol === 'SOCIO';
    const esEmpleado = rol === 'ADMINISTRATIVO' || rol === 'PROFESOR';
    toggle(dom.datosSocioWrap, esSocio);
    toggle(dom.datosEmpleadoWrap, esEmpleado);
    if (esEmpleado && dom.empleadoNuevo?.tipoEmpleado) {
      if (!dom.empleadoNuevo.tipoEmpleado.value) {
        dom.empleadoNuevo.tipoEmpleado.value = rol === 'ADMINISTRATIVO' ? 'ADMINISTRATIVO' : 'ENTRENADOR';
      }
    }
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
    onRolChange();
    clearSocioNuevo();
    clearEmpleadoNuevo();
  }

  function resetForm(){
    state.editingId = null;
    if(!dom.form) return;
    dom.form.reset();
    onRolChange();
    clearSocioNuevo();
    clearEmpleadoNuevo();
  }

  async function onFormSubmit(e){
    e.preventDefault();
    const f = dom.form;
    const isEditing = Boolean(state.editingId);
    const payload = {
      nombreUsuario: f.nombreUsuario.value.trim(),
      clave: f.clave.value.trim(),
      rol: f.rol.value,
      socio: null,
      empleado: null
    };
    try {
      if(payload.rol === 'SOCIO' && !isEditing){
        const datosSocio = collectSocioNuevo();
        if(!datosSocio){
          alert('Completa todos los datos obligatorios del socio.');
          return;
        }
        payload.socio = datosSocio;
      } else if ((payload.rol === 'ADMINISTRATIVO' || payload.rol === 'PROFESOR') && !isEditing) {
        const datosEmpleado = collectEmpleadoNuevo();
        if(!datosEmpleado){
          alert('Completa todos los datos obligatorios del empleado.');
          return;
        }
        payload.empleado = datosEmpleado;
      }

      let createdOrId = state.editingId;
      if(state.editingId){
        await sendJson(buildUrl(`/api/usuarios/${state.editingId}`), 'PUT', payload);
      } else {
        const created = await sendJson(buildUrl('/api/usuarios'), 'POST', payload);
        createdOrId = created?.id;
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

  function collectSocioNuevo(){
    const campos = dom.socioNuevo;
    if(!campos) return null;
    const nombre = campos.nombre?.value.trim();
    const apellido = campos.apellido?.value.trim();
    const fechaNacimiento = campos.fechaNacimiento?.value;
    const tipoDocumento = campos.tipoDocumento?.value;
    const numeroDocumento = campos.numeroDocumento?.value.trim();
    const telefono = campos.telefono?.value.trim();
    const correo = campos.correo?.value.trim();
    if(!nombre || !apellido || !fechaNacimiento || !tipoDocumento || !numeroDocumento || !telefono || !correo){
      return null;
    }
    const numeroSocioVal = campos.numeroSocio?.value;
    return {
      nombre,
      apellido,
      fechaNacimiento,
      tipoDocumento,
      numeroDocumento,
      telefono,
      correoElectronico: correo,
      numeroSocio: numeroSocioVal ? Number(numeroSocioVal) : null
    };
  }

  function clearSocioNuevo(){
    const campos = dom.socioNuevo;
    if(!campos) return;
    Object.values(campos).forEach(input => {
      if(!input) return;
      input.value = '';
    });
    if(campos.tipoDocumento) campos.tipoDocumento.value = '';
  }

  function collectEmpleadoNuevo(){
    const campos = dom.empleadoNuevo;
    if(!campos) return null;
    const nombre = campos.nombre?.value.trim();
    const apellido = campos.apellido?.value.trim();
    const fechaNacimiento = campos.fechaNacimiento?.value;
    const tipoDocumento = campos.tipoDocumento?.value;
    const numeroDocumento = campos.numeroDocumento?.value.trim();
    const telefono = campos.telefono?.value.trim();
    const correo = campos.correo?.value.trim();
    let tipoEmpleado = campos.tipoEmpleado?.value;
    if(!nombre || !apellido || !fechaNacimiento || !tipoDocumento || !numeroDocumento || !telefono || !correo){
      return null;
    }
    if(!tipoEmpleado){
      const rol = dom.rol ? dom.rol.value : null;
      if(rol === 'ADMINISTRATIVO') {
        tipoEmpleado = 'ADMINISTRATIVO';
      } else if (rol === 'PROFESOR') {
        tipoEmpleado = 'ENTRENADOR';
      }
    }
    if(!tipoEmpleado){
      return null;
    }
    return {
      nombre,
      apellido,
      fechaNacimiento,
      tipoDocumento,
      numeroDocumento,
      telefono,
      correoElectronico: correo,
      tipoEmpleado
    };
  }

  function clearEmpleadoNuevo(){
    const campos = dom.empleadoNuevo;
    if(!campos) return;
    Object.values(campos).forEach(input => {
      if(!input) return;
      input.value = '';
    });
    if(campos.tipoDocumento) campos.tipoDocumento.value = '';
    if(campos.tipoEmpleado) campos.tipoEmpleado.value = '';
  }
})();
