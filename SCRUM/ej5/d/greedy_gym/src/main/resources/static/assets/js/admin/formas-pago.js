(function(){
  const $ = (sel) => document.querySelector(sel);
  const $$ = (sel) => Array.from(document.querySelectorAll(sel));

  document.addEventListener('DOMContentLoaded', () => {
    bindCrear();
    cargarLista();
  });

  function bindCrear(){
    const btn = $('#fp-crear');
    if (!btn) return;
    btn.addEventListener('click', () => {
      const tipo = $('#fp-tipo')?.value;
      const observacion = $('#fp-observacion')?.value || '';
      if (!tipo) return alertar('El tipo es obligatorio', 'warning');
      fetch('/greedy_gym/api/formas-pago',{
        method:'POST', headers:{'Content-Type':'application/json'},
        body: JSON.stringify({ tipoPago: tipo, observacion })
      }).then(r=>r.ok?r.json():r.text().then(t=>Promise.reject(t)))
        .then(()=>{
          alertar('Forma de pago creada', 'success');
          $('#fp-observacion').value = '';
          cargarLista();
        })
        .catch(err=>alertar(err||'No se pudo crear','danger'));
    });
  }

  function cargarLista(){
    fetch('/greedy_gym/api/formas-pago')
      .then(r=>r.ok?r.json():[])
      .then(items=>renderTabla(items||[]));
  }

  function renderTabla(items){
    const tbody = $('#fp-tabla');
    if (!tbody) return;
    tbody.innerHTML = '';
    if (!items.length){
      tbody.innerHTML = '<tr><td colspan="3" class="text-muted text-center">Sin datos</td></tr>';
      return;
    }
    for (const f of items){
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${f.tipoPago}</td>
        <td>${f.observacion||''}</td>
        <td class="text-end">
          <button class="btn btn-sm btn-outline-danger" data-id="${f.id}">Eliminar</button>
        </td>`;
      tbody.appendChild(tr);
    }
    tbody.querySelectorAll('button.btn-outline-danger').forEach(btn => {
      btn.addEventListener('click', () => eliminar(btn.dataset.id));
    });
  }

  function eliminar(id){
    if (!id) return;
    if (!confirm('¿Eliminar forma de pago?')) return;
    fetch(`/greedy_gym/api/formas-pago/${id}`, { method: 'DELETE' })
      .then(r=> r.ok ? null : r.text().then(t=>Promise.reject(t)))
      .then(()=>{ alertar('Eliminada', 'success'); cargarLista(); })
      .catch(err=>alertar(err||'No se pudo eliminar', 'danger'));
  }

  function alertar(msg, tipo='info'){
    const el = $('#fp-alert');
    if (!el) return;
    el.className = `alert alert-${tipo}`;
    el.textContent = msg;
    el.classList.remove('d-none');
    setTimeout(()=> el.classList.add('d-none'), 3000);
  }
})();
