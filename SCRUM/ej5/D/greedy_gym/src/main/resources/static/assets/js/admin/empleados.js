(function(){
  const $ = window.jQuery;
  function normaliseContextPath(path){ if(!path) return ''; if(path==='/' ) return ''; return path.endsWith('/')? path.slice(0,-1): path; }
  const rawContextPath = document.body ? (document.body.dataset.contextPath || '') : '';
  const contextPath = normaliseContextPath(rawContextPath);
  function buildUrl(path){ if(!path) return contextPath||''; const clean = path.startsWith('/')? path: '/'+path; return contextPath? `${contextPath}${clean}`: clean; }
  async function requestJson(url, opts){
    const res = await fetch(url, opts);
    if(!res.ok) throw new Error('HTTP '+res.status);
    const ct = res.headers.get('content-type')||'';
    if(ct.includes('application/json')) return res.json();
    return null;
  }
  async function sendJson(url, method, payload){
    return requestJson(url, { method, headers: {'Content-Type':'application/json'}, body: JSON.stringify(payload||{}) });
  }
  const dom = {
    form: document.getElementById('empleado-form'),
    reset: document.getElementById('empleado-reset'),
    search: document.getElementById('empleado-search'),
    refresh: document.getElementById('empleado-refresh'),
    tableBody: document.getElementById('empleado-table-body'),
    userForm: document.getElementById('empleado-usuario-form')
  };
  const state = { items: [], editingId: null };
  document.addEventListener('DOMContentLoaded', ()=>{ attach(); listar(); });
  function attach(){
    dom.form?.addEventListener('submit', onSave);
    dom.reset?.addEventListener('click', resetForm);
    dom.search?.addEventListener('input', render);
    dom.refresh?.addEventListener('click', render);
    dom.tableBody?.addEventListener('click', onTableClick);
    dom.userForm?.addEventListener('submit', onCreateAndLinkUser);
  }
  async function listar(){
    try{ state.items = await requestJson(buildUrl('/api/v1/empleados')) || []; render(); }
    catch(e){ console.error('List empleados', e); }
  }
  function render(){
    const q = (dom.search?.value||'').toLowerCase().trim();
    const rows = (state.items||[]).filter(e=>{
      const t = [e.nombre, e.apellido, e.numeroDocumento, e.correoElectronico].filter(Boolean).join(' ').toLowerCase();
      return !q || t.includes(q);
    });
    if(!dom.tableBody) return;
    dom.tableBody.innerHTML = rows.map(e=>`
      <tr data-id="${e.id}">
        <td>${esc(`${e.nombre||''} ${e.apellido||''}`.trim())}</td>
        <td>${esc(e.numeroDocumento||'')}</td>
        <td>${esc(e.correoElectronico||'')}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary js-edit" data-id="${e.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger js-del" data-id="${e.id}">Borrar</button>
        </td>
      </tr>`).join('');
  }
  function onTableClick(e){
    const btn = e.target.closest('button'); if(!btn) return; const id = btn.getAttribute('data-id');
    if(btn.classList.contains('js-edit')){
      const it = state.items.find(x=>x.id===id); if(it) fillForm(it);
    } else if(btn.classList.contains('js-del')){ if(confirm('¿Eliminar empleado?')) eliminar(id); }
  }
  function fillForm(x){
    state.editingId = x.id;
    const f = dom.form; if(!f) return;
    f.nombre.value = x.nombre||'';
    f.apellido.value = x.apellido||'';
    f.fechaNacimiento.value = x.fechaNacimiento||'';
    f.tipoDocumento.value = x.tipoDocumento||'DNI';
    f.numeroDocumento.value = x.numeroDocumento||'';
    f.telefono.value = x.telefono||'';
    f.correoElectronico.value = x.correoElectronico||'';
    f.tipoEmpleado.value = x.tipoEmpleado||'ADMINISTRATIVO';
  }
  function resetForm(){ state.editingId=null; dom.form?.reset(); }
  async function onSave(e){ e.preventDefault(); const f = dom.form; const payload = {
      nombre: f.nombre.value.trim(), apellido: f.apellido.value.trim(), fechaNacimiento: f.fechaNacimiento.value,
      tipoDocumento: f.tipoDocumento.value, numeroDocumento: f.numeroDocumento.value.trim(), telefono: f.telefono.value.trim(), correoElectronico: f.correoElectronico.value.trim(), tipoEmpleado: f.tipoEmpleado.value
    };
    try{
      if(state.editingId){ await sendJson(buildUrl(`/api/v1/empleados/${state.editingId}`), 'PUT', payload); }
      else { await sendJson(buildUrl('/api/v1/empleados'), 'POST', payload); }
      await listar(); resetForm();
    }catch(err){ console.error('Guardar empleado', err); alert('No se pudo guardar'); }
  }
  async function eliminar(id){ try{ await requestJson(buildUrl(`/api/v1/empleados/${id}`), {method:'DELETE'}); await listar(); } catch(e){ console.error(e); alert('No se pudo eliminar'); } }
  async function onCreateAndLinkUser(e){ e.preventDefault(); const f = dom.userForm; const empleadoSel = state.editingId || (state.items[0]?.id);
    if(!empleadoSel){ alert('Primero guardá el empleado.'); return; }
    const payload = { nombreUsuario: (f.nombreUsuario?.value||'').trim(), clave: (f.clave?.value||'').trim(), rol: (f.rol?.value||'PROFESOR') };
    try{
      const user = await sendJson(buildUrl('/api/usuarios'), 'POST', payload);
      await sendJson(buildUrl(`/api/v1/empleados/${empleadoSel}/usuario`), 'POST', { usuarioId: user.id });
      alert('Usuario creado y vinculado');
    }catch(err){ console.error('Crear/vincular usuario', err); alert('No se pudo crear/vincular'); }
  }
  function esc(s){ return (s||'').replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[c])); }
})();
