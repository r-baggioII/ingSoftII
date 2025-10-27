// Admin Facturas: crear y buscar
(function(){
  const fmt = new Intl.NumberFormat('es-AR', { style: 'currency', currency: 'ARS' });
  const $ = (sel) => document.querySelector(sel);
  const $$ = (sel) => Array.from(document.querySelectorAll(sel));

  let socioSeleccionado = null; // { id, nombreCompleto, numeroDocumento }
  let cuotas = []; // cuotas del socio
  let seleccionadas = new Set();

  document.addEventListener('DOMContentLoaded', () => {
  cargarFormasDePago();
    bindBuscadorSocio();
    bindCrearFactura();
    bindBusquedaFacturas();
    buscarFacturas();
  });

  function bindBusquedaFacturas(){
    const btn = $('#btn-buscar');
    if (btn) btn.addEventListener('click', buscarFacturas);
  }

  async function buscarFacturas() {
    const estadoEl = document.getElementById('filtro-estado');
    const numeroEl = document.getElementById('filtro-numero');
    const estado = estadoEl ? (estadoEl.value || '') : '';
    const numero = numeroEl ? (numeroEl.value || '') : '';
    const params = new URLSearchParams();
    if (estado) params.append('estado', estado);
    if (numero) params.append('numero', numero);
    params.append('page', 0);
    params.append('size', 20);

    const resp = await fetch(`/api/facturas2?${params.toString()}`);
    if (!resp.ok) {
      console.error('Error buscando facturas');
      return;
    }
    const data = await resp.json();
    renderTablaFacturas(data.content || []);
  }

  function renderTablaFacturas(items, error){
    const tbody = $('#tabla-facturas');
    if (!tbody) return;
    tbody.innerHTML='';
    if (error){
      tbody.innerHTML = `<tr><td colspan="6" class="text-danger text-center">${error}</td></tr>`;
      return;
    }
    const content = Array.isArray(items) ? items : (items?.content || []);
    if (!content.length){
      tbody.innerHTML = '<tr><td colspan="6" class="text-muted text-center">Sin resultados</td></tr>';
      return;
    }
    for (const f of content){
      const tr = document.createElement('tr');
      const fecha = f.fechaFactura ? new Date(f.fechaFactura).toLocaleDateString('es-AR') : '-';
      const total = typeof f.totalPagado === 'number' ? fmt.format(f.totalPagado) : '$0,00';
      const medio = f.formaDePago?.tipoPago || '-';
      tr.innerHTML = `
        <td>${f.numeroFactura ?? '-'}</td>
        <td>${fecha}</td>
        <td><span class="badge ${badgeForEstado(f.estado)}">${f.estado}</span></td>
        <td>${medio}</td>
        <td class="text-end">${total}</td>
        <td class="text-end"><a class="btn btn-sm btn-outline-secondary" href="/facturas/${f.id}/ver">Ver</a></td>
      `;
      tbody.appendChild(tr);
    }
  }

  function badgeForEstado(estado){
    switch(String(estado||'').toUpperCase()){
      case 'PAGADA': return 'badge-success';
      case 'ANULADA': return 'badge-danger';
      default: return 'badge-warning text-dark';
    }
  }

  function bindCrearFactura(){
    const btn = $('#btn-crear-factura');
    if (!btn) return;
    btn.addEventListener('click', async () => {
      const formaPagoTipo = $('#forma-pago')?.value;
      const fecha = $('#fecha-factura')?.value;
      const ids = Array.from(seleccionadas);
      if (!socioSeleccionado) return alerta('Seleccione un socio', 'warning');
      if (!formaPagoTipo) return alerta('Seleccione una forma de pago', 'warning');
      if (!fecha) return alerta('Seleccione una fecha', 'warning');
      if (!ids.length) return alerta('Seleccione al menos una cuota', 'warning');

      const total = cuotas.filter(c=>seleccionadas.has(c.id))
        .map(c=>c.valorCuota?.valorCuota||0).reduce((a,b)=>a+b,0);

      // Resolver/crear la FormaDePago por enum seleccionado
      let formaPagoId;
      try {
        formaPagoId = await ensureFormaDePagoId(formaPagoTipo);
      } catch (e) {
        return alerta(typeof e === 'string' ? e : 'No se pudo resolver la forma de pago', 'danger');
      }

      const payload = {
        fechaFactura: fecha,
        totalPagado: total,
        estado: 'PAGADA',
        formaDePago: { id: formaPagoId },
        detalles: ids.map(id => ({ cuotaMensual: { id } }))
      };

      fetch('/api/facturas',{
        method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(payload)
      }).then(r=>r.ok?r.json():r.text().then(t=>Promise.reject(t)))
        .then(f=>{
          alerta('Factura creada correctamente', 'success');
          window.open(`/facturas/${f.id}/ver`, '_blank');
          buscarFacturas();
        })
        .catch(err=>alerta(err||'No se pudo crear la factura','danger'))
        .finally(()=>{
          $('#btn-crear-factura').disabled = false;
        });
    });
  }

  function cargarFormasDePago(){
    const sel = $('#forma-pago');
    if (!sel) return;
    sel.innerHTML = '';
    const tipos = ['EFECTIVO','TRANSFERENCIA','BILLETERA_VIRTUAL'];
    for (const t of tipos){
      const opt = document.createElement('option');
      opt.value = t; opt.textContent = t;
      sel.appendChild(opt);
    }
  }

  async function ensureFormaDePagoId(tipo){
    // Busca una FormaDePago existente por tipo; si no existe la crea y retorna su id
    const resp = await fetch('/api/formas-pago');
    if (!resp.ok) throw 'No se pudo obtener formas de pago';
    const items = await resp.json();
    const existente = (items||[]).find(f => String(f.tipoPago).toUpperCase() === String(tipo).toUpperCase());
    if (existente && existente.id) return existente.id;
    const crear = await fetch('/api/formas-pago', {
      method:'POST', headers:{'Content-Type':'application/json'},
      body: JSON.stringify({ tipoPago: tipo, observacion: '' })
    });
    if (!crear.ok) {
      const msg = await crear.text();
      throw (msg || 'No se pudo crear forma de pago');
    }
    const creada = await crear.json();
    return creada.id;
  }

  function bindBuscadorSocio(){
    const input = $('#socio-buscar');
    if (!input) return;
    let debounce;
    input.addEventListener('input', () => {
      clearTimeout(debounce);
      const q = input.value.trim();
      if (!q){ $('#socio-sugerencias').innerHTML=''; return; }
      debounce = setTimeout(()=>buscarSocios(q), 250);
    });
  }

  function buscarSocios(q){
    // Simple filtro cliente con el endpoint existente de socios
    fetch('/api/v1/socios/activos')
      .then(r=>r.ok?r.json():[])
      .then(items=>{
        const lista = $('#socio-sugerencias');
        lista.innerHTML='';
        const lower = q.toLowerCase();
        const filtrados = items.filter(s=>
          (s.nombre && s.nombre.toLowerCase().includes(lower)) ||
          (s.apellido && s.apellido.toLowerCase().includes(lower)) ||
          (s.numeroDocumento && String(s.numeroDocumento).includes(lower))
        ).slice(0,20);
        for (const s of filtrados){
          const a = document.createElement('a');
          a.href = '#'; a.className = 'list-group-item list-group-item-action';
          const label = `${s.apellido||''} ${s.nombre||''} - DNI ${s.numeroDocumento||''}`.trim();
          a.textContent = label;
          a.addEventListener('click', ev => { ev.preventDefault(); seleccionarSocio(s); });
          lista.appendChild(a);
        }
      });
  }

  function seleccionarSocio(s){
    socioSeleccionado = { id: s.id, nombreCompleto: `${s.apellido||''} ${s.nombre||''}`.trim(), numeroDocumento: s.numeroDocumento };
    $('#socio-buscar').value = `${socioSeleccionado.nombreCompleto} - DNI ${socioSeleccionado.numeroDocumento}`;
    $('#socio-sugerencias').innerHTML='';
    cargarCuotasDeSocio(s.id);
  }

  function cargarCuotasDeSocio(idSocio){
    fetch(`/api/cuotas-mensuales/deuda-por-socio/${idSocio}`)
      .then(r=>r.ok?r.json():[])
      .then(data=>{
        cuotas = Array.isArray(data)?data:[];
        seleccionadas.clear();
        renderCuotas();
      });
  }

  function renderCuotas(){
    const body = $('#cuotas-body');
    body.innerHTML='';
    if (!cuotas.length){
      body.innerHTML = '<tr><td colspan="4" class="text-success text-center">No registra deuda</td></tr>';
      $('#btn-crear-factura').disabled = true;
      $('#total-seleccionado').textContent = fmt.format(0);
      return;
    }
    for (const c of cuotas){
      const tr = document.createElement('tr');
      const monto = c.valorCuota?.valorCuota || 0;
      const fecha = c.fechaVencimiento ? new Date(c.fechaVencimiento).toLocaleDateString('es-AR') : '-';
      const checked = seleccionadas.has(c.id) ? 'checked' : '';
      tr.innerHTML = `
        <td class="text-center"><input type="checkbox" class="chk-cuota" data-id="${c.id}" ${checked}></td>
        <td>${c.mes} ${c.anio}</td>
        <td>${fecha}</td>
        <td class="text-end">${fmt.format(monto)}</td>`;
      body.appendChild(tr);
    }
    body.querySelectorAll('.chk-cuota').forEach(chk => {
      chk.addEventListener('change', ev => {
        const id = ev.target.dataset.id;
        if (ev.target.checked) seleccionadas.add(id); else seleccionadas.delete(id);
        actualizarTotal();
      });
    });
    actualizarTotal();
  }

  function actualizarTotal(){
    const total = cuotas.filter(c=>seleccionadas.has(c.id)).map(c=>c.valorCuota?.valorCuota||0).reduce((a,b)=>a+b,0);
    $('#total-seleccionado').textContent = fmt.format(total);
    $('#btn-crear-factura').disabled = seleccionadas.size === 0;
  }

  function alerta(msg, tipo='info'){
    const el = $('#crear-alert');
    if (!el) return;
    el.className = `alert alert-${tipo}`;
    el.textContent = msg;
    el.classList.remove('d-none');
    setTimeout(()=> el.classList.add('d-none'), 3500);
  }
})();
