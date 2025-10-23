(function(){
  // Lightweight admin script for Empresas CRUD using /api/empresas
  const $ = window.jQuery;

  function normaliseContextPath(path){
    if(!path) return '';
    if(path === '/') return '';
    return path.endsWith('/') ? path.slice(0,-1) : path;
  }
  const rawContextPath = document.body ? (document.body.dataset.contextPath || '') : '';
  const contextPath = normaliseContextPath(rawContextPath);
  function buildUrl(path){
    if(!path) return contextPath || '';
    const clean = path.startsWith('/') ? path : '/' + path;
    return contextPath ? `${contextPath}${clean}` : clean;
  }

  async function requestJson(url, opts){
    const res = await fetch(url, opts);
    if(!res.ok){
      let msg = res.statusText;
      try{ const t = await res.text(); msg = t || msg; }catch{}
      throw new Error(msg || ('HTTP '+res.status));
    }
    const ct = res.headers.get('content-type')||'';
    if(ct.includes('application/json')) return res.json();
    return null;
  }
  async function sendJson(url, method, payload){
    return requestJson(url, { method, headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload||{}) });
  }

  const dom = {
    form: document.getElementById('empresa-form'),
    resetBtn: document.getElementById('empresa-reset'),
    search: document.getElementById('empresa-search'),
    refresh: document.getElementById('empresa-refresh'),
    tableBody: document.getElementById('empresa-table-body')
  };

  const state = { items: [], editingId: null };

  document.addEventListener('DOMContentLoaded', () => {
    attach();
    listar();
  });

  function attach(){
    dom.form?.addEventListener('submit', onSubmit);
    dom.resetBtn?.addEventListener('click', resetForm);
    dom.refresh?.addEventListener('click', render);
    dom.search?.addEventListener('input', render);
    dom.tableBody?.addEventListener('click', onTableClick);
  }

  async function listar(){
    try{
      state.items = await requestJson(buildUrl('/api/empresas')) || [];
      render();
    }catch(e){ console.error('Empresas listar error', e); }
  }

  function render(){
    const q = (dom.search?.value||'').toLowerCase().trim();
    const rows = (state.items||[]).filter(it => {
      const text = [it.nombre, it.telefono, it.correoElectronico].filter(Boolean).join(' ').toLowerCase();
      return !q || text.includes(q);
    });
    if(!dom.tableBody) return;
    dom.tableBody.innerHTML = rows.map(it => `
      <tr data-id="${escapeHtml(it.id||'')}">
        <td>${escapeHtml(it.nombre||'')}</td>
        <td>${escapeHtml(it.telefono||'')}</td>
        <td>${escapeHtml(it.correoElectronico||'')}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary js-edit" data-id="${it.id}">Editar</button>
          <button class="btn btn-sm btn-outline-danger js-del" data-id="${it.id}">Borrar</button>
        </td>
      </tr>`).join('');
  }

  function onTableClick(e){
    const btn = e.target.closest('button');
    if(!btn) return;
    const id = btn.getAttribute('data-id');
    if(btn.classList.contains('js-edit')){
      const item = state.items.find(x=>String(x.id)===String(id));
      if(item) fillFormForEdit(item);
    } else if(btn.classList.contains('js-del')){
      if(confirm('¿Eliminar empresa?')) eliminar(id);
    }
  }

  function fillFormForEdit(item){
    if(!dom.form) return;
    state.editingId = item.id;
    dom.form.nombre.value = item.nombre||'';
    dom.form.telefono.value = item.telefono||'';
    dom.form.correoElectronico.value = item.correoElectronico||'';
    // Solo nombre es editable por API PUT; deshabilitar teléfono/correo en edición
    dom.form.telefono.disabled = true;
    dom.form.correoElectronico.disabled = true;
  }

  function resetForm(){
    state.editingId = null;
    if(!dom.form) return;
    dom.form.reset();
    dom.form.telefono.disabled = false;
    dom.form.correoElectronico.disabled = false;
  }

  async function onSubmit(e){
    e.preventDefault();
    const f = dom.form;
    const nombre = f.nombre.value.trim();
    const telefono = f.telefono.value.trim();
    const correo = f.correoElectronico.value.trim();
    try{
      if(state.editingId){
        const params = new URLSearchParams();
        params.set('nombre', nombre);
        await requestJson(buildUrl(`/api/empresas/${state.editingId}?${params.toString()}`), { method: 'PUT' });
      } else {
        await sendJson(buildUrl('/api/empresas'), 'POST', { nombre, telefono, correoElectronico: correo });
      }
      await listar();
      resetForm();
    }catch(err){
      console.error('Guardar empresa error', err);
      alert('No se pudo guardar la empresa');
    }
  }

  async function eliminar(id){
    try{
      await requestJson(buildUrl(`/api/empresas/${id}`), { method: 'DELETE' });
      await listar();
    }catch(err){
      console.error('Eliminar empresa error', err);
      alert('No se pudo eliminar la empresa');
    }
  }

  function escapeHtml(s){
    return (s||'').toString().replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[c]));
  }
})();
