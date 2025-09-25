(function(){
  const state = {
    mensajes: [],
    promociones: [],
    socios: [],
    mensajeEditId: null,
    promocionEditId: null
  };

  const dom = {
    mensajeForm: document.getElementById('mensaje-form'),
    mensajeReset: document.querySelector('[data-action="mensaje-reset"]'),
    mensajesTable: document.getElementById('mensajes-table'),
    promocionForm: document.getElementById('promocion-form'),
    promocionReset: document.querySelector('[data-action="promocion-reset"]'),
    promocionesTable: document.getElementById('promociones-table'),
    destinatariosSelect: document.querySelector('#promocion-form select[name="destinatarios"]'),
    enviarTodosCheckbox: document.getElementById('promocion-enviar-todos')
  };

  function normaliseContextPath(path){
    if(!path || path === '/') return '';
    return path.endsWith('/') ? path.slice(0,-1) : path;
  }
  function buildUrl(path){
    const raw = document.body ? document.body.dataset.contextPath || '' : '';
    const base = normaliseContextPath(raw);
    if(!path) return base || '';
    const clean = path.startsWith('/') ? path : '/' + path;
    return base ? base + clean : clean;
  }

  document.addEventListener('DOMContentLoaded', init);

  function init(){
    attachEvents();
    cargarSocios();
    cargarMensajes();
    cargarPromociones();
    syncDestinatariosState();
  }

  function attachEvents(){
    if(dom.mensajeForm){
      dom.mensajeForm.addEventListener('submit', onMensajeSubmit);
    }
    if(dom.mensajeReset){
      dom.mensajeReset.addEventListener('click', resetMensajeForm);
    }
    if(dom.mensajesTable){
      dom.mensajesTable.addEventListener('click', onMensajesTableClick);
    }
    if(dom.promocionForm){
      dom.promocionForm.addEventListener('submit', onPromocionSubmit);
    }
    if(dom.promocionReset){
      dom.promocionReset.addEventListener('click', resetPromocionForm);
    }
    if(dom.promocionesTable){
      dom.promocionesTable.addEventListener('click', onPromocionesTableClick);
    }
    if(dom.enviarTodosCheckbox){
      dom.enviarTodosCheckbox.addEventListener('change', syncDestinatariosState);
    }
  }

  async function cargarSocios(){
    try {
      const res = await fetch(buildUrl('/api/v1/socios/activos'), { credentials: 'same-origin' });
      if(!res.ok) throw new Error('HTTP '+res.status);
      state.socios = await res.json();
      renderSocios();
    } catch (err) {
      console.error('Error cargando socios', err);
    }
  }

  function renderSocios(){
    if(!dom.destinatariosSelect) return;
    dom.destinatariosSelect.innerHTML = state.socios.map(s => `
      <option value="${s.id}">${escapeHtml(s.apellido||'')} ${escapeHtml(s.nombre||'')} - ${escapeHtml(s.correoElectronico||'')}</option>
    `).join('');
    syncDestinatariosState();
  }

  async function cargarMensajes(){
    try {
      const res = await fetch(buildUrl('/api/admin/mensajes'), { credentials: 'same-origin' });
      if(!res.ok) throw new Error('HTTP '+res.status);
      state.mensajes = await res.json();
      renderMensajes();
    } catch (err) {
      console.error('Error cargando mensajes', err);
    }
  }

  function renderMensajes(){
    if(!dom.mensajesTable) return;
    dom.mensajesTable.innerHTML = state.mensajes.map(m => {
      const badge = m.eliminado ? '<span class="badge bg-secondary badge-status">Inactivo</span>' : '<span class="badge bg-success badge-status">Activo</span>';
      return `
        <tr data-id="${m.id}">
          <td>${escapeHtml(m.titulo||'')}</td>
          <td>${escapeHtml(formatearTipo(m.tipo||''))}</td>
          <td>${badge}</td>
          <td class="text-end">
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-outline-secondary" data-action="apply">Usar plantilla</button>
              <button type="button" class="btn btn-outline-primary" data-action="edit">Editar</button>
              <button type="button" class="btn btn-outline-danger" data-action="delete">Eliminar</button>
            </div>
          </td>
        </tr>
      `;
    }).join('');
  }

  function onMensajesTableClick(e){
    const btn = e.target.closest('button[data-action]');
    if(!btn) return;
    const tr = btn.closest('tr[data-id]');
    if(!tr) return;
    const id = tr.getAttribute('data-id');
    if(btn.dataset.action === 'edit'){
      const mensaje = state.mensajes.find(m => m.id === id);
      if(mensaje) fillMensajeForm(mensaje);
    } else if(btn.dataset.action === 'apply'){
      const plantilla = state.mensajes.find(m => m.id === id);
      if(plantilla) aplicarPlantillaEnPromocion(plantilla);
    } else if(btn.dataset.action === 'delete'){
      if(confirm('¿Eliminar esta plantilla de mensaje?')){
        eliminarMensaje(id);
      }
    }
  }

  async function eliminarMensaje(id){
    try {
      const res = await fetch(buildUrl(`/api/admin/mensajes/${id}`), {
        method: 'DELETE',
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' }
      });
      if(!res.ok) throw new Error('HTTP '+res.status);
      await cargarMensajes();
      if(state.mensajeEditId === id){
        resetMensajeForm();
      }
    } catch(err){
      alert('No se pudo eliminar el mensaje');
      console.error(err);
    }
  }

  function fillMensajeForm(mensaje){
    if(!dom.mensajeForm) return;
    state.mensajeEditId = mensaje.id;
    dom.mensajeForm.querySelector('input[name="id"]').value = mensaje.id;
    dom.mensajeForm.querySelector('input[name="titulo"]').value = mensaje.titulo || '';
    dom.mensajeForm.querySelector('textarea[name="texto"]').value = mensaje.texto || '';
    dom.mensajeForm.querySelector('select[name="tipo"]').value = mensaje.tipo || 'PROMOCION';
  }

  function resetMensajeForm(){
    if(!dom.mensajeForm) return;
    state.mensajeEditId = null;
    dom.mensajeForm.reset();
    dom.mensajeForm.querySelector('input[name="id"]').value = '';
    dom.mensajeForm.querySelector('select[name="tipo"]').value = 'PROMOCION';
  }

  async function onMensajeSubmit(e){
    e.preventDefault();
    const form = dom.mensajeForm;
    const payload = {
      titulo: form.titulo.value.trim(),
      texto: form.texto.value.trim(),
      tipo: form.tipo.value
    };
    const id = state.mensajeEditId;
    const method = id ? 'PUT' : 'POST';
    const url = id ? buildUrl(`/api/admin/mensajes/${id}`) : buildUrl('/api/admin/mensajes');
    try {
      const res = await fetch(url, {
        method,
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if(!res.ok) throw new Error('HTTP '+res.status);
      await cargarMensajes();
      resetMensajeForm();
      alert('Mensaje guardado correctamente');
    } catch(err){
      alert('No se pudo guardar el mensaje');
      console.error(err);
    }
  }

  async function cargarPromociones(){
    try {
      const res = await fetch(buildUrl('/api/admin/promociones'), { credentials: 'same-origin' });
      if(!res.ok) throw new Error('HTTP '+res.status);
      state.promociones = await res.json();
      renderPromociones();
    } catch(err){
      console.error('Error cargando promociones', err);
    }
  }

  function renderPromociones(){
    if(!dom.promocionesTable) return;
    dom.promocionesTable.innerHTML = state.promociones.map(p => {
      const countDest = Array.isArray(p.destinatariosIds) ? p.destinatariosIds.length : 0;
      const destino = p.enviarATodos ? 'Todos los socios' : `${countDest} seleccionado${countDest === 1 ? '' : 's'}`;
      const estado = p.enviada
        ? `<span class="badge bg-success badge-status">Enviada</span>`
        : `<span class="badge bg-warning text-dark badge-status">Pendiente</span>`;
      const fecha = p.fechaEnvioPromocion ? formatearFecha(p.fechaEnvioPromocion) : '-';
      const enviados = typeof p.cantidadSociosEnviados === 'number' ? p.cantidadSociosEnviados : 0;
      return `
        <tr data-id="${p.id}">
          <td>${escapeHtml(p.titulo||'')}</td>
          <td>${fecha}</td>
          <td>${escapeHtml(destino)}</td>
          <td>${estado}</td>
          <td>${enviados}</td>
          <td class="text-end">
            <div class="btn-group btn-group-sm">
              <button type="button" class="btn btn-outline-primary" data-action="edit">Planificar</button>
              <button type="button" class="btn btn-outline-success" data-action="send" ${p.enviada ? 'disabled' : ''}>Enviar ahora</button>
              <button type="button" class="btn btn-outline-danger" data-action="delete">Eliminar</button>
            </div>
          </td>
        </tr>
      `;
    }).join('');
  }

  function onPromocionesTableClick(e){
    const btn = e.target.closest('button[data-action]');
    if(!btn) return;
    const tr = btn.closest('tr[data-id]');
    if(!tr) return;
    const id = tr.getAttribute('data-id');
    if(btn.dataset.action === 'edit'){
      const promo = state.promociones.find(p => p.id === id);
      if(promo) fillPromocionForm(promo);
    } else if(btn.dataset.action === 'delete'){
      if(confirm('¿Eliminar la promoción?')){
        eliminarPromocion(id);
      }
    } else if(btn.dataset.action === 'send'){
      if(confirm('¿Enviar ahora la promoción seleccionada?')){
        enviarPromocion(id);
      }
    }
  }

  function fillPromocionForm(promo){
    state.promocionEditId = promo.id;
    dom.promocionForm.querySelector('input[name="id"]').value = promo.id;
    dom.promocionForm.querySelector('input[name="titulo"]').value = promo.titulo || '';
    dom.promocionForm.querySelector('textarea[name="texto"]').value = promo.texto || '';
    const fechaInput = dom.promocionForm.querySelector('input[name="fechaEnvio"]');
    if(promo.fechaEnvioPromocion){
      fechaInput.value = toDateTimeLocal(promo.fechaEnvioPromocion);
    } else {
      fechaInput.value = '';
    }
    const enviarTodos = Boolean(promo.enviarATodos);
    if(dom.enviarTodosCheckbox){
      dom.enviarTodosCheckbox.checked = enviarTodos;
    }
    const seleccion = new Set(promo.destinatariosIds || []);
    Array.from(dom.destinatariosSelect.options).forEach(opt => {
      opt.selected = !enviarTodos && seleccion.has(opt.value);
    });
    syncDestinatariosState();
  }

  function resetPromocionForm(){
    state.promocionEditId = null;
    dom.promocionForm.reset();
    dom.promocionForm.querySelector('input[name="id"]').value = '';
    if(dom.destinatariosSelect){
      Array.from(dom.destinatariosSelect.options).forEach(opt => opt.selected = false);
    }
    if(dom.enviarTodosCheckbox){
      dom.enviarTodosCheckbox.checked = false;
    }
    syncDestinatariosState();
  }

  async function eliminarPromocion(id){
    try {
      const res = await fetch(buildUrl(`/api/admin/promociones/${id}`), {
        method: 'DELETE',
        credentials: 'same-origin'
      });
      if(!res.ok) throw new Error('HTTP '+res.status);
      await cargarPromociones();
      if(state.promocionEditId === id) resetPromocionForm();
    } catch(err){
      alert('No se pudo eliminar la promoción');
      console.error(err);
    }
  }

  async function enviarPromocion(id, opts){
    try {
      const res = await fetch(buildUrl(`/api/admin/promociones/${id}/enviar`), {
        method: 'POST',
        credentials: 'same-origin'
      });
      if(!res.ok) throw new Error('HTTP '+res.status);
      await cargarPromociones();
      if(!opts || !opts.silent){
        alert('Promoción enviada');
      }
      return true;
    } catch(err){
      alert('No se pudo enviar la promoción');
      console.error(err);
      return false;
    }
  }

  async function onPromocionSubmit(e){
    e.preventDefault();
    const form = dom.promocionForm;
    const enviarTodos = form.enviarTodos?.checked ?? false;
    const selectedIds = enviarTodos ? [] : Array.from(form.destinatarios.selectedOptions || []).map(opt => opt.value);
    const payload = {
      titulo: form.titulo.value.trim(),
      texto: form.texto.value.trim(),
      fechaEnvio: form.fechaEnvio.value ? new Date(form.fechaEnvio.value).toISOString() : null,
      destinatarios: selectedIds,
      enviarATodos: enviarTodos
    };
    if(!payload.fechaEnvio){
      alert('Seleccioná fecha y hora de envío');
      return;
    }
    if(!enviarTodos && selectedIds.length === 0){
      alert('Seleccioná al menos un destinatario o marcá la opción para enviar a todos.');
      return;
    }
    const id = state.promocionEditId;
    const method = id ? 'PUT' : 'POST';
    const url = id ? buildUrl(`/api/admin/promociones/${id}`) : buildUrl('/api/admin/promociones');
    const action = e.submitter?.dataset?.action || 'planificar';
    try {
      const res = await fetch(url, {
        method,
        credentials: 'same-origin',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      if(!res.ok) throw new Error('HTTP '+res.status);
      const data = await res.json();
      const promoId = data?.id || id;
      await cargarPromociones();
      resetPromocionForm();
      if(action === 'enviar-ahora' && promoId){
        const ok = await enviarPromocion(promoId, { silent: true });
        if(ok){
          alert('Promoción enviada');
        }
      } else {
        alert('Promoción planificada');
      }
    } catch(err){
      alert('No se pudo guardar la promoción');
      console.error(err);
    }
  }

  function formatearTipo(tipo){
    if(!tipo) return '';
    return tipo.toString().toLowerCase().replace(/_/g,' ').replace(/^.|\s./g, c => c.toUpperCase());
  }

  function formatearFecha(fechaIso){
    try {
      const d = new Date(fechaIso);
      if(isNaN(d.getTime())) return fechaIso;
      return d.toLocaleString();
    } catch(err){
      return fechaIso;
    }
  }

  function toDateTimeLocal(fechaIso){
    const d = new Date(fechaIso);
    if(isNaN(d.getTime())) return '';
    const tzOffset = d.getTimezoneOffset() * 60000;
    const local = new Date(d.getTime() - tzOffset);
    return local.toISOString().slice(0,16);
  }

  function escapeHtml(str){
    return (str||'').replace(/[&<>"']/g, c => ({
      '&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'
    }[c]));
  }

  function syncDestinatariosState(){
    if(!dom.destinatariosSelect) return;
    const enviarTodos = dom.enviarTodosCheckbox?.checked;
    dom.destinatariosSelect.disabled = Boolean(enviarTodos);
    if(enviarTodos){
      Array.from(dom.destinatariosSelect.options).forEach(opt => opt.selected = false);
    }
  }

  function aplicarPlantillaEnPromocion(plantilla){
    if(!dom.promocionForm) return;
    dom.promocionForm.querySelector('input[name="titulo"]').value = plantilla.titulo || '';
    dom.promocionForm.querySelector('textarea[name="texto"]').value = plantilla.texto || '';
    dom.promocionForm.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }
})();
