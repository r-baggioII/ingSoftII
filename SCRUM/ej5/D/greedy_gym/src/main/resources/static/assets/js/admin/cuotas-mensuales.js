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

  const MONTH_OPTIONS = [
    { value: 'ENERO', label: 'Enero' },
    { value: 'FEBRERO', label: 'Febrero' },
    { value: 'MARZO', label: 'Marzo' },
    { value: 'ABRIL', label: 'Abril' },
    { value: 'MAYO', label: 'Mayo' },
    { value: 'JUNIO', label: 'Junio' },
    { value: 'JULIO', label: 'Julio' },
    { value: 'AGOSTO', label: 'Agosto' },
    { value: 'SEPTIEMBRE', label: 'Septiembre' },
    { value: 'OCTUBRE', label: 'Octubre' },
    { value: 'NOVIEMBRE', label: 'Noviembre' },
    { value: 'DICIEMBRE', label: 'Diciembre' }
  ];

  const dom = {
    form: document.getElementById('cuota-form'),
    resetBtn: document.getElementById('cuota-reset'),
    tableBody: document.getElementById('cuota-table-body'),
    filtroForm: document.getElementById('filtro-cuotas'),
    socioSelect: document.querySelector("select[name='idSocio']"),
    anioInput: document.querySelector("input[name='anio']"),
    mesSelect: document.querySelector("select[name='mes']"),
    estadoSelect: document.querySelector("select[name='estado']"),
    btnBuscar: document.getElementById('btn-buscar'),
    btnCrear: document.getElementById('btn-crear'),
    deudaTotal: document.getElementById('deuda-total'),
    
    // Form fields
    dniInput: document.querySelector("input[name='numeroDocumento']"),
    formMesSelect: document.querySelector("select[name='mesForm']"),
    formAnioInput: document.querySelector("input[name='anioForm']"),
    valorCuotaSelect: document.querySelector("select[name='valorCuotaId']"),
    formEstadoSelect: document.querySelector("select[name='estadoForm']")
  };

  const state = { 
    items: [], 
    editingId: null,
    filtros: { socio: '', anio: '', mes: '', estado: '' }
  };

  document.addEventListener('DOMContentLoaded', () => {
    attach();
    cargarCombos();
    listar();
  });

  function attach(){
    dom.form?.addEventListener('submit', onSubmit);
    dom.resetBtn?.addEventListener('click', resetForm);
    dom.tableBody?.addEventListener('click', onTableClick);
    dom.filtroForm?.addEventListener('submit', onFiltrar);
    dom.btnBuscar?.addEventListener('click', onFiltrar);
    dom.socioSelect?.addEventListener('change', onSocioChange);
  }

  async function cargarCombos(){
    await Promise.all([cargarSocios(), cargarValoresCuota(), initializeSelects()]);
  }

  async function cargarSocios(){
    try {
      const data = await requestJson(buildUrl('/api/usuarios'))||[];
      const socios = data.filter(u => u.rol === 'SOCIO' && !u.eliminado);
      
      // Fill filter select
      if(dom.socioSelect) {
        fillSelect(dom.socioSelect, socios.map(s=>({
          value: s.id, 
          label: `${s.socio?.numeroDocumento || s.nombreUsuario} - ${s.socio?.nombre || ''} ${s.socio?.apellido || ''}`.trim()
        })), 'Todos los socios');
      }
    } catch(e) {
      console.error('Error cargando socios:', e);
    }
  }

  async function cargarValoresCuota(){
    if(!dom.valorCuotaSelect) return;
    try {
      const data = await requestJson(buildUrl('/api/valor-cuotas/activos'))||[];
      fillSelect(dom.valorCuotaSelect, data.map(v=>({
        value: v.id, 
        label: `$${v.valorCuota} (desde ${v.fechaDesde})`
      })), 'Seleccionar valor...');
    } catch(e) {
      console.error('Error cargando valores de cuota:', e);
    }
  }

  function initializeSelects(){
    // Initialize month selects
    if(dom.mesSelect) {
      fillSelect(dom.mesSelect, MONTH_OPTIONS, 'Todos los meses');
    }
    if(dom.formMesSelect) {
      fillSelect(dom.formMesSelect, MONTH_OPTIONS, 'Seleccionar mes...');
    }

    // Initialize estado selects
    const estadoOptions = [
      { value: 'PENDIENTE', label: 'Pendiente' },
      { value: 'PAGADA', label: 'Pagada' },
      { value: 'VENCIDA', label: 'Vencida' },
      { value: 'CANCELADA', label: 'Cancelada' }
    ];
    if(dom.estadoSelect) {
      fillSelect(dom.estadoSelect, estadoOptions, 'Todos los estados');
    }
    if(dom.formEstadoSelect) {
      fillSelect(dom.formEstadoSelect, estadoOptions, 'Seleccionar estado...');
    }

    // Set current year
    const currentYear = new Date().getFullYear();
    if(dom.formAnioInput) {
      dom.formAnioInput.value = currentYear;
    }
  }

  function fillSelect(select, options, placeholder) {
    if(!select) return;
    select.innerHTML = `<option value="">${placeholder}</option>`;
    options.forEach(opt => {
      const option = document.createElement('option');
      option.value = opt.value;
      option.textContent = opt.label;
      select.appendChild(option);
    });
  }

  async function onSubmit(e){
    e.preventDefault();
    const formData = new FormData(dom.form);
    
    try {
      if(state.editingId) {
        await actualizarCuota(state.editingId, formData);
      } else {
        await crearCuota(formData);
      }
      resetForm();
      await listar();
    } catch(error) {
      alert('Error: ' + error.message);
    }
  }

  async function crearCuota(formData) {
    const params = new URLSearchParams();
    params.set('numeroDocumento', formData.get('numeroDocumento') || '');
    params.set('mes', formData.get('mesForm') || '');
    params.set('anio', formData.get('anioForm') || '');
    params.set('idValorCuota', formData.get('valorCuotaId') || '');
    
    const url = `${buildUrl('/api/cuotas-mensuales')}?${params.toString()}`;
    await requestJson(url, { method: 'POST' });
  }

  async function actualizarCuota(id, formData) {
    const cuota = state.items.find(c => c.id === id);
    if(!cuota) throw new Error('Cuota no encontrada');

    const params = new URLSearchParams();
    params.set('idSocio', cuota.idSocio || '');
    params.set('mes', cuota.mes || '');
    params.set('anio', cuota.anio || '');
    params.set('idValorCuota', cuota.valorCuota?.id || '');
    params.set('estado', formData.get('estadoForm') || '');
    
    const url = `${buildUrl('/api/cuotas-mensuales/' + id)}?${params.toString()}`;
    await requestJson(url, { method: 'PUT' });
  }

  async function eliminarCuota(id) {
    if(!confirm('¿Está seguro de eliminar esta cuota?')) return;
    await requestJson(buildUrl('/api/cuotas-mensuales/' + id), { method: 'DELETE' });
    await listar();
  }

  function resetForm(){
    state.editingId = null;
    dom.form?.reset();
    const currentYear = new Date().getFullYear();
    if(dom.formAnioInput) {
      dom.formAnioInput.value = currentYear;
    }
    if(dom.btnCrear) {
      dom.btnCrear.textContent = 'Crear Cuota';
    }
    // Re-enable all fields that may have been disabled during edit
    ['numeroDocumento', 'mesForm', 'anioForm', 'valorCuotaId'].forEach(name => {
      const field = document.querySelector(`[name="${name}"]`);
      if(field) field.disabled = false;
    });
  }

  function prepareCreate(){
    resetForm();
  }

  function editarCuota(id){
    const cuota = state.items.find(c => c.id === id);
    if(!cuota) return;

    state.editingId = id;
    
    // Only allow editing estado
    if(dom.formEstadoSelect) {
      dom.formEstadoSelect.value = cuota.estado || '';
    }
    
    if(dom.btnCrear) {
      dom.btnCrear.textContent = 'Actualizar Estado';
    }
    
    // Disable other fields during edit
    ['numeroDocumento', 'mesForm', 'anioForm', 'valorCuotaId'].forEach(name => {
      const field = document.querySelector(`[name="${name}"]`);
      if(field) field.disabled = true;
    });
  }

  async function onFiltrar(e){
    if(e) e.preventDefault();
    await listar();
  }

  async function onSocioChange(){
    const socioId = dom.socioSelect?.value;
    if(socioId) {
      await calcularDeuda(socioId);
    } else {
      if(dom.deudaTotal) dom.deudaTotal.textContent = '';
    }
    // Refresh the list with current filters
    await listar();
  }

  async function calcularDeuda(socioId) {
    try {
      // Get debt for this member using the new endpoint
      const cuotasAdeudadas = await requestJson(buildUrl(`/api/cuotas-mensuales/deuda-por-socio/${socioId}`));
      
      const totalDeuda = cuotasAdeudadas.reduce((sum, cuota) => 
        sum + (cuota.valorCuota?.valorCuota || 0), 0
      );
      
      if(dom.deudaTotal) {
        dom.deudaTotal.textContent = cuotasAdeudadas.length > 0 
          ? `Deuda total: $${totalDeuda.toFixed(2)} (${cuotasAdeudadas.length} cuotas)`
          : 'Sin deudas pendientes';
      }
    } catch(e) {
      console.error('Error calculando deuda:', e);
      if(dom.deudaTotal) {
        dom.deudaTotal.textContent = 'Error calculando deuda';
      }
    }
  }

  async function listar(){
    try {
      const data = await requestJson(buildUrl('/api/cuotas-mensuales'))||[];
      state.items = data;
      
      // Apply filters
      let filtered = data;
      
      const socioFiltro = dom.socioSelect?.value;
      const anioFiltro = dom.anioInput?.value;
      const mesFiltro = dom.mesSelect?.value;
      const estadoFiltro = dom.estadoSelect?.value;
      
      if(socioFiltro) {
        filtered = filtered.filter(c => c.idSocio === socioFiltro);
      }
      if(anioFiltro) {
        filtered = filtered.filter(c => String(c.anio) === anioFiltro);
      }
      if(mesFiltro) {
        filtered = filtered.filter(c => c.mes === mesFiltro);
      }
      if(estadoFiltro) {
        filtered = filtered.filter(c => c.estado === estadoFiltro);
      }
      
      render(filtered);
    } catch(error) {
      console.error('Error listando cuotas:', error);
    }
  }

  function render(items = state.items){
    if(!dom.tableBody) return;
    
    dom.tableBody.innerHTML = '';
    
    if(items.length === 0) {
      dom.tableBody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No hay cuotas registradas</td></tr>';
      return;
    }
    
    items.forEach(cuota => {
      const row = document.createElement('tr');
      row.innerHTML = `
        <td>${cuota.socioNumeroDocumento || '—'}</td>
        <td>${formatMonth(cuota.mes) || '—'}</td>
        <td>${cuota.anio || '—'}</td>
        <td><span class="badge ${getEstadoBadgeClass(cuota.estado)}">${formatEstado(cuota.estado)}</span></td>
        <td>$${cuota.valorCuota?.valorCuota?.toFixed(2) || '0.00'}</td>
        <td>${cuota.fechaVencimiento || '—'}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${cuota.id}">
            <i class="fa fa-edit"></i>
          </button>
          <button class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${cuota.id}">
            <i class="fa fa-trash"></i>
          </button>
        </td>
      `;
      dom.tableBody.appendChild(row);
    });
  }

  function onTableClick(e){
    const action = e.target.closest('[data-action]')?.dataset.action;
    const id = e.target.closest('[data-action]')?.dataset.id;
    
    if(!action || !id) return;
    
    if(action === 'edit') {
      editarCuota(id);
    } else if(action === 'delete') {
      eliminarCuota(id);
    }
  }

  function formatMonth(mes) {
    const month = MONTH_OPTIONS.find(m => m.value === mes);
    return month ? month.label : mes;
  }

  function getEstadoBadgeClass(estado) {
    switch(estado) {
      case 'PENDIENTE': return 'bg-warning text-dark';
      case 'PAGADA': return 'bg-success';
      case 'VENCIDA': return 'bg-danger';
      case 'CANCELADA': return 'bg-secondary';
      default: return 'bg-secondary';
    }
  }

  function formatEstado(estado) {
    switch(estado) {
      case 'PENDIENTE': return 'Pendiente';
      case 'PAGADA': return 'Pagada';
      case 'VENCIDA': return 'Vencida';
      case 'CANCELADA': return 'Cancelada';
      default: return estado;
    }
  }

})();