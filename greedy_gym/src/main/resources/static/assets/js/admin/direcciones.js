(function(){
  const $ = window.jQuery;
  function ctx(p){ if(!p||p==='/') return ''; return p.endsWith('/')?p.slice(0,-1):p; }
  const contextPath = ctx(document.body?.dataset?.contextPath||'');
  const url = p => (contextPath?contextPath:'') + (p.startsWith('/')?p:'/'+p);
  async function req(urlStr, opts){ const res = await fetch(urlStr, opts); if(!res.ok){ let m=res.statusText; try{ const t=await res.text(); m=t||m;}catch{}; throw new Error(m);} const ct=res.headers.get('content-type')||''; if(ct.includes('application/json')) return res.json(); return null; }
  async function send(urlStr, method, body){ return req(urlStr, { method, headers:{'Content-Type':'application/json'}, body: JSON.stringify(body||{}) }); }

  const dom = {
    pais: { form: document.getElementById('pais-form'), table: document.getElementById('pais-table') },
    prov: { form: document.getElementById('provincia-form'), table: document.getElementById('provincia-table'), selPais: document.querySelector('#provincia-form select[name="idPais"]') },
    dep:  { form: document.getElementById('departamento-form'), table: document.getElementById('departamento-table'), selProv: document.querySelector('#departamento-form select[name="idProvincia"]') },
    loc:  { form: document.getElementById('localidad-form'), table: document.getElementById('localidad-table'), selDep: document.querySelector('#localidad-form select[name="idDepartamento"]') },
    dir:  { form: document.getElementById('direccion-form'), table: document.getElementById('direccion-table'), selPais: document.querySelector('#direccion-form select[name="idPais"]'), selProv: document.querySelector('#direccion-form select[name="idProvincia"]'), selDep: document.querySelector('#direccion-form select[name="idDepartamento"]'), selLoc: document.querySelector('#direccion-form select[name="idLocalidad"]') }
  };

  const state = { paises:[], provincias:[], departamentos:[], localidades:[], paisEdit:null, provEdit:null, depEdit:null, locEdit:null, dirEdit:null, direcciones:[] };

  document.addEventListener('DOMContentLoaded', () => { attach(); initLists(); });

  function attach(){
    dom.pais.form?.addEventListener('submit', onPaisSubmit);
    dom.prov.form?.addEventListener('submit', onProvSubmit);
    dom.dep.form?.addEventListener('submit', onDepSubmit);
    dom.loc.form?.addEventListener('submit', onLocSubmit);
    dom.dir.form?.addEventListener('submit', onDirSubmit);
    dom.dir.selPais?.addEventListener('change', onDirPaisChange);
    dom.dir.selProv?.addEventListener('change', onDirProvChange);
    dom.dir.selDep?.addEventListener('change', onDirDepChange);
  }

  async function initLists(){ await Promise.all([loadPaises(), loadProvincias(), loadDepartamentos(), loadLocalidades(), loadDirecciones()]); renderAll(); fillCascading(); }

  // Loads
  async function loadPaises(){ state.paises = await req(url('/api/direcciones/paises'))||[]; }
  async function loadProvincias(){ state.provincias = await req(url('/api/direcciones/provincias'))||[]; }
  async function loadDepartamentos(){ state.departamentos = await req(url('/api/direcciones/departamentos'))||[]; }
  async function loadLocalidades(){ state.localidades = await req(url('/api/direcciones/localidades'))||[]; }
  async function loadDirecciones(){ state.direcciones = await req(url('/api/direcciones'))||[]; }

  // Render tables
  function renderAll(){ renderPaises(); renderProvincias(); renderDepartamentos(); renderLocalidades(); renderDirecciones(); }
  function renderPaises(){ if(!dom.pais.table) return; const rows = (state.paises||[]).filter(p=>!p.eliminado).map(p=>`<tr data-id="${p.id}"><td>${e(p.nombre)}</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-a="edit-pais" data-id="${p.id}">Editar</button> <button class="btn btn-sm btn-outline-danger" data-a="del-pais" data-id="${p.id}">Borrar</button></td></tr>`).join(''); dom.pais.table.innerHTML = rows; dom.pais.table.onclick = onPaisTableClick; }
  function renderProvincias(){ if(!dom.prov.table) return; const rows = (state.provincias||[]).filter(p=>!p.eliminado).map(p=>`<tr data-id="${p.id}"><td>${e(p.nombre)}</td><td>${e(p.pais?.nombre||'')}</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-a="edit-prov" data-id="${p.id}">Editar</button> <button class="btn btn-sm btn-outline-danger" data-a="del-prov" data-id="${p.id}">Borrar</button></td></tr>`).join(''); dom.prov.table.innerHTML = rows; dom.prov.table.onclick = onProvTableClick; }
  function renderDepartamentos(){ if(!dom.dep.table) return; const rows = (state.departamentos||[]).filter(d=>!d.eliminado).map(d=>`<tr data-id="${d.id}"><td>${e(d.nombre)}</td><td>${e(d.provincia?.nombre||'')}</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-a="edit-dep" data-id="${d.id}">Editar</button> <button class="btn btn-sm btn-outline-danger" data-a="del-dep" data-id="${d.id}">Borrar</button></td></tr>`).join(''); dom.dep.table.innerHTML = rows; dom.dep.table.onclick = onDepTableClick; }
  function renderLocalidades(){ if(!dom.loc.table) return; const rows = (state.localidades||[]).filter(l=>!l.eliminado).map(l=>`<tr data-id="${l.id}"><td>${e(l.nombre)}</td><td>${e(l.departamento?.nombre||'')}</td><td>${e(l.departamento?.provincia?.nombre||'')}</td><td>${e(l.departamento?.provincia?.pais?.nombre||'')}</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-a="edit-loc" data-id="${l.id}">Editar</button> <button class="btn btn-sm btn-outline-danger" data-a="del-loc" data-id="${l.id}">Borrar</button></td></tr>`).join(''); dom.loc.table.innerHTML = rows; dom.loc.table.onclick = onLocTableClick; }
  function renderDirecciones(){ if(!dom.dir.table) return; const rows = (state.direcciones||[]).filter(d=>!d.eliminado).map(d=>`<tr data-id="${d.id}"><td>${e(`${d.calle||''} ${d.numeracion||d.numero||''}`.trim())}</td><td>${e(d.localidad?.nombre||'')}</td><td>${e(d.localidad?.departamento?.nombre||'')}</td><td>${e(d.localidad?.departamento?.provincia?.nombre||'')}</td><td>${e(d.localidad?.departamento?.provincia?.pais?.nombre||'')}</td><td class="text-end"><button class="btn btn-sm btn-outline-primary" data-a="edit-dir" data-id="${d.id}">Editar</button> <button class="btn btn-sm btn-outline-danger" data-a="del-dir" data-id="${d.id}">Borrar</button></td></tr>`).join(''); dom.dir.table.innerHTML = rows; dom.dir.table.onclick = onDirTableClick; }

  // Table events
  async function onPaisTableClick(ev){ const btn = ev.target.closest('button'); if(!btn) return; const id = btn.dataset.id; if(btn.dataset.a==='edit-pais'){ const it = state.paises.find(p=>p.id===id); if(it){ dom.pais.form.nombre.value = it.nombre; dom.pais.form.dataset.editId = id; } } else if(btn.dataset.a==='del-pais'){ if(confirm('¿Borrar país?')){ await req(url(`/api/direcciones/paises/${id}`), { method:'DELETE' }); await loadPaises(); renderPaises(); } } }
  async function onProvTableClick(ev){ const btn = ev.target.closest('button'); if(!btn) return; const id = btn.dataset.id; if(btn.dataset.a==='edit-prov'){ const it = state.provincias.find(p=>p.id===id); if(it){ dom.prov.form.nombre.value = it.nombre; dom.prov.selPais.value = it.pais?.id||''; dom.prov.form.dataset.editId = id; } } else if(btn.dataset.a==='del-prov'){ if(confirm('¿Borrar provincia?')){ await req(url(`/api/direcciones/provincias/${id}`), { method:'DELETE' }); await loadProvincias(); renderProvincias(); } } }
  async function onDepTableClick(ev){ const btn = ev.target.closest('button'); if(!btn) return; const id = btn.dataset.id; if(btn.dataset.a==='edit-dep'){ const it = state.departamentos.find(d=>d.id===id); if(it){ dom.dep.form.nombre.value = it.nombre; dom.dep.selProv.value = it.provincia?.id||''; dom.dep.form.dataset.editId = id; } } else if(btn.dataset.a==='del-dep'){ if(confirm('¿Borrar departamento?')){ await req(url(`/api/direcciones/departamentos/${id}`), { method:'DELETE' }); await loadDepartamentos(); renderDepartamentos(); } } }
  async function onLocTableClick(ev){ const btn = ev.target.closest('button'); if(!btn) return; const id = btn.dataset.id; if(btn.dataset.a==='edit-loc'){ const it = state.localidades.find(l=>l.id===id); if(it){ dom.loc.form.nombre.value = it.nombre; dom.loc.form.codigoPostal.value = it.codigoPostal||''; dom.loc.selDep.value = it.departamento?.id||''; dom.loc.form.dataset.editId = id; } } else if(btn.dataset.a==='del-loc'){ if(confirm('¿Borrar localidad?')){ await req(url(`/api/direcciones/localidades/${id}`), { method:'DELETE' }); await loadLocalidades(); renderLocalidades(); } } }
  async function onDirTableClick(ev){ const btn = ev.target.closest('button'); if(!btn) return; const id = btn.dataset.id; if(btn.dataset.a==='edit-dir'){ const it = state.direcciones.find(d=>d.id===id); if(it){ dom.dir.form.calle.value = it.calle||''; dom.dir.form.numeracion.value = it.numeracion||it.numero||''; dom.dir.form.barrio.value = it.barrio||''; dom.dir.form.manzanaPiso.value = it.manzanaPiso||''; dom.dir.form.casaDepartamento.value = it.casaDepartamento||''; dom.dir.form.referencia.value = it.referencia||''; const paisId = it.localidad?.departamento?.provincia?.pais?.id||''; const provId = it.localidad?.departamento?.provincia?.id||''; const depId = it.localidad?.departamento?.id||''; const locId = it.localidad?.id||''; dom.dir.selPais.value = paisId; await onDirPaisChange(); dom.dir.selProv.value = provId; await onDirProvChange(); dom.dir.selDep.value = depId; await onDirDepChange(); dom.dir.selLoc.value = locId; dom.dir.form.dataset.editId = id; } } else if(btn.dataset.a==='del-dir'){ if(confirm('¿Borrar dirección?')){ await req(url(`/api/direcciones/${id}`), { method:'DELETE' }); await loadDirecciones(); renderDirecciones(); } } }

  // Forms submit
  async function onPaisSubmit(e){ e.preventDefault(); const f = dom.pais.form; const nombre = f.nombre.value.trim(); const editId = f.dataset.editId; try{ if(editId){ const qs = new URLSearchParams(); qs.set('nombre', nombre); await req(url(`/api/direcciones/paises/${editId}?${qs}`), { method:'PUT' }); } else { await send(url('/api/direcciones/paises'), 'POST', { nombre }); } await loadPaises(); renderPaises(); f.reset(); delete f.dataset.editId; }catch(err){ alert('No se pudo guardar país'); } }
  async function onProvSubmit(e){ e.preventDefault(); const f = dom.prov.form; const payload = { nombre: f.nombre.value.trim(), idPais: dom.prov.selPais.value }; const editId = f.dataset.editId; try{ if(editId){ await send(url(`/api/direcciones/provincias/${editId}`), 'PUT', payload); } else { await send(url('/api/direcciones/provincias'), 'POST', payload);} await loadProvincias(); renderProvincias(); f.reset(); delete f.dataset.editId; }catch(err){ alert('No se pudo guardar provincia'); } }
  async function onDepSubmit(e){ e.preventDefault(); const f = dom.dep.form; const payload = { nombre: f.nombre.value.trim(), idProvincia: dom.dep.selProv.value }; const editId = f.dataset.editId; try{ if(editId){ await send(url(`/api/direcciones/departamentos/${editId}`), 'PUT', payload); } else { await send(url('/api/direcciones/departamentos'), 'POST', payload);} await loadDepartamentos(); renderDepartamentos(); f.reset(); delete f.dataset.editId; }catch(err){ alert('No se pudo guardar departamento'); } }
  async function onLocSubmit(e){ e.preventDefault(); const f = dom.loc.form; const payload = { nombre: f.nombre.value.trim(), codigoPostal: f.codigoPostal.value.trim()||null, idDepartamento: dom.loc.selDep.value }; const editId = f.dataset.editId; try{ if(editId){ await send(url(`/api/direcciones/localidades/${editId}`), 'PUT', payload);} else { await send(url('/api/direcciones/localidades'), 'POST', payload);} await loadLocalidades(); renderLocalidades(); f.reset(); delete f.dataset.editId; }catch(err){ alert('No se pudo guardar localidad'); } }
  async function onDirSubmit(e){ e.preventDefault(); const f = dom.dir.form; const payload = { calle: f.calle.value.trim(), numeracion: f.numeracion.value.trim(), barrio: f.barrio.value.trim()||'', manzanaPiso: f.manzanaPiso.value.trim()||'', casaDepartamento: f.casaDepartamento.value.trim()||'', referencia: f.referencia.value.trim()||'', idLocalidad: dom.dir.selLoc.value }; const editId = f.dataset.editId; try{ if(editId){ await send(url(`/api/direcciones/${editId}`), 'PUT', payload);} else { await send(url('/api/direcciones'), 'POST', payload);} await loadDirecciones(); renderDirecciones(); f.reset(); delete f.dataset.editId; }catch(err){ alert('No se pudo guardar dirección'); } }

  // Cascading selects for Direccion form (v1 lookups)
  async function fillCascading(){ // initial fill
    await fillPaises(dom.dir.selPais);
  }
  async function fillPaises(select){ if(!select) return; const data = await req(url('/api/v1/paises/activos'))||[]; fillSelect(select, data.map(p=>({value:p.id,label:p.nombre})), 'País'); clearSelect(dom.dir.selProv, 'Provincia'); clearSelect(dom.dir.selDep, 'Departamento'); clearSelect(dom.dir.selLoc, 'Localidad'); }
  async function onDirPaisChange(){ const id = dom.dir.selPais?.value; if(!id){ clearSelect(dom.dir.selProv,'Provincia'); clearSelect(dom.dir.selDep,'Departamento'); clearSelect(dom.dir.selLoc,'Localidad'); return; } const data = await req(url(`/api/v1/provincias?paisId=${encodeURIComponent(id)}`))||[]; fillSelect(dom.dir.selProv, data.map(x=>({value:x.id,label:x.nombre})), 'Provincia'); clearSelect(dom.dir.selDep,'Departamento'); clearSelect(dom.dir.selLoc,'Localidad'); }
  async function onDirProvChange(){ const id = dom.dir.selProv?.value; if(!id){ clearSelect(dom.dir.selDep,'Departamento'); clearSelect(dom.dir.selLoc,'Localidad'); return; } const data = await req(url(`/api/v1/departamentos?provinciaId=${encodeURIComponent(id)}`))||[]; fillSelect(dom.dir.selDep, data.map(x=>({value:x.id,label:x.nombre})), 'Departamento'); clearSelect(dom.dir.selLoc,'Localidad'); }
  async function onDirDepChange(){ const id = dom.dir.selDep?.value; if(!id){ clearSelect(dom.dir.selLoc,'Localidad'); return; } const data = await req(url(`/api/v1/localidades?departamentoId=${encodeURIComponent(id)}`))||[]; fillSelect(dom.dir.selLoc, data.map(x=>({value:x.id,label:`${x.nombre}${x.codigoPostal?` (${x.codigoPostal})`:''}`})), 'Localidad'); }

  function fillSelect(select, options, placeholder){ if(!select) return; select.innerHTML = ''; const ph = document.createElement('option'); ph.value=''; ph.textContent = placeholder||'Seleccione'; select.appendChild(ph); (options||[]).forEach(o=>{ const opt=document.createElement('option'); opt.value=o.value; opt.textContent=o.label; select.appendChild(opt); }); }
  function clearSelect(select, placeholder){ if(!select) return; select.innerHTML = ''; const ph=document.createElement('option'); ph.value=''; ph.textContent=placeholder||'Seleccione'; select.appendChild(ph); }
  function e(s){ return (s||'').toString().replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[c])); }
})();
