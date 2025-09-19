(function () {
  console.log('[Panel entidades] Script loading...');
  const $ = window.jQuery;
  const rawContextPath = document.body ? (document.body.dataset.contextPath || '') : '';
  const contextPath = normaliseContextPath(rawContextPath);
  console.log('[Panel entidades] Context path:', contextPath);

  const dom = {
    switcher: document.getElementById('entity-switcher'),
    search: document.getElementById('entity-search'),
    status: document.getElementById('entity-status'),
    create: document.getElementById('entity-create'),
    createLabel: document.getElementById('entity-create-label'),
    tableHead: document.querySelector('#entity-table thead'),
    tableBody: document.querySelector('#entity-table tbody'),
    empty: document.getElementById('entity-empty'),
    emptyCreate: document.getElementById('entity-empty-create'),
    counter: document.getElementById('entity-counter'),
    alert: document.getElementById('panel-alert'),
    title: document.getElementById('entity-title'),
    description: document.getElementById('entity-description'),
    modal: document.getElementById('entityModal'),
    form: document.getElementById('entity-form'),
    formFields: document.getElementById('entity-form-fields'),
    formAlert: document.getElementById('entity-form-alert'),
    formSubmit: document.getElementById('entity-form-submit'),
    modalTitle: document.getElementById('entity-modal-title')
  };

  const optionCache = {};

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

  const CUOTA_STATE_OPTIONS = [
    { value: 'PENDIENTE', label: 'Pendiente' },
    { value: 'PAGADA', label: 'Pagada' },
    { value: 'VENCIDA', label: 'Vencida' },
    { value: 'CANCELADA', label: 'Cancelada' }
  ];

  const DOCUMENT_OPTIONS = ['DNI', 'PASAPORTE', 'CEDULA', 'LIBRETA_CIVICA', 'LIBRETA_ENROLAMIENTO']
    .map(value => ({ value, label: formatLabel(value) }));

  const EMPLOYEE_TYPE_OPTIONS = ['ADMINISTRATIVO', 'ENTRENADOR', 'RECEPCION', 'SERVICIOS_GENERALES']
    .map(value => ({ value, label: formatLabel(value) }));

  const state = {
    entity: 'empresas',
    records: [],
    search: '',
    status: 'activos',
    loading: false,
    formMode: null,
    editing: null
  };

  const ENTITIES = {
    empresas: {
      label: 'Empresas',
      description: 'Gestión de empresas propietarias de gimnasios.',
      singular: 'empresa',
      createLabel: 'Nueva empresa',
      searchPlaceholder: 'Buscar por nombre, teléfono o correo electrónico',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Nombre', value: item => item?.nombre || '—' },
        { header: 'Teléfono', value: item => item?.telefono || '—' },
        { header: 'Correo', value: item => item?.correoElectronico || '—' },
        { header: 'Estado', value: item => item?.eliminado ? 'Dada de baja' : 'Activa' }
      ],
      formFields: [
        { name: 'nombre', label: 'Nombre comercial', type: 'text', required: true, fullWidth: true },
        { name: 'telefono', label: 'Teléfono', type: 'text', required: true, editDisabled: true },
        { name: 'correoElectronico', label: 'Correo electrónico', type: 'email', required: true, editDisabled: true }
      ],
      async list() {
        return requestJson(buildUrl('/api/empresas'));
      },
      async create(payload) {
        invalidateOptions('empresas');
        return sendJson(buildUrl('/api/empresas'), 'POST', payload);
      },
      async update(item, payload) {
        const params = new URLSearchParams();
        params.set('nombre', payload.nombre || '');
        invalidateOptions('empresas');
        return requestJson(`${buildUrl('/api/empresas/' + item.id)}?${params.toString()}`, { method: 'PUT' });
      },
      async remove(item) {
        invalidateOptions('empresas');
        return requestJson(buildUrl('/api/empresas/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        return {
          nombre: item?.nombre || '',
          telefono: item?.telefono || '',
          correoElectronico: item?.correoElectronico || ''
        };
      },
      toPayload(mode, values) {
        if (mode === 'create') {
          return {
            nombre: values.nombre.trim(),
            telefono: values.telefono.trim(),
            correoElectronico: values.correoElectronico.trim()
          };
        }
        return { nombre: values.nombre.trim() };
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [item?.nombre, item?.telefono, item?.correoElectronico]
          .filter(Boolean)
          .join(' ')
          .toLowerCase();
      }
    },
    sucursales: {
      label: 'Sucursales',
      description: 'Ubicaciones físicas asociadas a cada empresa.',
      singular: 'sucursal',
      createLabel: 'Nueva sucursal',
      searchPlaceholder: 'Buscar por nombre de sucursal, empresa o dirección',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Sucursal', value: item => item?.nombre || '—' },
        { header: 'Empresa', value: item => item?.empresa?.nombre || '—' },
        { header: 'Dirección', value: item => formatAddress(item?.direccion) || '—' },
        { header: 'Estado', value: item => item?.eliminado ? 'Dada de baja' : 'Activa' }
      ],
      formFields: [
        { name: 'nombre', label: 'Nombre', type: 'text', required: true },
        { name: 'empresaId', label: 'Empresa', type: 'select', required: true, loadOptions: loadEmpresasOptions },
        { name: 'calle', label: 'Calle', type: 'text', required: true },
        { name: 'numero', label: 'Altura', type: 'text', required: true },
        { name: 'ciudad', label: 'Ciudad', type: 'text', required: true },
        { name: 'provincia', label: 'Provincia', type: 'text', required: true },
        { name: 'pais', label: 'País', type: 'text', required: true },
        { name: 'codigoPostal', label: 'Código postal', type: 'text' }
      ],
      async list() {
        return requestJson(buildUrl('/api/v1/sucursales'));
      },
      async create(payload) {
        return sendJson(buildUrl('/api/v1/sucursales'), 'POST', payload);
      },
      async update(item, payload) {
        return sendJson(buildUrl('/api/v1/sucursales/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        return requestJson(buildUrl('/api/v1/sucursales/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        const direccion = item?.direccion || {};
        return {
          nombre: item?.nombre || '',
          empresaId: item?.empresa?.id || '',
          calle: direccion.calle || '',
          numero: direccion.numero || '',
          ciudad: direccion.ciudad || '',
          provincia: direccion.provincia || '',
          pais: direccion.pais || '',
          codigoPostal: direccion.codigoPostal || ''
        };
      },
      toPayload(mode, values, item) {
        return {
          nombre: values.nombre.trim(),
          idEmpresa: values.empresaId || item?.empresa?.id || '',
          direccion: {
            calle: values.calle.trim(),
            numero: values.numero.trim(),
            ciudad: values.ciudad.trim(),
            provincia: values.provincia.trim(),
            pais: values.pais.trim(),
            codigoPostal: values.codigoPostal ? values.codigoPostal.trim() : null
          }
        };
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [
          item?.nombre,
          item?.empresa?.nombre,
          formatAddress(item?.direccion),
          item?.id
        ].filter(Boolean).join(' ').toLowerCase();
      }
    },
    empleados: {
      label: 'Empleados',
      description: 'Personal de trabajo de las sucursales.',
      singular: 'empleado',
      createLabel: 'Nuevo empleado',
      searchPlaceholder: 'Buscar por nombre, apellido, DNI o correo electrónico',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Nombre completo', value: item => `${(item?.nombre || '').trim()} ${(item?.apellido || '').trim()}`.trim() || '—' },
        { header: 'Documento', value: item => `${item?.tipoDocumento || ''} ${item?.numeroDocumento || ''}`.trim() || '—' },
        { header: 'Tipo', value: item => formatLabel(item?.tipoEmpleado) || '—' },
        { header: 'Correo', value: item => item?.correoElectronico || '—' },
        { header: 'Estado', value: item => item?.eliminado ? 'Dado de baja' : 'Activo' }
      ],
      formFields: [
        { name: 'nombre', label: 'Nombre', type: 'text', required: true },
        { name: 'apellido', label: 'Apellido', type: 'text', required: true },
        { name: 'fechaNacimiento', label: 'Fecha de nacimiento', type: 'date', required: true },
        { name: 'tipoDocumento', label: 'Tipo de documento', type: 'select', required: true, options: DOCUMENT_OPTIONS },
        { name: 'numeroDocumento', label: 'Número de documento', type: 'text', required: true },
        { name: 'telefono', label: 'Teléfono', type: 'text', required: true },
        { name: 'correoElectronico', label: 'Correo electrónico', type: 'email', required: true },
        { name: 'tipoEmpleado', label: 'Tipo de empleado', type: 'select', required: true, options: EMPLOYEE_TYPE_OPTIONS }
      ],
      async list() {
        return requestJson(buildUrl('/api/v1/empleados'));
      },
      async create(payload) {
        return sendJson(buildUrl('/api/v1/empleados'), 'POST', payload);
      },
      async update(item, payload) {
        return sendJson(buildUrl('/api/v1/empleados/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        return requestJson(buildUrl('/api/v1/empleados/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        return {
          nombre: item?.nombre || '',
          apellido: item?.apellido || '',
          fechaNacimiento: item?.fechaNacimiento || '',
          tipoDocumento: item?.tipoDocumento || DOCUMENT_OPTIONS[0].value,
          numeroDocumento: item?.numeroDocumento || '',
          telefono: item?.telefono || '',
          correoElectronico: item?.correoElectronico || '',
          tipoEmpleado: item?.tipoEmpleado || EMPLOYEE_TYPE_OPTIONS[0].value
        };
      },
      toPayload(mode, values) {
        return {
          nombre: values.nombre.trim(),
          apellido: values.apellido.trim(),
          fechaNacimiento: values.fechaNacimiento,
          tipoDocumento: values.tipoDocumento,
          numeroDocumento: values.numeroDocumento.trim(),
          telefono: values.telefono.trim(),
          correoElectronico: values.correoElectronico.trim(),
          tipoEmpleado: values.tipoEmpleado
        };
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [
          item?.nombre,
          item?.apellido,
          item?.correoElectronico,
          item?.telefono,
          item?.numeroDocumento,
          formatLabel(item?.tipoEmpleado)
        ].filter(Boolean).join(' ').toLowerCase();
      }
    },
    cuotas: {
      label: 'Cuotas mensuales',
      description: 'Gestión de cuotas emitidas a cada socio.',
      singular: 'cuota',
      createLabel: 'Nueva cuota',
      searchPlaceholder: 'Buscar por socio, mes o estado de pago',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Socio', value: item => item?.idSocio || '—' },
        { header: 'Mes', value: item => formatMonth(item?.mes) || '—' },
        { header: 'Año', value: item => item?.anio != null ? String(item.anio) : '—' },
        { header: 'Estado', value: item => formatLabel(item?.estado) || '—' },
        { header: 'Monto', value: item => formatMoney(item?.valorCuota?.valorCuota) },
        { header: 'Vencimiento', value: item => item?.fechaVencimiento || '—' }
      ],
      formFields: [
        { name: 'idSocio', label: 'ID de socio', type: 'text', required: true, modes: ['create'] },
        { name: 'mes', label: 'Mes', type: 'select', required: true, options: MONTH_OPTIONS, modes: ['create'] },
        { name: 'anio', label: 'Año', type: 'number', required: true, min: 2000, modes: ['create'] },
        { name: 'fechaVencimiento', label: 'Fecha de vencimiento', type: 'date', required: true },
        { name: 'valorCuotaId', label: 'Valor de cuota', type: 'select', required: true, modes: ['create'], loadOptions: loadValorCuotasOptions },
        { name: 'estado', label: 'Estado', type: 'select', required: true, options: CUOTA_STATE_OPTIONS, modes: ['edit'] }
      ],
      async list() {
        return requestJson(buildUrl('/api/v1/cuotas'));
      },
      async create(payload) {
        return sendJson(buildUrl('/api/v1/cuotas'), 'POST', payload);
      },
      async update(item, payload) {
        return sendJson(buildUrl('/api/v1/cuotas/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        return requestJson(buildUrl('/api/v1/cuotas/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item, mode) {
        if (mode === 'create') {
          const year = new Date().getFullYear();
          return {
            idSocio: '',
            mes: MONTH_OPTIONS[0].value,
            anio: year,
            fechaVencimiento: '',
            valorCuotaId: '',
            estado: 'PENDIENTE'
          };
        }
        return {
          idSocio: item?.idSocio || '',
          mes: item?.mes || MONTH_OPTIONS[0].value,
          anio: item?.anio != null ? item.anio : new Date().getFullYear(),
          fechaVencimiento: item?.fechaVencimiento || '',
          valorCuotaId: item?.valorCuota?.id || '',
          estado: item?.estado || 'PENDIENTE'
        };
      },
      toPayload(mode, values, item) {
        if (mode === 'create') {
          return {
            idSocio: values.idSocio.trim(),
            mes: values.mes,
            anio: Number(values.anio),
            fechaVencimiento: values.fechaVencimiento,
            valorCuota: { id: values.valorCuotaId }
          };
        }
        const payload = {};
        if (values.estado) {
          payload.estado = values.estado;
        }
        if (values.fechaVencimiento) {
          payload.fechaVencimiento = values.fechaVencimiento;
        }
        return payload;
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [
          item?.idSocio,
          formatMonth(item?.mes),
          item?.anio,
          formatLabel(item?.estado),
          item?.valorCuota?.valorCuota,
          item?.fechaVencimiento
        ].filter(Boolean).join(' ').toLowerCase();
      }
    },
    valorCuotas: {
      label: 'Valores de cuota',
      description: 'Historial de montos vigentes para las cuotas.',
      singular: 'valor de cuota',
      createLabel: 'Nuevo valor de cuota',
      searchPlaceholder: 'Buscar por monto o fecha de vigencia',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Monto', value: item => formatMoney(item?.valorCuota) },
        { header: 'Vigencia desde', value: item => item?.fechaDesde || '—' },
        { header: 'Vigencia hasta', value: item => item?.fechaHasta || 'Abierta' },
        { header: 'Estado', value: item => item?.eliminado ? 'No vigente' : 'Activo' }
      ],
      formFields: [
        { name: 'fechaDesde', label: 'Vigencia desde', type: 'date', required: true },
        { name: 'fechaHasta', label: 'Vigencia hasta', type: 'date' },
        { name: 'valorCuota', label: 'Monto', type: 'number', step: '0.01', min: 0, required: true }
      ],
      async list() {
        return requestJson(buildUrl('/api/v1/valor-cuotas'));
      },
      async create(payload) {
        invalidateOptions('valorCuotas');
        return sendJson(buildUrl('/api/v1/valor-cuotas'), 'POST', payload);
      },
      async update(item, payload) {
        invalidateOptions('valorCuotas');
        return sendJson(buildUrl('/api/v1/valor-cuotas/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        invalidateOptions('valorCuotas');
        return requestJson(buildUrl('/api/v1/valor-cuotas/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        return {
          fechaDesde: item?.fechaDesde || '',
          fechaHasta: item?.fechaHasta || '',
          valorCuota: item?.valorCuota != null ? item.valorCuota : ''
        };
      },
      toPayload(mode, values) {
        const payload = {
          fechaDesde: values.fechaDesde || null,
          fechaHasta: values.fechaHasta || null,
          valorCuota: values.valorCuota !== '' ? Number(values.valorCuota) : null
        };
        if (payload.valorCuota == null || Number.isNaN(payload.valorCuota)) {
          throw new Error('Ingresá un monto válido.');
        }
        return payload;
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [
          item?.valorCuota,
          item?.fechaDesde,
          item?.fechaHasta,
          item?.id
        ].filter(Boolean).join(' ').toLowerCase();
      }
    },
    paises: {
      label: 'Países',
      description: 'Gestión de países para direcciones.',
      singular: 'país',
      createLabel: 'Nuevo país',
      searchPlaceholder: 'Buscar por nombre del país',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Nombre', value: item => item?.nombre || '—' },
        { header: 'Estado', value: item => item?.eliminado ? 'Dado de baja' : 'Activo' }
      ],
      formFields: [
        { name: 'nombre', label: 'Nombre del país', type: 'text', required: true, fullWidth: true }
      ],
      async list() {
        return requestJson(buildUrl('/api/paises'));
      },
      async create(payload) {
        invalidateOptions('paises');
        return sendJson(buildUrl('/api/paises'), 'POST', payload);
      },
      async update(item, payload) {
        const params = new URLSearchParams();
        params.set('nombre', payload.nombre || '');
        invalidateOptions('paises');
        return requestJson(`${buildUrl('/api/paises/' + item.id)}?${params.toString()}`, { method: 'PUT' });
      },
      async remove(item) {
        invalidateOptions('paises');
        return requestJson(buildUrl('/api/paises/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        return {
          nombre: item?.nombre || ''
        };
      },
      toPayload(mode, values) {
        return { nombre: values.nombre.trim() };
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [item?.nombre].filter(Boolean).join(' ').toLowerCase();
      }
    }
  };

  if (!dom.switcher || !dom.tableHead || !dom.tableBody) {
    console.warn('[Panel entidades] Elementos básicos no disponibles.', {
      switcher: !!dom.switcher,
      tableHead: !!dom.tableHead,
      tableBody: !!dom.tableBody
    });
    return;
  }

  console.log('[Panel entidades] DOM elements found, initializing...');
  initialise();

  function initialise() {
    dom.switcher.addEventListener('click', onEntitySwitch);
    dom.search.addEventListener('input', onSearchChange);
    dom.status.addEventListener('change', onStatusChange);
    dom.create.addEventListener('click', () => openForm('create'));
    dom.emptyCreate.addEventListener('click', () => openForm('create'));
    dom.form.addEventListener('submit', onFormSubmit);
    dom.tableBody.addEventListener('click', onTableClick);

    if ($ && dom.modal) {
      $(dom.modal).on('hidden.bs.modal', resetForm);
    }

    loadEntity('empresas');
  }

  function normaliseContextPath(path) {
    if (!path) {
      return '';
    }
    if (path === '/') {
      return '';
    }
    return path.endsWith('/') ? path.slice(0, -1) : path;
  }

  function buildUrl(path) {
    if (!path) {
      return contextPath || '';
    }
    const cleanPath = path.startsWith('/') ? path : '/' + path;
    return contextPath ? `${contextPath}${cleanPath}` : cleanPath;
  }

  function onEntitySwitch(event) {
    console.log('[Panel entidades] Entity switch clicked', event.target);
    const listItem = event.target.closest('li[data-entity]');
    if (!listItem) {
      console.log('[Panel entidades] No list item found');
      return;
    }
    const entity = listItem.dataset.entity;
    console.log('[Panel entidades] Entity selected:', entity);
    if (!entity || entity === state.entity) {
      console.log('[Panel entidades] Entity same as current or empty:', entity, state.entity);
      return;
    }
    loadEntity(entity);
  }

  function loadEntity(entityKey) {
    console.log('[Panel entidades] Loading entity:', entityKey);
    if (!ENTITIES[entityKey]) {
      console.error('[Panel entidades] Entity not found in ENTITIES:', entityKey, Object.keys(ENTITIES));
      return;
    }
    state.entity = entityKey;
    state.records = [];
    state.search = '';
    state.status = 'activos';
    dom.search.value = '';
    dom.status.value = 'activos';
    setActiveSwitcher();
    renderTable();
    loadRecords();
  }

  function setActiveSwitcher() {
    const listItems = dom.switcher.querySelectorAll('li[data-entity]');
    listItems.forEach(item => {
      const isActive = item.dataset.entity === state.entity;
      item.classList.toggle('active', isActive);
    });
  }

  async function loadRecords() {
    const config = ENTITIES[state.entity];
    if (!config) {
      return;
    }
    state.loading = true;
    renderTable();
    try {
      const response = await config.list();
      state.records = normalizeList(response);
      showMessage('', '');
    } catch (error) {
      state.records = [];
      showMessage('danger', error?.message || 'No pudimos cargar los datos.');
    } finally {
      state.loading = false;
      renderTable();
    }
  }

  function renderTable() {
    const config = ENTITIES[state.entity];
    if (!config) {
      return;
    }

    dom.title.textContent = config.label;
    dom.description.textContent = config.description || '';
    
    // Update search placeholder
    if (config.searchPlaceholder) {
      dom.search.placeholder = config.searchPlaceholder;
    }

    if (config.allowCreate) {
      dom.create.classList.remove('d-none');
      dom.create.disabled = false;
      dom.emptyCreate.classList.remove('d-none');
      dom.createLabel.textContent = config.createLabel || `Nuevo ${config.singular || 'registro'}`;
    } else {
      dom.create.classList.add('d-none');
      dom.emptyCreate.classList.add('d-none');
    }

    const hasActions = !!(config.allowUpdate || config.allowDelete);
    const headers = config.columns.map(col => `<th>${escapeHtml(col.header || '')}</th>`).join('');
    dom.tableHead.innerHTML = hasActions
      ? `<tr>${headers}<th class="text-end">Acciones</th></tr>`
      : `<tr>${headers}</tr>`;

    if (state.loading) {
      dom.tableBody.innerHTML = `<tr><td colspan="${config.columns.length + (hasActions ? 1 : 0)}" class="text-center text-muted py-4">Cargando información...</td></tr>`;
      dom.empty.classList.add('d-none');
      dom.counter.textContent = '';
      return;
    }

    const filtered = filterRecords(config);
    dom.counter.textContent = `${filtered.length} de ${state.records.length} ${config.label.toLowerCase()}`;

    if (filtered.length === 0) {
      dom.tableBody.innerHTML = `<tr><td colspan="${config.columns.length + (hasActions ? 1 : 0)}" class="text-center text-muted py-4">Sin registros para mostrar.</td></tr>`;
      dom.empty.classList.remove('d-none');
      return;
    }

    dom.empty.classList.add('d-none');

    const rows = filtered.map(item => renderRow(item, config, hasActions)).join('');
    dom.tableBody.innerHTML = rows;
  }

  function renderRow(item, config, hasActions) {
    const id = item?.id || item?.uuid || '';
    const cells = config.columns.map(col => {
      let value = '';
      try {
        value = col.value ? col.value(item) : '';
      } catch (e) {
        value = '';
      }
      return `<td>${escapeHtml(value != null ? String(value) : '—')}</td>`;
    }).join('');

    let actions = '';
    if (hasActions) {
      const buttons = [];
      if (config.allowUpdate) {
        buttons.push(`<button type="button" class="btn btn-sm btn-outline-primary" data-action="edit" data-id="${escapeAttr(id)}"><i class="fa fa-pencil"></i> Editar</button>`);
      }
      if (config.allowDelete) {
        buttons.push(`<button type="button" class="btn btn-sm btn-outline-danger" data-action="delete" data-id="${escapeAttr(id)}"><i class="fa fa-trash"></i> Borrar</button>`);
      }
      actions = `<td class="text-end action-buttons">${buttons.join(' ')}</td>`;
    }

    return `<tr data-id="${escapeAttr(id)}">${cells}${actions}</tr>`;
  }

  function filterRecords(config) {
    let items = Array.isArray(state.records) ? state.records.slice() : [];

    if (state.status !== 'todos') {
      items = items.filter(item => {
        const inactive = config.isInactive ? config.isInactive(item) : false;
        return state.status === 'inactivos' ? inactive : !inactive;
      });
    }

    if (state.search) {
      const finder = typeof config.searchText === 'function'
        ? config.searchText
        : defaultSearch(config.columns);
      items = items.filter(item => {
        try {
          const text = finder(item) || '';
          return text.toLowerCase().includes(state.search);
        } catch (e) {
          return false;
        }
      });
    }

    return items;
  }

  function defaultSearch(columns) {
    return function (item) {
      return columns.map(col => {
        try {
          const value = col.value ? col.value(item) : '';
          return value != null ? String(value) : '';
        } catch (e) {
          return '';
        }
      }).join(' ').toLowerCase();
    };
  }

  function onSearchChange(event) {
    state.search = event.target.value.trim().toLowerCase();
    renderTable();
  }

  function onStatusChange(event) {
    state.status = event.target.value;
    renderTable();
  }

  async function onTableClick(event) {
    const button = event.target.closest('button[data-action]');
    if (!button) {
      return;
    }
    const action = button.dataset.action;
    const id = button.dataset.id;
    const config = ENTITIES[state.entity];
    if (!config) {
      return;
    }
    const item = state.records.find(record => String(record?.id || record?.uuid || '') === String(id));
    if (!item) {
      showMessage('danger', 'No pudimos encontrar el registro seleccionado.');
      return;
    }

    if (action === 'edit' && config.allowUpdate) {
      openForm('edit', item);
      return;
    }

    if (action === 'delete' && config.allowDelete) {
      const confirmDelete = window.confirm(`¿Seguro que querés eliminar esta ${config.singular || 'entidad'}?`);
      if (!confirmDelete) {
        return;
      }
      try {
        await config.remove(item);
        showMessage('success', `${capitalize(config.singular || 'registro')} eliminado.`);
        await loadRecords();
      } catch (error) {
        showMessage('danger', error?.message || 'No pudimos eliminar el registro.');
      }
    }
  }

  function openForm(mode, item) {
    const config = ENTITIES[state.entity];
    if (!config) {
      return;
    }
    if (mode === 'create' && !config.allowCreate) {
      return;
    }
    if (mode === 'edit' && !config.allowUpdate) {
      return;
    }

    state.formMode = mode;
    state.editing = mode === 'edit' ? item : null;
    setFormError('');
    dom.formFields.innerHTML = '<div class="col-12"><p class="text-muted mb-0">Cargando formulario...</p></div>';
    dom.formSubmit.textContent = mode === 'create' ? 'Guardar' : 'Guardar cambios';
    dom.formSubmit.disabled = true;

    const singular = config.singular || 'registro';
    dom.modalTitle.textContent = mode === 'create'
      ? `Crear ${singular}`
      : `Editar ${singular}`;

    showModal();

    buildForm(config, mode, item).catch(error => {
      setFormError(error?.message || 'No pudimos preparar el formulario.');
      dom.formFields.innerHTML = '';
    });
  }

  async function buildForm(config, mode, item) {
    const fields = (config.formFields || []).filter(field => {
      if (!field.modes) {
        return true;
      }
      return field.modes.includes(mode);
    });

    const values = config.prepareFormValues ? config.prepareFormValues(item, mode) : {};

    const fieldBlocks = [];
    for (const field of fields) {
      let options = field.options;
      if (!options && typeof field.loadOptions === 'function') {
        options = await field.loadOptions();
        field.options = options;
      }
      fieldBlocks.push(renderField(field, values[field.name] ?? '', mode));
    }

    dom.formFields.innerHTML = fieldBlocks.join('');
    dom.formSubmit.disabled = false;
  }

  function renderField(field, value, mode) {
    const colClass = field.fullWidth ? 'col-12' : 'col-12 col-md-6';
    const label = `<label class="form-label">${escapeHtml(field.label || '')}</label>`;
    const requiredAttr = field.required ? 'required' : '';
    const disabledAttr = mode === 'edit' && field.editDisabled ? 'disabled' : '';
    const placeholder = field.placeholder ? ` placeholder="${escapeAttr(field.placeholder)}"` : '';
    const minAttr = field.min != null ? ` min="${escapeAttr(field.min)}"` : '';
    const maxAttr = field.max != null ? ` max="${escapeAttr(field.max)}"` : '';
    const stepAttr = field.step != null ? ` step="${escapeAttr(field.step)}"` : '';

    let control = '';

    if (field.type === 'select') {
      const currentValue = value == null ? '' : String(value);
      const options = (field.options || []).map(opt => {
        const optionValue = opt.value != null ? String(opt.value) : '';
        const selected = optionValue === currentValue ? ' selected' : '';
        return `<option value="${escapeAttr(opt.value)}"${selected}>${escapeHtml(opt.label)}</option>`;
      }).join('');
      const hasValue = currentValue.length > 0;
      const placeholderText = field.placeholder ? escapeHtml(field.placeholder) : 'Seleccioná una opción';
      const placeholderSelected = hasValue ? '' : ' selected';
      const placeholderDisabled = field.required ? ' disabled' : '';
      const placeholderOption = `<option value=""${placeholderSelected}${placeholderDisabled}>${placeholderText}</option>`;
      control = `<select class="form-control" name="${escapeAttr(field.name)}" ${requiredAttr} ${disabledAttr}>${placeholderOption}${options}</select>`;
    } else if (field.type === 'textarea') {
      const rows = field.rows || 3;
      control = `<textarea class="form-control" rows="${escapeAttr(rows)}" name="${escapeAttr(field.name)}" ${requiredAttr} ${disabledAttr}>${escapeHtml(value || '')}</textarea>`;
    } else {
      const type = field.type || 'text';
      control = `<input class="form-control" type="${escapeAttr(type)}" name="${escapeAttr(field.name)}" value="${escapeAttr(value || '')}"${placeholder}${minAttr}${maxAttr}${stepAttr} ${requiredAttr} ${disabledAttr}>`;
    }

    return `<div class="${colClass}">${label}${control}</div>`;
  }

  function setFormError(message) {
    if (!dom.formAlert) {
      return;
    }
    if (!message) {
      dom.formAlert.classList.add('d-none');
      dom.formAlert.textContent = '';
    } else {
      dom.formAlert.classList.remove('d-none');
      dom.formAlert.textContent = message;
    }
  }

  async function onFormSubmit(event) {
    event.preventDefault();
    const config = ENTITIES[state.entity];
    if (!config) {
      return;
    }
    const mode = state.formMode;
    if (!mode) {
      return;
    }

    const formData = collectFormValues(dom.form);
    let payload;
    try {
      payload = config.toPayload ? config.toPayload(mode, formData, state.editing) : formData;
    } catch (error) {
      setFormError(error?.message || 'Datos inválidos.');
      return;
    }

    setFormError('');
    dom.formSubmit.disabled = true;

    try {
      if (mode === 'create') {
        await config.create(payload);
        showMessage('success', `${capitalize(config.singular || 'registro')} creado correctamente.`);
      } else {
        await config.update(state.editing, payload);
        showMessage('success', `${capitalize(config.singular || 'registro')} actualizado correctamente.`);
      }
      closeModal();
      await loadRecords();
    } catch (error) {
      setFormError(error?.message || 'No pudimos guardar los cambios.');
    } finally {
      dom.formSubmit.disabled = false;
    }
  }

  function collectFormValues(form) {
    const values = {};
    const elements = form.querySelectorAll('[name]');
    elements.forEach(el => {
      if (el.disabled || !el.name) {
        return;
      }
      if (el.type === 'checkbox') {
        values[el.name] = el.checked;
      } else {
        values[el.name] = el.value != null ? el.value.trim() : '';
      }
    });
    return values;
  }

  function resetForm() {
    state.formMode = null;
    state.editing = null;
    if (dom.form) {
      dom.form.reset();
    }
    if (dom.formFields) {
      dom.formFields.innerHTML = '';
    }
    setFormError('');
  }

  function showModal() {
    if ($ && dom.modal) {
      $(dom.modal).modal('show');
    } else if (dom.modal) {
      dom.modal.classList.add('show');
    }
  }

  function closeModal() {
    if ($ && dom.modal) {
      $(dom.modal).modal('hide');
    } else if (dom.modal) {
      dom.modal.classList.remove('show');
    }
  }

  function showMessage(type, message) {
    if (!dom.alert) {
      return;
    }
    if (!message) {
      dom.alert.classList.add('d-none');
      dom.alert.textContent = '';
      return;
    }
    const alertClass = type ? `alert-${type}` : 'alert-info';
    dom.alert.className = `alert ${alertClass}`;
    dom.alert.textContent = message;
    dom.alert.classList.remove('d-none');
  }

  function formatAddress(direccion) {
    if (!direccion) {
      return '';
    }
    return [direccion.calle, direccion.numero, direccion.ciudad, direccion.provincia, direccion.pais]
      .filter(Boolean)
      .join(', ');
  }

  function formatLabel(value) {
    if (!value) {
      return '';
    }
    return String(value)
      .toLowerCase()
      .replace(/_/g, ' ')
      .replace(/^(\w)|\s(\w)/g, (m) => m.toUpperCase());
  }

  function formatMonth(value) {
    if (!value) {
      return '';
    }
    const found = MONTH_OPTIONS.find(option => option.value === value);
    return found ? found.label : formatLabel(value);
  }

  function formatMoney(value) {
    if (value == null || Number.isNaN(Number(value))) {
      return '—';
    }
    return `$ ${Number(value).toFixed(2)}`;
  }

  function capitalize(text) {
    if (!text) {
      return '';
    }
    const normalized = String(text);
    return normalized.charAt(0).toUpperCase() + normalized.slice(1);
  }

  function escapeHtml(value) {
    return String(value)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&#39;');
  }

  function escapeAttr(value) {
    return escapeHtml(value == null ? '' : value);
  }

  function normalizeList(data) {
    if (!data) {
      return [];
    }
    if (Array.isArray(data)) {
      return data;
    }
    if (Array.isArray(data.content)) {
      return data.content;
    }
    if (Array.isArray(data.items)) {
      return data.items;
    }
    return [];
  }

  function invalidateOptions(key) {
    if (!key) {
      return;
    }
    delete optionCache[key];
  }

  async function loadEmpresasOptions() {
    if (optionCache.empresas) {
      return optionCache.empresas;
    }
    const data = await requestJson(buildUrl('/api/empresas'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: item.nombre }));
    optionCache.empresas = list;
    return list;
  }

  async function loadValorCuotasOptions() {
    if (optionCache.valorCuotas) {
      return optionCache.valorCuotas;
    }
    const data = await requestJson(buildUrl('/api/v1/valor-cuotas'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({
        value: item.id,
        label: `${item.fechaDesde || 'Sin inicio'} · ${item.fechaHasta || 'Abierto'} · $ ${Number(item.valorCuota).toFixed(2)}`
      }));
    optionCache.valorCuotas = list;
    return list;
  }

  async function loadPaisesOptions() {
    if (optionCache.paises) {
      return optionCache.paises;
    }
    const data = await requestJson(buildUrl('/api/paises'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: item.nombre }));
    optionCache.paises = list;
    return list;
  }

  async function sendJson(url, method, payload) {
    return requestJson(url, {
      method,
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload)
    });
  }

  async function requestJson(url, options) {
    const finalOptions = options ? { ...options } : {};
    finalOptions.headers = finalOptions.headers ? { ...finalOptions.headers } : {};
    const response = await fetch(url, finalOptions);
    if (!response.ok) {
      const errorMessage = await parseError(response);
      throw new Error(errorMessage || 'Error al comunicarse con el servidor.');
    }
    if (response.status === 204) {
      return null;
    }
    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
      return response.json();
    }
    return response.text();
  }

  async function parseError(response) {
    try {
      const text = await response.text();
      if (!text) {
        return response.statusText || '';
      }
      if (response.headers.get('content-type')?.includes('application/json')) {
        const parsed = JSON.parse(text);
        return parsed.message || parsed.error || response.statusText || text;
      }
      return text;
    } catch (error) {
      return response.statusText || 'Error desconocido';
    }
  }
})();
