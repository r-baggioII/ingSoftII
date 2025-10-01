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
  function formatDireccion(dir){
    if(!dir) return '';
    const parts = [];
    if(dir.calle) parts.push(dir.calle);
    if(dir.numero) parts.push(dir.numero);
    const loc = dir.localidad;
    if(loc?.nombre) parts.push(loc.nombre);
    const dep = loc?.departamento;
    if(dep?.nombre) parts.push(dep.nombre);
    const prov = dep?.provincia;
    if(prov?.nombre) parts.push(prov.nombre);
    const pais = prov?.pais;
    if(pais?.nombre) parts.push(pais.nombre);
    if(dir.codigoPostal) parts.push(`CP ${dir.codigoPostal}`);
    return parts.filter(Boolean).join(', ');
  }
  function fillSelect(select, options, placeholder){
    if(!select) return;
    select.innerHTML = '';
    const ph = document.createElement('option');
    ph.value = '';
    ph.textContent = placeholder || 'Seleccione...';
    select.appendChild(ph);
    (options||[]).forEach(opt => {
      const option = document.createElement('option');
      option.value = opt.value;
      option.textContent = opt.label;
      select.appendChild(option);
    });
  }
  function setSelectValue(select, value, label){
    if(!select) return;
    if(!value){ select.value = ''; return; }
    const strVal = String(value);
    let option = Array.from(select.options).find(opt => String(opt.value) === strVal);
    if(!option){
      option = document.createElement('option');
      option.value = strVal;
      option.textContent = label || strVal;
      select.appendChild(option);
    }
    select.value = strVal;
  }
  async function populatePaises(select, placeholder){
    if(!select) return;
    if(!state.paises || !state.paises.length){
      state.paises = await requestJson(buildUrl('/api/v1/paises/activos')) || [];
    }
    fillSelect(select, state.paises.map(p => ({ value: p.id, label: p.nombre })), placeholder || 'País...');
  }
  async function populateProvincias(select, paisId, placeholder){
    if(!select) return;
    if(!paisId){ fillSelect(select, [], placeholder || 'Provincia...'); return; }
    const data = await requestJson(buildUrl(`/api/v1/provincias?paisId=${encodeURIComponent(paisId)}`)) || [];
    fillSelect(select, data.map(p => ({ value: p.id, label: p.nombre })), placeholder || 'Provincia...');
  }
  async function populateDepartamentos(select, provinciaId, placeholder){
    if(!select) return;
    if(!provinciaId){ fillSelect(select, [], placeholder || 'Departamento...'); return; }
    const data = await requestJson(buildUrl(`/api/v1/departamentos?provinciaId=${encodeURIComponent(provinciaId)}`)) || [];
    fillSelect(select, data.map(d => ({ value: d.id, label: d.nombre })), placeholder || 'Departamento...');
  }
  async function populateLocalidades(select, departamentoId, placeholder){
    if(!select) return;
    if(!departamentoId){ fillSelect(select, [], placeholder || 'Localidad...'); return; }
    const data = await requestJson(buildUrl(`/api/v1/localidades?departamentoId=${encodeURIComponent(departamentoId)}`)) || [];
    fillSelect(select, data.map(l => ({ value: l.id, label: `${l.nombre}${l.codigoPostal ? ` (${l.codigoPostal})` : ''}` })), placeholder || 'Localidad...');
  }

  // --- state ---
  const state = {
    registros: [],
    editingId: null,
    selectedUsuarioId: null,
    direcciones: [],
    sucursales: [],
    paises: []
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
    nuevaDireccionForm: document.getElementById('nueva-direccion-form'),
    nuevaEmpleadoDireccionForm: document.getElementById('nueva-empleado-direccion-form'),
    socioNuevo: {
      nombre: document.querySelector("input[name='socioNombre']"),
      apellido: document.querySelector("input[name='socioApellido']"),
      fechaNacimiento: document.querySelector("input[name='socioFechaNacimiento']"),
      numeroSocio: document.querySelector("input[name='socioNumero']"),
      tipoDocumento: document.querySelector("select[name='socioTipoDocumento']"),
      numeroDocumento: document.querySelector("input[name='socioNumeroDocumento']"),
      telefono: document.querySelector("input[name='socioTelefono']"),
      correo: document.querySelector("input[name='socioCorreo']"),
      // FALTABA: referencia al select de dirección para socios
      direccionId: document.querySelector("select[name='socioDireccionId']"),
      sucursalId: document.querySelector("select[name='socioSucursalId']")
    },
    empleadoNuevo: {
      nombre: document.querySelector("input[name='empleadoNombre']"),
      apellido: document.querySelector("input[name='empleadoApellido']"),
      fechaNacimiento: document.querySelector("input[name='empleadoFechaNacimiento']"),
      tipoDocumento: document.querySelector("select[name='empleadoTipoDocumento']"),
      numeroDocumento: document.querySelector("input[name='empleadoNumeroDocumento']"),
      telefono: document.querySelector("input[name='empleadoTelefono']"),
      correo: document.querySelector("input[name='empleadoCorreo']"),
      tipoEmpleado: document.querySelector("select[name='empleadoTipo']"),
      direccionId: document.querySelector("select[name='empleadoDireccionId']"),
      sucursalId: document.querySelector("select[name='empleadoSucursalId']")
    },
  };

  function buildDireccionGroup(form, prefix){
    if(!form) return null;
    const pick = (suffix, tag = 'input') => form.querySelector(`${tag}[name='${prefix}${suffix}']`);
    return {
      wrap: form,
      calle: pick('Calle'),
      numeracion: pick('Numeracion'),
      barrio: pick('Barrio'),
      manzana: pick('Manzana'),
      casa: pick('Casa'),
      referencia: pick('Referencia', 'textarea'),
      pais: pick('Pais', 'select'),
      provincia: pick('Provincia', 'select'),
      departamento: pick('Departamento', 'select'),
      localidad: pick('Localidad', 'select')
    };
  }

  dom.nuevaDireccion = buildDireccionGroup(dom.nuevaDireccionForm, 'nuevaDireccion');
  dom.nuevaEmpleadoDireccion = buildDireccionGroup(dom.nuevaEmpleadoDireccionForm, 'nuevaEmpleadoDireccion');

  // --- init ---
  $(document).ready(function(){
    attachEvents();
    onRolChange();
    initDireccionCascades();
    loadDirecciones();
    loadSucursales();
    listar();
    handleDireccionSelection(dom.socioNuevo?.direccionId, dom.nuevaDireccionForm);
    handleDireccionSelection(dom.empleadoNuevo?.direccionId, dom.nuevaEmpleadoDireccionForm);
  });

  // --- load select options ---
  async function loadDirecciones() {
    try {
      state.direcciones = await requestJson(buildUrl('/api/direcciones')) || [];
      const selectsConCrear = [dom.socioNuevo?.direccionId, dom.empleadoNuevo?.direccionId].filter(Boolean);
      selectsConCrear.forEach(select => {
        if(!select) return;
        const prev = select.value;
        select.innerHTML = '<option value="">Seleccione una dirección...</option><option value="__CREATE_NEW__">+ Crear nueva dirección</option>';
        state.direcciones.forEach(dir => {
          const option = document.createElement('option');
          option.value = dir.id;
          option.textContent = formatDireccion(dir) || `${dir.calle || ''} ${dir.numero || ''}`;
          select.appendChild(option);
        });
        if(prev){ select.value = prev; }
        handleDireccionSelection(select, select === dom.socioNuevo?.direccionId ? dom.nuevaDireccionForm : dom.nuevaEmpleadoDireccionForm);
      });
    } catch (e) {
      console.error('Error loading direcciones:', e);
    }
  }

  async function loadSucursales() {
    try {
      state.sucursales = await requestJson(buildUrl('/api/v1/sucursales')) || [];
      const selects = [dom.socioNuevo?.sucursalId, dom.empleadoNuevo?.sucursalId].filter(Boolean);
      selects.forEach(select => {
        if(!select) return;
        const prev = select.value;
        select.innerHTML = '<option value="">Seleccione una sucursal...</option>';
        state.sucursales.forEach(suc => {
          const option = document.createElement('option');
          option.value = suc.id;
          option.textContent = suc.nombre || 'Sin nombre';
          select.appendChild(option);
        });
        if(prev) select.value = prev;
      });
    } catch (e) {
      console.error('Error loading sucursales:', e);
    }
  }

  function initDireccionCascades() {
    const cascadas = [
      { group: dom.nuevaDireccion, placeholders: { pais: 'País...', provincia: 'Provincia...', departamento: 'Departamento...', localidad: 'Localidad...' } },
      { group: dom.nuevaEmpleadoDireccion, placeholders: { pais: 'País...', provincia: 'Provincia...', departamento: 'Departamento...', localidad: 'Localidad...' } }
    ];

    cascadas.forEach(({ group, placeholders }) => initDireccionCascadeForGroup(group, placeholders));
  }

  function initDireccionCascadeForGroup(group, placeholders){
    if(!group) return;
    const { pais, provincia, departamento, localidad } = group;
    if(pais){
      populatePaises(pais, placeholders?.pais).catch(err => console.error('Error poblando países', err));
      pais.addEventListener('change', async () => {
        await populateProvincias(provincia, pais.value, placeholders?.provincia);
        await populateDepartamentos(departamento, null, placeholders?.departamento);
        await populateLocalidades(localidad, null, placeholders?.localidad);
      });
    } else {
      if(provincia) fillSelect(provincia, [], placeholders?.provincia);
      if(departamento) fillSelect(departamento, [], placeholders?.departamento);
      if(localidad) fillSelect(localidad, [], placeholders?.localidad);
    }
    if(provincia){
      provincia.addEventListener('change', async () => {
        await populateDepartamentos(departamento, provincia.value, placeholders?.departamento);
        await populateLocalidades(localidad, null, placeholders?.localidad);
      });
    }
    if(departamento){
      departamento.addEventListener('change', async () => {
        await populateLocalidades(localidad, departamento.value, placeholders?.localidad);
      });
    }
  }



  function attachEvents(){
    if (dom.rol) {
      dom.rol.addEventListener('change', onRolChange);
    }
    if (dom.form) dom.form.addEventListener('submit', onFormSubmit);
    if (dom.reset) dom.reset.addEventListener('click', resetForm);
    if (dom.refreshBtn) dom.refreshBtn.addEventListener('click', render);
    if (dom.searchInput) dom.searchInput.addEventListener('input', render);
    if (dom.tableBody) dom.tableBody.addEventListener('click', onTableClick);
    if (dom.socioNuevo?.direccionId) dom.socioNuevo.direccionId.addEventListener('change', () => handleDireccionSelection(dom.socioNuevo.direccionId, dom.nuevaDireccionForm));
    if (dom.empleadoNuevo?.direccionId) dom.empleadoNuevo.direccionId.addEventListener('change', () => handleDireccionSelection(dom.empleadoNuevo.direccionId, dom.nuevaEmpleadoDireccionForm));
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

  function handleDireccionSelection(select, form){
    if(!select || !form) return;
    const show = select.value === '__CREATE_NEW__';
    form.classList[show ? 'remove' : 'add']('d-none');
  }

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

  async function onTableClick(e){
    const btn = e.target.closest('button');
    if(!btn) return;
    const id = btn.getAttribute('data-id');
    if(btn.classList.contains('js-edit')){
      const item = state.registros.find(x => String(x.id) === String(id));
      if(item) await fillFormForEdit(item);
    } else if(btn.classList.contains('js-del')){
      if(confirm('¿Eliminar este usuario?')){
        eliminar(id);
      }
    }
  }

  async function fillFormForEdit(item){
    if(!dom.form) return;
    state.editingId = item.id;
    dom.form.nombreUsuario.value = item.nombreUsuario || '';
    dom.form.clave.value = item.clave || '';
    dom.form.rol.value = item.rol || 'SOCIO';
    onRolChange();
    
    // Fill persona data based on role - work with existing APIs
    await fillPersonaData(item);
  }

  async function fillPersonaData(user) {
    clearSocioNuevo();
    clearEmpleadoNuevo();

    if (user.rol === 'SOCIO') {
      // Find socio data
      try {
        const socios = await requestJson(buildUrl('/api/v1/socios'));
        const socio = socios.find(s => s.usuario && s.usuario.id === user.id);
        if (socio) {
          fillSocioData(socio);
        }
      } catch (e) {
        console.error('Error loading socio data:', e);
      }
    } else if (user.rol === 'ADMINISTRATIVO' || user.rol === 'PROFESOR') {
      // Find empleado data
      try {
        const empleados = await requestJson(buildUrl('/api/v1/empleados'));
        const empleado = empleados.find(e => e.usuario && e.usuario.id === user.id);
        if (empleado) {
          fillEmpleadoData(empleado);
        }
      } catch (e) {
        console.error('Error loading empleado data:', e);
      }
    }
  }

  function fillSocioData(socio) {
    const campos = dom.socioNuevo;
    if (!campos) return;
    
    campos.nombre.value = socio.nombre || '';
    campos.apellido.value = socio.apellido || '';
    campos.fechaNacimiento.value = socio.fechaNacimiento || '';
    campos.tipoDocumento.value = socio.tipoDocumento || '';
    campos.numeroDocumento.value = socio.numeroDocumento || '';
    campos.telefono.value = socio.telefono || '';
   campos.correo.value = socio.correoElectronico || '';
   campos.numeroSocio.value = socio.numeroSocio || '';
    setSelectValue(campos.direccionId, socio.direccion?.id, socio.direccion?.descripcion);
    setSelectValue(campos.sucursalId, socio.sucursal?.id, socio.sucursal?.nombre);
  }

  function fillEmpleadoData(empleado) {
    const campos = dom.empleadoNuevo;
    if (!campos) return;
    
    campos.nombre.value = empleado.nombre || '';
    campos.apellido.value = empleado.apellido || '';
    campos.fechaNacimiento.value = empleado.fechaNacimiento || '';
    campos.tipoDocumento.value = empleado.tipoDocumento || '';
    campos.numeroDocumento.value = empleado.numeroDocumento || '';
    campos.telefono.value = empleado.telefono || '';
    campos.correo.value = empleado.correoElectronico || '';
    campos.tipoEmpleado.value = empleado.tipoEmpleado || '';
    setSelectValue(campos.direccionId, empleado.direccion?.id, formatDireccion(empleado.direccion));
    handleDireccionSelection(dom.empleadoNuevo?.direccionId, dom.nuevaEmpleadoDireccionForm);
    setSelectValue(campos.sucursalId, empleado.sucursal?.id, empleado.sucursal?.nombre);
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
        const datosSocio = await collectSocioNuevo();
        if(!datosSocio){
          alert('Completa todos los datos obligatorios del socio.');
          return;
        }
        payload.socio = datosSocio;
      } else if ((payload.rol === 'ADMINISTRATIVO' || payload.rol === 'PROFESOR') && !isEditing) {
        const datosEmpleado = await collectEmpleadoNuevo();
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

  async function collectSocioNuevo(){
    const campos = dom.socioNuevo;
    if(!campos) return null;
    const nombre = campos.nombre?.value.trim();
    const apellido = campos.apellido?.value.trim();
    const fechaNacimiento = campos.fechaNacimiento?.value;
    const tipoDocumento = campos.tipoDocumento?.value;
    const numeroDocumento = campos.numeroDocumento?.value.trim();
    const telefono = campos.telefono?.value.trim();
    const correo = campos.correo?.value.trim();
    let direccionId = campos.direccionId?.value;
    let sucursalId = campos.sucursalId?.value;
    
    if(!nombre || !apellido || !fechaNacimiento || !tipoDocumento || !numeroDocumento || !telefono || !correo){
      return null;
    }
    
    // Permitir crear nueva dirección para socio igual que empleado
    if (direccionId === '__CREATE_NEW__') {
      const nuevaDireccion = await createNewDireccion(dom.nuevaDireccion);
      if(!nuevaDireccion) return null; // usuario canceló/errores
      direccionId = nuevaDireccion.id;
      setSelectValue(dom.socioNuevo?.direccionId, direccionId, formatDireccion(nuevaDireccion));
      handleDireccionSelection(dom.socioNuevo?.direccionId, dom.nuevaDireccionForm);
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
      numeroSocio: numeroSocioVal ? Number(numeroSocioVal) : null,
      direccionId: direccionId || null,
      sucursalId: sucursalId || null
    };
  }

  async function createNewDireccion(group) {
    if (!group) return null;

    const calle = group.calle?.value.trim();
    const numeracion = group.numeracion?.value.trim();
    const localidadId = group.localidad?.value;

    if (!calle || !numeracion || !localidadId) {
      alert('Completá calle, altura y localidad para crear la dirección');
      return null;
    }

    const payload = {
      calle,
      numeracion,
      barrio: group.barrio?.value.trim() || null,
      manzanaPiso: group.manzana?.value.trim() || null,
      casaDepartamento: group.casa?.value.trim() || null,
      referencia: group.referencia?.value.trim() || null,
      idLocalidad: localidadId
    };

    try {
      const direccion = await sendJson(buildUrl('/api/direcciones'), 'POST', payload);
      await loadDirecciones();
      clearDireccionGroup(group);
      return direccion;
    } catch (e) {
      console.error('Error creating direccion:', e);
      alert('Error al crear la dirección');
      return null;
    }
  }

  function clearDireccionGroup(group){
    if(!group) return;
    ['calle','numeracion','barrio','manzana','casa','referencia'].forEach(key => {
      const input = group[key];
      if(input) input.value = '';
    });
    if(group.localidad) group.localidad.value = '';
    if(group.departamento) group.departamento.value = '';
    if(group.provincia) group.provincia.value = '';
    if(group.pais){
      group.pais.value = '';
      group.pais.dispatchEvent(new Event('change'));
    }
    if(group.wrap) group.wrap.classList.add('d-none');
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

  async function collectEmpleadoNuevo(){
    const campos = dom.empleadoNuevo;
    if(!campos) return null;
    const nombre = campos.nombre?.value.trim();
    const apellido = campos.apellido?.value.trim();
    const fechaNacimiento = campos.fechaNacimiento?.value;
    const tipoDocumento = campos.tipoDocumento?.value;
    const numeroDocumento = campos.numeroDocumento?.value.trim();
    const telefono = campos.telefono?.value.trim();
    const correo = campos.correo?.value.trim();
    const direccionId = campos.direccionId?.value;
    const sucursalId = campos.sucursalId?.value;
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

    let finalDireccionId = direccionId;

    // Create new direccion if needed
    if (direccionId === '__CREATE_NEW__') {
      const nuevaDireccion = await createNewDireccion(dom.nuevaEmpleadoDireccion);
      if(!nuevaDireccion) return null;
      finalDireccionId = nuevaDireccion.id;
      setSelectValue(dom.empleadoNuevo?.direccionId, finalDireccionId, formatDireccion(nuevaDireccion));
      handleDireccionSelection(dom.empleadoNuevo?.direccionId, dom.nuevaEmpleadoDireccionForm);
    }

    return {
      nombre,
      apellido,
      fechaNacimiento,
      tipoDocumento,
      numeroDocumento,
      telefono,
      correoElectronico: correo,
      tipoEmpleado,
      direccionId: finalDireccionId || null,
      sucursalId: sucursalId || null
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
