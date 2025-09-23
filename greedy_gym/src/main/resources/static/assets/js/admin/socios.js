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
  function formatLabel(v){ if(!v) return ''; return v.toString().toLowerCase().replace(/_/g,' ').replace(/\b\w/g,c=>c.toUpperCase()); }
  const dom = {
    form: document.getElementById('socio-form'),
    reset: document.getElementById('socio-reset'),
    search: document.getElementById('socio-search'),
    refresh: document.getElementById('socio-refresh'),
    tableBody: document.getElementById('socio-table-body'),
    userForm: document.getElementById('socio-usuario-form')
  };
  const state = { items: [], editingId: null, selectedSocioId: null };
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
    try{ state.items = await requestJson(buildUrl('/api/v1/socios')) || []; render(); }
    catch(e){ console.error('List socios', e); }
  }
  function render(){
    const q = (dom.search?.value||'').toLowerCase().trim();
    const rows = (state.items||[]).filter(s=>{
      const t = [s.nombre, s.apellido, s.numeroDocumento, s.correoElectronico, s.numeroSocio?.toString()].filter(Boolean).join(' ').toLowerCase();
      return !q || t.includes(q);
    });
    if(!dom.tableBody) return;
    dom.tableBody.innerHTML = rows.map(s=>`
      <tr data-id="${s.id}">
        <td>${esc(`${s.nombre||''} ${s.apellido||''}`.trim())}</td>
        <td>${esc(s.numeroDocumento||'')}</td>
        <td>${esc(s.correoElectronico||'')}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary js-edit" data-id="${s.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger js-del" data-id="${s.id}">Borrar</button>
        </td>
      </tr>`).join('');
  }
  function onTableClick(e){
    const btn = e.target.closest('button'); if(!btn) return; const id = btn.getAttribute('data-id');
    if(btn.classList.contains('js-edit')){
      const it = state.items.find(x=>x.id===id); if(it) fillForm(it);
    } else if(btn.classList.contains('js-del')){ if(confirm('¿Eliminar socio?')) eliminar(id); }
  }
  function fillForm(s){
    state.editingId = s.id;
    const f = dom.form; if(!f) return;
    f.nombre.value = s.nombre||'';
    f.apellido.value = s.apellido||'';
    f.fechaNacimiento.value = s.fechaNacimiento||'';
    f.tipoDocumento.value = s.tipoDocumento||'DNI';
    f.numeroDocumento.value = s.numeroDocumento||'';
    f.telefono.value = s.telefono||'';
    if (f.correoElectronico) f.correoElectronico.value = s.correoElectronico||s.correo||'';
  }
  function resetForm(){ state.editingId=null; dom.form?.reset(); }
  async function onSave(e){ e.preventDefault(); const f = dom.form; const payload = {
      nombre: f.nombre.value.trim(), apellido: f.apellido.value.trim(), fechaNacimiento: f.fechaNacimiento.value,
      tipoDocumento: f.tipoDocumento.value, numeroDocumento: f.numeroDocumento.value.trim(), telefono: (f.telefono?.value||'').trim(), correoElectronico: (f.correoElectronico?.value||'').trim()
    };
    try{
      if(state.editingId){ await sendJson(buildUrl(`/api/v1/socios/${state.editingId}`), 'PUT', payload); }
      else { await sendJson(buildUrl('/api/v1/socios'), 'POST', payload); }
      await listar(); resetForm();
    }catch(err){ console.error('Guardar socio', err); alert('No se pudo guardar'); }
  }
  async function eliminar(id){ try{ await requestJson(buildUrl(`/api/v1/socios/${id}`), {method:'DELETE'}); await listar(); } catch(e){ console.error(e); alert('No se pudo eliminar'); } }
  async function onCreateAndLinkUser(e){ e.preventDefault(); const f = dom.userForm; const socioSel = state.editingId || (state.items[0]?.id);
    if(!socioSel){ alert('Primero guardá el socio.'); return; }
    const payload = { nombreUsuario: (f.nombreUsuario?.value||'').trim(), clave: (f.clave?.value||'').trim(), rol: (f.rol?.value||'SOCIO') };
    try{
      const user = await sendJson(buildUrl('/api/usuarios'), 'POST', payload);
      await sendJson(buildUrl(`/api/v1/socios/${socioSel}/usuario`), 'POST', { usuarioId: user.id });
      alert('Usuario creado y vinculado');
    }catch(err){ console.error('Crear/vincular usuario', err); alert('No se pudo crear/vincular'); }
  }
  function esc(s){ return (s||'').replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[c])); }
})();
