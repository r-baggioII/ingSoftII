(function(){
  const $ = window.jQuery;

  function normaliseContextPath(path){
    if(!path) return '';
    if(path === '/') return '';
    return path.endsWith('/') ? path.slice(0,-1) : path;
  }
  const rawContextPath = document.body ? (document.body.dataset.contextPath || '') : '';
  const contextPath = normaliseContextPath(rawContextPath);
  function buildUrl(path){ if(!path) return contextPath||''; const clean = path.startsWith('/')?path:'/'+path; return contextPath?`${contextPath}${clean}`:clean; }

  async function requestJson(url, opts){
    const res = await fetch(url, opts);
    if(!res.ok){ let msg = res.statusText; try{ const t=await res.text(); msg=t||msg; }catch{}; throw new Error(msg||('HTTP '+res.status)); }
    const ct = res.headers.get('content-type')||''; if(ct.includes('application/json')) return res.json(); return null;
  }
  async function sendJson(url, method, payload){
    return requestJson(url, { method, headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload||{}) });
  }

  const dom = {
    form: document.getElementById('sucursal-form'),
    resetBtn: document.getElementById('sucursal-reset'),
    tableBody: document.getElementById('sucursal-table-body'),
    search: document.getElementById('sucursal-search'),
    refresh: document.getElementById('sucursal-refresh'),
    empresa: document.querySelector("select[name='empresaId']"),
    dirExistente: document.querySelector("select[name='idDireccion']"),
    pais: document.querySelector("select[name='idPais']"),
    prov: document.querySelector("select[name='idProvincia']"),
    depto: document.querySelector("select[name='idDepartamento']"),
    loc: document.querySelector("select[name='idLocalidad']"),
  };

  const state = { items: [], editingId: null };

  document.addEventListener('DOMContentLoaded', () => {
    attach();
    cargarCombos();
    listar();
  });

  function attach(){
    dom.form?.addEventListener('submit', onSubmit);
    dom.resetBtn?.addEventListener('click', resetForm);
    dom.tableBody?.addEventListener('click', onTableClick);
    dom.search?.addEventListener('input', render);
    dom.refresh?.addEventListener('click', render);
    dom.pais?.addEventListener('change', onPaisChange);
    dom.prov?.addEventListener('change', onProvChange);
    dom.depto?.addEventListener('change', onDeptoChange);
  }

  async function cargarCombos(){
    await Promise.all([cargarEmpresas(), cargarDirecciones(), cargarPaises()]);
  }

  async function cargarEmpresas(){
    if(!dom.empresa) return;
    const data = await requestJson(buildUrl('/api/empresas'))||[];
    fillSelect(dom.empresa, data.filter(x=>!x.eliminado).map(x=>({value:x.id,label:x.nombre})), 'Seleccione una empresa...');
  }

  async function cargarDirecciones(){
    if(!dom.dirExistente) return;
    const data = await requestJson(buildUrl('/api/direcciones'))||[];
    const opts = data.filter(x=>!x.eliminado).map(d=>({value:d.id,label:formatAddress(d)}));
    fillSelect(dom.dirExistente, opts, 'Seleccionar existente...');
  }

  async function cargarPaises(){
    if(!dom.pais) return;
    const data = await requestJson(buildUrl('/api/v1/paises/activos'))||[];
    fillSelect(dom.pais, data.map(x=>({value:x.id,label:x.nombre})), 'País...');
    clearSelect(dom.prov, 'Provincia...');
    clearSelect(dom.depto, 'Departamento...');
    clearSelect(dom.loc, 'Localidad...');
  }

  async function onPaisChange(){
    const idPais = dom.pais?.value;
    clearSelect(dom.prov, 'Provincia...');
    clearSelect(dom.depto, 'Departamento...');
    clearSelect(dom.loc, 'Localidad...');
    if(!idPais) return;
    const data = await requestJson(buildUrl(`/api/v1/provincias?paisId=${encodeURIComponent(idPais)}`))||[];
    fillSelect(dom.prov, data.map(x=>({value:x.id,label:x.nombre})), 'Provincia...');
  }

  async function onProvChange(){
    const idProv = dom.prov?.value;
    clearSelect(dom.depto, 'Departamento...');
    clearSelect(dom.loc, 'Localidad...');
    if(!idProv) return;
    const data = await requestJson(buildUrl(`/api/v1/departamentos?provinciaId=${encodeURIComponent(idProv)}`))||[];
    fillSelect(dom.depto, data.map(x=>({value:x.id,label:x.nombre})), 'Departamento...');
  }

  async function onDeptoChange(){
    const idDep = dom.depto?.value;
    clearSelect(dom.loc, 'Localidad...');
    if(!idDep) return;
    const data = await requestJson(buildUrl(`/api/v1/localidades?departamentoId=${encodeURIComponent(idDep)}`))||[];
    fillSelect(dom.loc, data.map(x=>({value:x.id,label:`${x.nombre}${x.codigoPostal?` (${x.codigoPostal})`:''}`})), 'Localidad...');
  }

  function fillSelect(sel, options, placeholder){
    if(!sel) return;
    sel.innerHTML = '';
    const ph = document.createElement('option'); ph.value=''; ph.textContent = placeholder||'Seleccione...'; sel.appendChild(ph);
    (options||[]).forEach(o=>{ const opt=document.createElement('option'); opt.value=o.value; opt.textContent=o.label; sel.appendChild(opt); });
  }
  function clearSelect(sel, placeholder){ if(!sel) return; sel.innerHTML=''; const ph=document.createElement('option'); ph.value=''; ph.textContent=placeholder||'Seleccione...'; sel.appendChild(ph); }

  async function listar(){
    try{
      state.items = await requestJson(buildUrl('/api/v1/sucursales'))||[];
      render();
    }catch(e){ console.error('Listar sucursales error', e); }
  }

  function render(){
    const q = (dom.search?.value||'').toLowerCase().trim();
    const rows = (state.items||[]).filter(s => {
      const txt = [s.nombre, s.empresa?.nombre, formatAddress(s.direccion)].filter(Boolean).join(' ').toLowerCase();
      return !q || txt.includes(q);
    });
    if(!dom.tableBody) return;
    dom.tableBody.innerHTML = rows.map(s => `
      <tr data-id="${escapeHtml(s.id||'')}">
        <td>${escapeHtml(s.nombre||'')}</td>
        <td>${escapeHtml(s.empresa?.nombre||'')}</td>
        <td>${escapeHtml(formatAddress(s.direccion)||'')}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary js-edit" data-id="${s.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger js-del" data-id="${s.id}">Borrar</button>
        </td>
      </tr>`).join('');
  }

  function onTableClick(e){
    const btn = e.target.closest('button'); if(!btn) return; const id = btn.getAttribute('data-id');
    if(btn.classList.contains('js-edit')){
      const item = state.items.find(x=>String(x.id)===String(id)); if(item) fillFormForEdit(item);
    } else if(btn.classList.contains('js-del')){
      if(confirm('¿Eliminar sucursal?')) eliminar(id);
    }
  }

  function fillFormForEdit(s){
    const f = dom.form; if(!f) return; state.editingId = s.id;
    f.nombre.value = s.nombre||'';
    if(dom.empresa) dom.empresa.value = s.empresa?.id || '';
    // intentar seleccionar dirección existente
    if(dom.dirExistente) dom.dirExistente.value = s.direccion?.id || '';
    // geografía
    if(s.direccion?.localidad){
      const paisId = s.direccion.localidad?.departamento?.provincia?.pais?.id || '';
      const provId = s.direccion.localidad?.departamento?.provincia?.id || '';
      const depId  = s.direccion.localidad?.departamento?.id || '';
      const locId  = s.direccion.localidad?.id || '';
      if(dom.pais) dom.pais.value = paisId; onPaisChange().then(()=>{
        if(dom.prov){ dom.prov.value = provId; onProvChange().then(()=>{
          if(dom.depto){ dom.depto.value = depId; onDeptoChange().then(()=>{
            if(dom.loc) dom.loc.value = locId;
          }); }
        }); }
      });
    }
    // campos de dirección
    f.calle.value = s.direccion?.calle || '';
    f.numero.value = s.direccion?.numero || '';
    f.codigoPostal.value = s.direccion?.codigoPostal || '';
    f.barrio.value = s.direccion?.barrio || '';
    f.referencia.value = s.direccion?.referencia || '';
  }

  function resetForm(){ state.editingId = null; dom.form?.reset(); cargarCombos(); }

  async function onSubmit(e){
    e.preventDefault(); const f = dom.form; const isEdit = !!state.editingId;
    const nombre = f.nombre.value.trim(); const idEmpresa = f.empresaId.value;
    const idDireccion = f.idDireccion.value;
    let payload;
    if(idDireccion){
      payload = { nombre, idEmpresa, direccion: { id: idDireccion } };
    } else {
      // construir dirección completa con jerarquía geográfica para pasar validación backend
      const idPais = f.idPais.value || '';
      const idProvincia = f.idProvincia.value || '';
      const idDepartamento = f.idDepartamento.value || '';
      const idLocalidad = f.idLocalidad.value || '';

      // localidad con anidado departamento -> provincia -> pais
      let localidad = null;
      if (idLocalidad) {
        localidad = { id: idLocalidad };
        // El servicio valida que existan los objetos anidados (no solo el id de localidad)
        localidad.departamento = idDepartamento ? { id: idDepartamento } : null;
        if (localidad.departamento) {
          localidad.departamento.provincia = idProvincia ? { id: idProvincia } : null;
          if (localidad.departamento.provincia) {
            localidad.departamento.provincia.pais = idPais ? { id: idPais } : null;
          }
        }
      }

      const dir = {
        calle: f.calle.value.trim(),
        numero: f.numero.value.trim(),
        localidad,
        codigoPostal: f.codigoPostal.value.trim() || null,
        barrio: f.barrio.value.trim() || null,
        referencia: f.referencia.value.trim() || null
      };
      payload = { nombre, idEmpresa, direccion: dir };
    }
    try{
      if(isEdit){
        await sendJson(buildUrl(`/api/v1/sucursales/${state.editingId}`), 'PUT', payload);
      } else {
        await sendJson(buildUrl('/api/v1/sucursales'), 'POST', payload);
      }
      await listar(); resetForm();
    }catch(err){ console.error('Guardar sucursal error', err); alert('No se pudo guardar la sucursal'); }
  }

  async function eliminar(id){
    try{ await requestJson(buildUrl(`/api/v1/sucursales/${id}`), { method:'DELETE' }); await listar(); }
    catch(err){ console.error('Eliminar sucursal error', err); alert('No se pudo eliminar la sucursal'); }
  }

  function formatAddress(d){
    if(!d) return '';
    const parts = [];
    if(d.calle) parts.push(d.calle);
    if(d.numero) parts.push(d.numero);
    const loc = d.localidad;
    if(loc?.nombre) parts.push(loc.nombre);
    const dep = loc?.departamento; if(dep?.nombre) parts.push(dep.nombre);
    const prov = dep?.provincia; if(prov?.nombre) parts.push(prov.nombre);
    const pais = prov?.pais; if(pais?.nombre) parts.push(pais.nombre);
    if(d.codigoPostal) parts.push(`CP ${d.codigoPostal}`);
    return parts.filter(Boolean).join(', ');
  }
  function escapeHtml(s){ return (s||'').toString().replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[c])); }
})();
