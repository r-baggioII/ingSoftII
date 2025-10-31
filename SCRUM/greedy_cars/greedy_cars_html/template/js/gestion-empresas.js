// Mock front-end logic for gestión de empresas
(function(){
  // In-memory stores
  const direcciones = [
    { id: 'dir-1', pais: 'Argentina', provincia: 'Buenos Aires', departamento: 'La Plata', localidad: 'La Plata', calle: 'Calle 1', numero: '123', cp: '1900', referencia: 'Frente al parque' },
    { id: 'dir-2', pais: 'Argentina', provincia: 'Córdoba', departamento: 'Capital', localidad: 'Córdoba', calle: 'Otra', numero: '45', cp: '5000', referencia: '' }
  ];

  const empresas = [
    { id: 'emp-1', nombre: 'AutoRent SL', telefono: '+54 9 11 1234', correoElectronico: 'contacto@autorent.com', direccionId: 'dir-1', eliminado:false },
    { id: 'emp-2', nombre: 'FastCars', telefono: '+54 9 351 9876', correoElectronico: 'info@fastcars.ar', direccionId: 'dir-2', eliminado:false }
  ];

  // Utilities
  function uid(prefix='id'){
    return prefix + '-' + Math.random().toString(36).slice(2,9);
  }

  // Business methods (as requested)
  function validar(nombre){
    if(!nombre || String(nombre).trim().length < 2) throw new Error('Nombre inválido (mínimo 2 caracteres)');
  }

  function crearEmpresa(nombre, telefono, correoElectronico, direccionId){
    validar(nombre);
    const e = { id: uid('emp'), nombre: String(nombre).trim(), telefono: String(telefono||''), correoElectronico: String(correoElectronico||''), direccionId: direccionId||null, eliminado:false };
    empresas.push(e);
    renderEmpresaRows();
    return e;
  }

  function buscarEmpresa(id){
    return empresas.find(x=>x.id===id) || null;
  }

  function buscarEmpresaPorNombre(nombre){
    if(!nombre) return null;
    const lower = String(nombre).toLowerCase();
    return empresas.find(x=>x.nombre.toLowerCase().includes(lower)) || null;
  }

  function modificarEmpresa(id, nombre, telefono, correoElectronico, direccionId){
    const e = buscarEmpresa(id);
    if(!e) throw new Error('Empresa no encontrada');
    validar(nombre);
    e.nombre = String(nombre).trim();
    e.telefono = String(telefono||'');
    e.correoElectronico = String(correoElectronico||'');
    e.direccionId = direccionId||null;
    renderEmpresaRows();
    return e;
  }

  function eliminarEmpresa(id){
    const e = buscarEmpresa(id);
    if(!e) throw new Error('Empresa no encontrada');
    e.eliminado = true;
    renderEmpresaRows();
    return e;
  }

  function listarEmpresa(){
    return empresas.slice();
  }

  function listarEmpresaActiva(){
    return empresas.filter(x=>!x.eliminado);
  }

  // Direccion helpers
  function crearDireccion(payload){
    const d = Object.assign({ id: uid('dir') }, payload);
    direcciones.push(d);
    return d;
  }

  function listarDirecciones(){
    return direcciones.slice();
  }

  // UI wiring
  function $(id){ return document.getElementById(id); }

  function renderDireccionesSelect(selectEl, selectedId){
    selectEl.innerHTML = '';
    const empty = document.createElement('option'); empty.value=''; empty.textContent='-- Seleccione una dirección --';
    selectEl.appendChild(empty);
    listarDirecciones().forEach(d=>{
      const opt = document.createElement('option');
      opt.value = d.id;
      opt.textContent = `${d.pais} / ${d.provincia} / ${d.localidad} - ${d.calle} ${d.numero}`;
      if(d.id===selectedId) opt.selected = true;
      selectEl.appendChild(opt);
    });
    // add create new option
    const createOpt = document.createElement('option'); createOpt.value='__CREATE_NEW__'; createOpt.textContent = '+ Crear nueva dirección';
    selectEl.appendChild(createOpt);
  }

  function renderEmpresaRows(){
    const tbody = document.getElementById('empresa-table-body');
    if(!tbody) return;
    tbody.innerHTML = '';
    listarEmpresa().forEach(e=>{
      const tr = document.createElement('tr');
      const d = direcciones.find(x=>x.id===e.direccionId);
      tr.innerHTML = `
        <td>${e.nombre}</td>
        <td>${e.telefono||''}</td>
        <td>${e.correoElectronico||''}</td>
        <td>${d? (d.calle+ ' ' + d.numero + ' - ' + d.localidad) : ''}</td>
        <td>${e.eliminado? 'Inactivo' : 'Activo'}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary me-2" data-action="edit" data-id="${e.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${e.id}">Eliminar</button>
        </td>
      `;
      tbody.appendChild(tr);
    });
  }

  // Contacto models and store
  const contactos = [];

  // Enums (client-side)
  const TIPO_CONTACTO_VALUES = ['PERSONAL','LABORAL','EMPRESA'];
  const TIPO_TELEFONO_VALUES = ['MOVIL','FIJO','OTRO'];

  function buscarContacto(id){
    return contactos.find(c=>c.id===id) || null;
  }

  function eliminarContacto(id){
    const c = buscarContacto(id);
    if(!c) throw new Error('Contacto no encontrado');
    c.eliminado = true;
    renderContactosList(c.empresaId || null);
    return c;
  }

  // Generic Contacto creation (non-email)
  function validarContacto(tipoContacto, observacion){
    if(!['PERSONAL','LABORAL','EMPRESA'].includes(tipoContacto)) throw new Error('Tipo de contacto inválido');
  }

  function crearContacto(tipoContacto, valor, observacion, empresaId){
    validarContacto(tipoContacto, observacion);
    const c = { id: uid('ct'), tipoContacto, observacion: observacion||'', eliminado:false, empresaId: empresaId||null, kind: 'GENERIC', value: valor||'' };
    contactos.push(c);
    renderContactosList(empresaId || null);
    return c;
  }

  // ContactoTelefonico methods
  function validarContactoTelefonico(telefono, tipoTelefono, tipoContacto, observacion){
    if(!telefono || String(telefono).trim().length < 4) throw new Error('Teléfono inválido');
    if(!TIPO_TELEFONO_VALUES.includes(tipoTelefono)) throw new Error('Tipo de teléfono inválido');
    if(!TIPO_CONTACTO_VALUES.includes(tipoContacto)) throw new Error('Tipo de contacto inválido');
  }

  function crearContactoTelefonico(telefono, tipoTelefono, tipoContacto, observacion, empresaId){
    validarContactoTelefonico(telefono, tipoTelefono, tipoContacto, observacion);
    const c = { id: uid('ct'), tipoContacto, observacion: observacion||'', eliminado:false, empresaId: empresaId||null, kind:'PHONE', telefono: String(telefono), tipoTelefono };
    contactos.push(c);
    renderContactosList(empresaId || null);
    return c;
  }

  function modificarContactoTelefonico(id, telefono, tipoTelefono, tipoContacto, observacion){
    const c = buscarContacto(id);
    if(!c) throw new Error('Contacto no encontrado');
    validarContactoTelefonico(telefono, tipoTelefono, tipoContacto, observacion);
    c.telefono = String(telefono); c.tipoTelefono = tipoTelefono; c.tipoContacto = tipoContacto; c.observacion = observacion||'';
    renderContactosList(c.empresaId || null);
    return c;
  }

  function listarContactoTelefonico(){
    return contactos.filter(c=>c.kind==='PHONE').slice();
  }

  function listarContactoTelefonicoActivo(){
    return contactos.filter(c=>c.kind==='PHONE' && !c.eliminado).slice();
  }

  // ContactoCorreoElectronico methods
  function validarContactoCorreoElectronico(email, tipoContacto, observacion){
    if(!email || !email.includes('@')) throw new Error('Email inválido');
    if(!['PERSONAL','LABORAL','EMPRESA'].includes(tipoContacto)) throw new Error('Tipo de contacto inválido');
  }

  function crearContactoCorreoElectronico(email, tipoContacto, observacion, empresaId){
    validarContactoCorreoElectronico(email, tipoContacto, observacion);
    const c = { id: uid('ct'), tipoContacto, observacion: observacion||'', eliminado:false, empresaId: empresaId||null, kind:'EMAIL', email };
    contactos.push(c);
    renderContactosList(empresaId || null);
    return c;
  }

  function modificarContactoCorreoElectronico(id, email, tipoContacto, observacion){
    const c = buscarContacto(id);
    if(!c) throw new Error('Contacto no encontrado');
    validarContactoCorreoElectronico(email, tipoContacto, observacion);
    c.email = email; c.tipoContacto = tipoContacto; c.observacion = observacion||'';
    renderContactosList(c.empresaId || null);
    return c;
  }

  function listarContactoCoreoElectronico(){
    return contactos.filter(c=>c.kind==='EMAIL').slice();
  }

  function listarContactoCoreoElectronicoActivo(){
    return contactos.filter(c=>c.kind==='EMAIL' && !c.eliminado).slice();
  }

  function renderContactosList(empresaId){
    const cont = document.getElementById('contactos-list');
    if(!cont) return;
    cont.innerHTML = '';
    const list = contactos.filter(c=>c.empresaId === (empresaId||null) && !c.eliminado);
    if(list.length===0){ cont.innerHTML = '<div class="text-muted">No hay contactos agregados.</div>'; return; }
    list.forEach(c=>{
      const card = document.createElement('div');
      card.className = 'd-flex align-items-center justify-content-between mb-2 p-2 border rounded';
      let leftHtml = '';
      if(c.kind === 'EMAIL'){
        leftHtml = `<strong>${c.email}</strong> <small class="text-muted">(${c.tipoContacto})</small><div>${c.observacion||''}</div>`;
      } else if(c.kind === 'PHONE'){
        leftHtml = `<strong>${c.telefono}</strong> <small class="text-muted">(${c.tipoContacto} / ${c.tipoTelefono})</small><div>${c.observacion||''}</div>`;
      } else {
        leftHtml = `<strong>${c.value}</strong> <small class="text-muted">(${c.tipoContacto})</small><div>${c.observacion||''}</div>`;
      }
      card.innerHTML = `<div>${leftHtml}</div><div><button class="btn btn-sm btn-outline-danger" data-action="del-contact" data-id="${c.id}">Eliminar</button></div>`;
      cont.appendChild(card);
    });
  }

  // DOM actions
  document.addEventListener('DOMContentLoaded', ()=>{
    const empresaForm = $('empresa-form');
    const empresaNombre = $('empresaNombre');
    const empresaTelefono = $('empresaTelefono');
    const empresaCorreo = $('empresaCorreo');
    const empresaDireccionSelect = $('empresaDireccionSelect');
    const nuevaDireccionForm = $('nueva-direccion-form');
    const empresaReset = $('empresa-reset');
    const empresaSearch = $('empresa-search');
    const empresaRefresh = $('empresa-refresh');

    const newContactoKind = $('newContactoKind');
    const newContactoValor = $('newContactoValor');
    const newContactoTipo = $('newContactoTipo');
    const newContactoTipoTelefono = $('newContactoTipoTelefono');
    const newContactoObservacion = $('newContactoObservacion');
    const btnAddContacto = $('btnAddContacto');

    // show/hide phone-type selector based on kind
    function updateContactoKindUI(){
      const colTel = document.getElementById('col-tipo-telefono');
      if(newContactoKind.value === 'PHONE') colTel.style.display = '';
      else colTel.style.display = 'none';
    }
    newContactoKind.addEventListener('change', updateContactoKindUI);
    // initial
    updateContactoKindUI();

    // address inputs
    const ndCalle = $('nuevaDireccionCalle');
    const ndNumero = $('nuevaDireccionNumeracion');
    const ndPais = $('nuevaDireccionPais');
    const ndProvincia = $('nuevaDireccionProvincia');
    const ndDepartamento = $('nuevaDireccionDepartamento');
    const ndLocalidad = $('nuevaDireccionLocalidad');
    const ndBarrio = $('nuevaDireccionBarrio');
    const ndManzana = $('nuevaDireccionManzana');
    const ndCasa = $('nuevaDireccionCasa');
    const ndReferencia = $('nuevaDireccionReferencia');

    // state for currently editing empresa (id or null)
    let editingEmpresaId = null;

  renderDireccionesSelect(empresaDireccionSelect);
  renderEmpresaRows();
  renderContactosList(null);

    // show/hide new address panel when user selects create option
    empresaDireccionSelect.addEventListener('change', ()=>{
      if(empresaDireccionSelect.value === '__CREATE_NEW__'){
        nuevaDireccionForm.classList.remove('d-none');
      } else {
        nuevaDireccionForm.classList.add('d-none');
      }
    });

    // add contacto (email or phone or generic) assigned to editing empresa if present, else empresaId=null until save
    btnAddContacto.addEventListener('click', ()=>{
      try{
        const valor = newContactoValor.value.trim();
        const tipo = newContactoTipo.value;
        const tipoTel = newContactoTipoTelefono.value;
        const obs = newContactoObservacion.value;
        const kind = newContactoKind.value;
        if(kind === 'EMAIL'){
          crearContactoCorreoElectronico(valor, tipo, obs, editingEmpresaId);
        } else if(kind === 'PHONE'){
          crearContactoTelefonico(valor, tipoTel, tipo, obs, editingEmpresaId);
        } else {
          crearContacto(tipo, valor, obs, editingEmpresaId);
        }
        // clear inputs
        newContactoValor.value = '';
        newContactoObservacion.value = '';
        renderContactosList(editingEmpresaId);
      }catch(err){ alert('Error: ' + err.message); }
    });

    empresaForm.addEventListener('submit', (ev)=>{
      ev.preventDefault();
      try{
        const nombre = empresaNombre.value;
        const tel = empresaTelefono.value;
        const correo = empresaCorreo.value;
        let dirId = empresaDireccionSelect.value;
        if(dirId === '__CREATE_NEW__'){
          // create new address from fields
          const payload = {
            pais: ndPais.value||'', provincia: ndProvincia.value||'', departamento: ndDepartamento.value||'', localidad: ndLocalidad.value||'', calle: ndCalle.value||'', numero: ndNumero.value||'', cp: '', referencia: ndReferencia.value||''
          };
          const d = crearDireccion(payload);
          dirId = d.id;
          renderDireccionesSelect(empresaDireccionSelect, dirId);
          nuevaDireccionForm.classList.add('d-none');
        }

        if(editingEmpresaId){
          modificarEmpresa(editingEmpresaId, nombre, tel, correo, dirId);
          // attach any contacts with empresaId null to this empresa
          contactos.filter(c=>c.empresaId===null && !c.eliminado).forEach(c=>{ c.empresaId = editingEmpresaId; });
        } else {
          const e = crearEmpresa(nombre, tel, correo, dirId);
          // attach contacts created with empresaId null to new empresa
          contactos.filter(c=>c.empresaId===null && !c.eliminado).forEach(c=>{ c.empresaId = e.id; });
        }
        // reset form
        editingEmpresaId = null;
        empresaForm.reset();
        renderDireccionesSelect(empresaDireccionSelect);
        renderEmpresaRows();
        renderContactosList(null);
      }catch(err){ alert('Error: ' + err.message); }
    });

    empresaReset.addEventListener('click', ()=>{
      editingEmpresaId = null;
      empresaForm.reset();
      renderContactosList(null);
      renderDireccionesSelect(empresaDireccionSelect);
      nuevaDireccionForm.classList.add('d-none');
    });

    // table actions (edit/delete)
    document.getElementById('empresa-table-body').addEventListener('click', (ev)=>{
      const btn = ev.target.closest('button');
      if(!btn) return;
      const action = btn.getAttribute('data-action');
      const id = btn.getAttribute('data-id');
      if(action==='edit'){
        const e = buscarEmpresa(id);
        if(!e) return alert('Empresa no encontrada');
        editingEmpresaId = e.id;
        empresaNombre.value = e.nombre;
        empresaTelefono.value = e.telefono || '';
        empresaCorreo.value = e.correoElectronico || '';
        renderDireccionesSelect(empresaDireccionSelect, e.direccionId);
        renderContactosList(e.id);
      } else if(action==='delete'){
        if(confirm('¿Eliminar empresa? (marcar como eliminado)')){
          eliminarEmpresa(id);
          renderEmpresaRows();
        }
      } else if(action==='del-contact'){
        const cid = btn.getAttribute('data-id');
        if(confirm('Eliminar contacto?')){
          eliminarContacto(cid);
        }
      }
    });

    empresaRefresh.addEventListener('click', ()=>{
      const q = empresaSearch.value.trim().toLowerCase();
      const tbody = document.getElementById('empresa-table-body');
      tbody.innerHTML = '';
      listarEmpresa().filter(e=>e.nombre.toLowerCase().includes(q)).forEach(e=>{
        const tr = document.createElement('tr');
        const d = direcciones.find(x=>x.id===e.direccionId);
        tr.innerHTML = `
          <td>${e.nombre}</td>
          <td>${e.telefono||''}</td>
          <td>${e.correoElectronico||''}</td>
          <td>${d? (d.calle+ ' ' + d.numero + ' - ' + d.localidad) : ''}</td>
          <td>${e.eliminado? 'Inactivo' : 'Activo'}</td>
          <td class="text-end">
            <button class="btn btn-sm btn-outline-primary me-2" data-action="edit" data-id="${e.id}">Editar</button>
            <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${e.id}">Eliminar</button>
          </td>
        `;
        tbody.appendChild(tr);
      });
    });

    // handle delete button inside contactos list (event delegation)
    document.getElementById('contactos-list').addEventListener('click', (ev)=>{
      const btn = ev.target.closest('button');
      if(!btn) return;
      const action = btn.getAttribute('data-action');
      const id = btn.getAttribute('data-id');
      if(action==='del-contact'){
        if(confirm('Eliminar contacto?')){ eliminarContacto(id); }
      }
    });
  });

  // Expose methods for console testing (optional)
  window.EmpresaAPI = {
    crearEmpresa, validar, buscarEmpresa, buscarEmpresaPorNombre, modificarEmpresa, eliminarEmpresa, listarEmpresa, listarEmpresaActiva, listarDirecciones, crearDireccion,
    // contactos API - base
    buscarContacto, eliminarContacto,
    // correo electronico
    crearContactoCorreoElectronico, validarContactoCorreoElectronico, modificarContactoCorreoElectronico, listarContactoCoreoElectronico, listarContactoCoreoElectronicoActivo,
    // telefonico
    crearContactoTelefonico, validarContactoTelefonico, modificarContactoTelefonico, listarContactoTelefonico, listarContactoTelefonicoActivo,
    // generic
    crearContacto
  };

})();
