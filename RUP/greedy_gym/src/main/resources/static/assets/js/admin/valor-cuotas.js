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

  const dom = {
    form: document.getElementById('valor-form'),
    resetBtn: document.getElementById('valor-reset'),
    tableBody: document.getElementById('valor-table-body'),
    btnCrear: document.getElementById('btn-crear-valor'),
    
    // Current value display
    valorVigente: document.getElementById('valor-vigente'),
    importeVigente: document.querySelector('[data-field="importe"]'),
    fechaVigente: document.querySelector('[data-field="vigenciaDesde"]'),
    
    // Form fields
    fechaDesdeInput: document.querySelector("input[name='fechaDesde']"),
    fechaHastaInput: document.querySelector("input[name='fechaHasta']"),
    valorCuotaInput: document.querySelector("input[name='valorCuota']")
  };

  const state = { 
    items: [], 
    editingId: null,
    valorVigente: null
  };

  document.addEventListener('DOMContentLoaded', () => {
    attach();
    listar();
    cargarValorVigente();
  });

  function attach(){
    dom.form?.addEventListener('submit', onSubmit);
    dom.resetBtn?.addEventListener('click', resetForm);
    dom.tableBody?.addEventListener('click', onTableClick);
    // Avoid binding submit button to extra click handlers that might reset the form before submit
  }

  async function onSubmit(e){
    e.preventDefault();
    const formData = new FormData(dom.form);
    
    try {
      if(state.editingId) {
        await actualizarValor(state.editingId, formData);
      } else {
        await crearValor(formData);
      }
      resetForm();
      await Promise.all([listar(), cargarValorVigente()]);
    } catch(error) {
      alert('Error: ' + error.message);
    }
  }

  async function crearValor(formData) {
    const params = new URLSearchParams();
    params.set('fechaDesde', formData.get('fechaDesde') || '');
    const fechaHasta = formData.get('fechaHasta');
    if(fechaHasta) {
      params.set('fechaHasta', fechaHasta);
    }
    params.set('valorCuota', formData.get('valorCuota') || '');
    
    const url = `${buildUrl('/api/valor-cuotas')}?${params.toString()}`;
    await requestJson(url, { method: 'POST' });
  }

  async function actualizarValor(id, formData) {
    const params = new URLSearchParams();
    params.set('fechaDesde', formData.get('fechaDesde') || '');
    const fechaHasta = formData.get('fechaHasta');
    if(fechaHasta) {
      params.set('fechaHasta', fechaHasta);
    }
    params.set('valorCuota', formData.get('valorCuota') || '');
    
    const url = `${buildUrl('/api/valor-cuotas/' + id)}?${params.toString()}`;
    await requestJson(url, { method: 'PUT' });
  }

  async function eliminarValor(id) {
    if(!confirm('¿Está seguro de eliminar este valor de cuota?')) return;
    try {
      await requestJson(buildUrl('/api/valor-cuotas/' + id), { method: 'DELETE' });
      await Promise.all([listar(), cargarValorVigente()]);
    } catch(error) {
      alert('Error eliminando valor: ' + error.message);
    }
  }

  function resetForm(){
    state.editingId = null;
    dom.form?.reset();
    
    // Set default fecha desde to today
    if(dom.fechaDesdeInput) {
      const today = new Date().toISOString().split('T')[0];
      dom.fechaDesdeInput.value = today;
    }
    
    if(dom.btnCrear) {
      dom.btnCrear.textContent = 'Crear Valor';
    }
    
    // Enable all fields
    ['fechaDesde', 'fechaHasta', 'valorCuota'].forEach(name => {
      const field = document.querySelector(`[name="${name}"]`);
      if(field) field.disabled = false;
    });
  }

  function prepareCreate(){
    resetForm();
  }

  function editarValor(id){
    const valor = state.items.find(v => v.id === id);
    if(!valor) return;

    state.editingId = id;
    
    if(dom.fechaDesdeInput) {
      dom.fechaDesdeInput.value = valor.fechaDesde || '';
    }
    if(dom.fechaHastaInput) {
      dom.fechaHastaInput.value = valor.fechaHasta || '';
    }
    if(dom.valorCuotaInput) {
      dom.valorCuotaInput.value = valor.valorCuota || '';
    }
    
    if(dom.btnCrear) {
      dom.btnCrear.textContent = 'Actualizar Valor';
    }
  }

  async function cargarValorVigente() {
    try {
      // Prefer the activos endpoint to determine the vigente without causing 500s
      const activos = await requestJson(buildUrl('/api/valor-cuotas/activos')) || [];
      let vigente = null;
      if (Array.isArray(activos) && activos.length > 0) {
        // Choose the one without fechaHasta or the most recent fechaDesde
        vigente = activos.find(v => !v.fechaHasta) || activos.slice().sort((a,b) => new Date(b.fechaDesde||'1900-01-01') - new Date(a.fechaDesde||'1900-01-01'))[0];
      } else {
        // Fallback to /vigente in case activos returns empty but there's a vigente
        try {
          vigente = await requestJson(buildUrl('/api/valor-cuotas/vigente'));
        } catch (_) {
          // ignore
        }
      }

      state.valorVigente = vigente || null;

      if(vigente) {
        if(dom.importeVigente) {
          dom.importeVigente.textContent = (Number(vigente.valorCuota)||0).toFixed(2);
        }
        if(dom.fechaVigente) {
          dom.fechaVigente.textContent = vigente.fechaDesde || '—';
        }
      } else {
        if(dom.importeVigente) dom.importeVigente.textContent = '—';
        if(dom.fechaVigente) dom.fechaVigente.textContent = '—';
      }
    } catch(error) {
      // Gracefully show no vigente without spamming errors if service throws 500
      if(dom.importeVigente) dom.importeVigente.textContent = '—';
      if(dom.fechaVigente) dom.fechaVigente.textContent = '—';
    }
  }

  async function listar(){
    try {
      const data = await requestJson(buildUrl('/api/valor-cuotas'))||[];
      state.items = data;
      render(data);
    } catch(error) {
      console.error('Error listando valores:', error);
    }
  }

  function render(items = state.items){
    if(!dom.tableBody) return;
    
    dom.tableBody.innerHTML = '';
    
    if(items.length === 0) {
      dom.tableBody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay valores de cuota registrados</td></tr>';
      return;
    }
    
    // Sort by fechaDesde descending (most recent first)
    const sortedItems = [...items].sort((a, b) => {
      const dateA = new Date(a.fechaDesde || '1900-01-01');
      const dateB = new Date(b.fechaDesde || '1900-01-01');
      return dateB - dateA;
    });
    
    sortedItems.forEach(valor => {
      const row = document.createElement('tr');
      const isVigente = state.valorVigente && state.valorVigente.id === valor.id;
      
      row.innerHTML = `
        <td>
          $${valor.valorCuota?.toFixed(2) || '0.00'}
          ${isVigente ? '<span class="badge bg-primary ms-2">Vigente</span>' : ''}
        </td>
        <td>${valor.fechaDesde || '—'}</td>
        <td>${valor.fechaHasta || 'Abierta'}</td>
        <td>
          <span class="badge ${getEstadoBadgeClass(valor.eliminado)}">
            ${valor.eliminado ? 'Inactivo' : 'Activo'}
          </span>
        </td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${valor.id}">
            <i class="fa fa-edit"></i>
          </button>
          <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${valor.id}">
            <i class="fa fa-trash"></i>
          </button>
        </td>
      `;
      
      if(isVigente) {
        row.classList.add('table-primary');
      }
      
      dom.tableBody.appendChild(row);
    });
  }

  function onTableClick(e){
    const action = e.target.closest('[data-action]')?.dataset.action;
    const id = e.target.closest('[data-action]')?.dataset.id;
    
    if(!action || !id) return;
    
    if(action === 'edit') {
      editarValor(id);
    } else if(action === 'delete') {
      eliminarValor(id);
    }
  }

  function getEstadoBadgeClass(eliminado) {
    return eliminado ? 'bg-secondary' : 'bg-success';
  }

  // Initialize with current date
  document.addEventListener('DOMContentLoaded', () => {
    const today = new Date().toISOString().split('T')[0];
    if(dom.fechaDesdeInput && !dom.fechaDesdeInput.value) {
      dom.fechaDesdeInput.value = today;
    }
  });

})();