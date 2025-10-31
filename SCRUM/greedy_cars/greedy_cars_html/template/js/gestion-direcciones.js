// In-memory management UI for Direcciones (paises, provincias, departamentos, localidades, direcciones)
(function(){
  // Stores
  const paises = [];
  const provincias = [];
  const departamentos = [];
  const localidades = [];
  const direcciones = [];

  function uid(prefix='id'){ return prefix + '-' + Math.random().toString(36).slice(2,9); }

  // CRUD helpers
  function crearPais(nombre){ if(!nombre) throw new Error('Nombre requerido'); const p={id:uid('pais'), nombre, eliminado:false}; paises.push(p); renderPaises(); return p; }
  function listarPaises(){ return paises.slice(); }

  function crearProvincia(nombre, idPais){ if(!nombre) throw new Error('Nombre requerido'); if(!idPais) throw new Error('País requerido'); const pr={id:uid('prov'), nombre, idPais, eliminado:false}; provincias.push(pr); renderProvincias(); return pr; }
  function listarProvincias(){ return provincias.slice(); }

  function crearDepartamento(nombre, idProvincia){ if(!nombre) throw new Error('Nombre requerido'); if(!idProvincia) throw new Error('Provincia requerida'); const d={id:uid('dep'), nombre, idProvincia, eliminado:false}; departamentos.push(d); renderDepartamentos(); return d; }
  function listarDepartamentos(){ return departamentos.slice(); }

  function crearLocalidad(nombre, codigoPostal, idDepartamento){ if(!nombre) throw new Error('Nombre requerido'); if(!idDepartamento) throw new Error('Departamento requerido'); const l={id:uid('loc'), nombre, codigoPostal:codigoPostal||'', idDepartamento, eliminado:false}; localidades.push(l); renderLocalidades(); return l; }
  function listarLocalidades(){ return localidades.slice(); }

  function crearDireccion(payload){
    // payload: {calle,numeracion,idLocalidad,barrio,manzanaPiso,casaDepartamento,referencia}
    if(!payload || !payload.calle || !payload.numeracion || !payload.idLocalidad) throw new Error('Calle, numeración y localidad son requeridos');
    const dir = Object.assign({ id: uid('dir'), eliminado:false }, payload);
    direcciones.push(dir);
    renderDireccionesTable();
    return dir;
  }

  function listarDirecciones(){ return direcciones.slice(); }

  function findById(list,id){ return list.find(x=>x.id===id) || null; }

  // Render functions
  function renderPaises(){
    const tbody = document.getElementById('pais-table'); if(!tbody) return; tbody.innerHTML='';
    listarPaises().forEach(p=>{
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${p.nombre}</td><td class="text-end"><button class="btn btn-sm btn-outline-danger" data-action="del-pais" data-id="${p.id}">Eliminar</button></td>`;
      tbody.appendChild(tr);
    });
    // populate selects
    document.querySelectorAll('#provincia-pais-select, #direccion-pais').forEach(sel=>{
      sel.innerHTML = '<option value="">País</option>';
      listarPaises().forEach(p=>{ const o=document.createElement('option'); o.value=p.id; o.textContent=p.nombre; sel.appendChild(o); });
    });
  }

  function renderProvincias(){ const tbody=document.getElementById('provincia-table'); if(!tbody) return; tbody.innerHTML=''; listarProvincias().forEach(pr=>{
      const pais=findById(paises,pr.idPais); const tr=document.createElement('tr'); tr.innerHTML=`<td>${pr.nombre}</td><td>${pais?pais.nombre:''}</td><td class="text-end"><button class="btn btn-sm btn-outline-danger" data-action="del-prov" data-id="${pr.id}">Eliminar</button></td>`; tbody.appendChild(tr);
    });
    document.querySelectorAll('#departamento-provincia-select, #direccion-provincia').forEach(sel=>{
      sel.innerHTML = '<option value="">Provincia</option>';
      listarProvincias().forEach(pr=>{ const o=document.createElement('option'); o.value=pr.id; o.textContent=pr.nombre; sel.appendChild(o); });
    });
  }

  function renderDepartamentos(){ const tbody=document.getElementById('departamento-table'); if(!tbody) return; tbody.innerHTML=''; listarDepartamentos().forEach(dep=>{
      const prov=findById(provincias,dep.idProvincia); const tr=document.createElement('tr'); tr.innerHTML=`<td>${dep.nombre}</td><td>${prov?prov.nombre:''}</td><td class="text-end"><button class="btn btn-sm btn-outline-danger" data-action="del-dep" data-id="${dep.id}">Eliminar</button></td>`; tbody.appendChild(tr);
    });
    document.querySelectorAll('#localidad-departamento-select, #direccion-departamento').forEach(sel=>{
      sel.innerHTML = '<option value="">Departamento</option>';
      listarDepartamentos().forEach(dep=>{ const o=document.createElement('option'); o.value=dep.id; o.textContent=dep.nombre; sel.appendChild(o); });
    });
  }

  function renderLocalidades(){ const tbody=document.getElementById('localidad-table'); if(!tbody) return; tbody.innerHTML=''; listarLocalidades().forEach(loc=>{
      const dep=findById(departamentos,loc.idDepartamento); const prov=findById(provincias, dep?dep.idProvincia:null); const pais=findById(paises, prov?prov.idPais:null);
      const tr=document.createElement('tr'); tr.innerHTML=`<td>${loc.nombre}</td><td>${dep?dep.nombre:''}</td><td>${prov?prov.nombre:''}</td><td>${pais?pais.nombre:''}</td><td class="text-end"><button class="btn btn-sm btn-outline-danger" data-action="del-loc" data-id="${loc.id}">Eliminar</button></td>`; tbody.appendChild(tr);
    });
    document.querySelectorAll('#direccion-localidad').forEach(sel=>{
      sel.innerHTML = '<option value="">Localidad</option>';
      listarLocalidades().forEach(loc=>{ const o=document.createElement('option'); o.value=loc.id; o.textContent=loc.nombre; sel.appendChild(o); });
    });
  }

  function renderDireccionesTable(){ const tbody=document.getElementById('direccion-table'); if(!tbody) return; tbody.innerHTML=''; listarDirecciones().forEach(dir=>{
      const loc=findById(localidades,dir.idLocalidad); const dep=findById(departamentos, loc?loc.idDepartamento:null); const prov=findById(provincias, dep?dep.idProvincia:null); const pais=findById(paises, prov?prov.idPais:null);
      const tr=document.createElement('tr'); tr.innerHTML=`<td>${dir.calle} ${dir.numeracion}</td><td>${loc?loc.nombre:''}</td><td>${dep?dep.nombre:''}</td><td>${prov?prov.nombre:''}</td><td>${pais?pais.nombre:''}</td><td class="text-end"><button class="btn btn-sm btn-outline-danger" data-action="del-dir" data-id="${dir.id}">Eliminar</button></td>`; tbody.appendChild(tr);
    }); }

  // Deletions (soft delete)
  function eliminarPais(id){ const p=findById(paises,id); if(!p) throw new Error('Pais no encontrado'); p.eliminado=true; renderPaises(); }
  function eliminarProvincia(id){ const pr=findById(provincias,id); if(!pr) throw new Error('Provincia no encontrada'); pr.eliminado=true; renderProvincias(); }
  function eliminarDepartamento(id){ const d=findById(departamentos,id); if(!d) throw new Error('Departamento no encontrado'); d.eliminado=true; renderDepartamentos(); }
  function eliminarLocalidad(id){ const l=findById(localidades,id); if(!l) throw new Error('Localidad no encontrada'); l.eliminado=true; renderLocalidades(); }
  function eliminarDireccion(id){ const d=findById(direcciones,id); if(!d) throw new Error('Dirección no encontrada'); d.eliminado=true; renderDireccionesTable(); }

  // DOM wiring
  document.addEventListener('DOMContentLoaded', ()=>{
    // Forms
    const paisForm = document.getElementById('pais-form');
    const provinciaForm = document.getElementById('provincia-form');
    const departamentoForm = document.getElementById('departamento-form');
    const localidadForm = document.getElementById('localidad-form');
    const direccionForm = document.getElementById('direccion-form');

    // Selects
    const provPaisSel = document.getElementById('provincia-pais-select');
    const depProvSel = document.getElementById('departamento-provincia-select');
    const locDepSel = document.getElementById('localidad-departamento-select');
    const dirPaisSel = document.getElementById('direccion-pais');
    const dirProvSel = document.getElementById('direccion-provincia');
    const dirDepSel = document.getElementById('direccion-departamento');
    const dirLocSel = document.getElementById('direccion-localidad');

    // Handlers
    paisForm.addEventListener('submit', (ev)=>{ ev.preventDefault(); const name = paisForm.nombre.value.trim(); if(!name) return alert('Nombre requerido'); crearPais(name); paisForm.reset(); });

    provinciaForm.addEventListener('submit', (ev)=>{ ev.preventDefault(); const name = provinciaForm.nombre.value.trim(); const idPais = provinciaForm.idPais.value; if(!name || !idPais) return alert('Nombre y país requeridos'); crearProvincia(name, idPais); provinciaForm.reset(); });

    departamentoForm.addEventListener('submit', (ev)=>{ ev.preventDefault(); const name = departamentoForm.nombre.value.trim(); const idProv = departamentoForm.idProvincia.value; if(!name || !idProv) return alert('Nombre y provincia requeridos'); crearDepartamento(name, idProv); departamentoForm.reset(); });

    localidadForm.addEventListener('submit', (ev)=>{ ev.preventDefault(); const name = document.getElementById('localidad-nombre').value.trim(); const cp = document.getElementById('localidad-cp').value.trim(); const idDep = localidadForm.idDepartamento.value; if(!name || !idDep) return alert('Nombre y departamento requeridos'); crearLocalidad(name, cp, idDep); localidadForm.reset(); });

    direccionForm.addEventListener('submit', (ev)=>{ ev.preventDefault(); const calle = document.getElementById('direccion-calle').value.trim(); const numer = document.getElementById('direccion-numeracion').value.trim(); const idLoc = direccionForm.idLocalidad.value; const payload = { calle, numeracion: numer, idLocalidad: idLoc, barrio: document.getElementById('direccion-barrio').value.trim(), manzanaPiso: document.getElementById('direccion-manzana').value.trim(), casaDepartamento: document.getElementById('direccion-casa').value.trim(), referencia: document.getElementById('direccion-referencia').value.trim() };
      try{ crearDireccion(payload); direccionForm.reset(); }catch(err){ alert(err.message); }
    });

    // Cascading: when country changed, populate provinces filter
    document.addEventListener('change', (ev)=>{
      if(ev.target === provPaisSel){ /* no-op here */ }
      if(ev.target === dirPaisSel){
        const pid = dirPaisSel.value;
        // populate dirProvSel with provinces for pid
        dirProvSel.innerHTML = '<option value="">Provincia</option>';
        provincias.filter(p=>p.idPais===pid).forEach(pr=>{ const o=document.createElement('option'); o.value=pr.id; o.textContent=pr.nombre; dirProvSel.appendChild(o); });
      }
      if(ev.target === dirProvSel){ const prid=dirProvSel.value; dirDepSel.innerHTML = '<option value="">Departamento</option>'; departamentos.filter(d=>d.idProvincia===prid).forEach(dep=>{ const o=document.createElement('option'); o.value=dep.id; o.textContent=dep.nombre; dirDepSel.appendChild(o); }); }
      if(ev.target === dirDepSel){ const did=dirDepSel.value; dirLocSel.innerHTML = '<option value="">Localidad</option>'; localidades.filter(l=>l.idDepartamento===did).forEach(loc=>{ const o=document.createElement('option'); o.value=loc.id; o.textContent=loc.nombre; dirLocSel.appendChild(o); }); }
    });

    // Table action delegation
    document.getElementById('pais-table').addEventListener('click',(ev)=>{ const btn=ev.target.closest('button'); if(!btn) return; if(btn.dataset.action==='del-pais') eliminarPais(btn.dataset.id); });
    document.getElementById('provincia-table').addEventListener('click',(ev)=>{ const btn=ev.target.closest('button'); if(!btn) return; if(btn.dataset.action==='del-prov') eliminarProvincia(btn.dataset.id); });
    document.getElementById('departamento-table').addEventListener('click',(ev)=>{ const btn=ev.target.closest('button'); if(!btn) return; if(btn.dataset.action==='del-dep') eliminarDepartamento(btn.dataset.id); });
    document.getElementById('localidad-table').addEventListener('click',(ev)=>{ const btn=ev.target.closest('button'); if(!btn) return; if(btn.dataset.action==='del-loc') eliminarLocalidad(btn.dataset.id); });
    document.getElementById('direccion-table').addEventListener('click',(ev)=>{ const btn=ev.target.closest('button'); if(!btn) return; if(btn.dataset.action==='del-dir') eliminarDireccion(btn.dataset.id); });

    // initial render
    renderPaises(); renderProvincias(); renderDepartamentos(); renderLocalidades(); renderDireccionesTable();
  });

  // Expose API for testing
  window.DireccionesAPI = { crearPais, listarPaises, crearProvincia, listarProvincias, crearDepartamento, listarDepartamentos, crearLocalidad, listarLocalidades, crearDireccion, listarDirecciones };

})();
