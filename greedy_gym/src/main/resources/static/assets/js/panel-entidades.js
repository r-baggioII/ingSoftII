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
    subtype: document.getElementById('entity-subtype'),
    subtypeContainer: document.getElementById('entity-subtype-container'),
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
    direcciones: {
      label: 'Gestión Geográfica',
      description: 'Gestión completa de direcciones, países, provincias, departamentos y localidades.',
      singular: 'elemento geográfico',
      createLabel: 'Nuevo elemento',
      searchPlaceholder: 'Buscar en cualquier categoría',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      
      // Configuración dinámica que cambia según el subtipo seleccionado
      currentSubType: 'direccionesData',
      
      get columns() {
        return this.getSubConfig().columns;
      },
      
      get formFields() {
        return this.getSubConfig().formFields;
      },
      
      get createLabel() {
        return this.getSubConfig().createLabel;
      },
      
      get singular() {
        return this.getSubConfig().singular;
      },

      // Método para obtener la configuración de la sub-entidad actual
      getSubConfig() {
        return this.subTypes[this.currentSubType] || this.subTypes.direccionesData;
      },

      // Definición de los diferentes tipos de entidades
      subTypes: {
        direccionesData: {
          label: 'Direcciones',
          singular: 'dirección',
          createLabel: 'Nueva dirección',
          columns: [
            { header: 'Calle', value: item => item?.calle || '—' },
            { header: 'Número', value: item => item?.numero || '—' },
            { header: 'Barrio', value: item => item?.barrio || '—' },
            { header: 'Localidad', value: item => item?.localidad?.nombre || '—' },
            { header: 'Departamento', value: item => item?.localidad?.departamento?.nombre || '—' },
            { header: 'Provincia', value: item => item?.localidad?.departamento?.provincia?.nombre || '—' },
            { header: 'País', value: item => item?.localidad?.departamento?.provincia?.pais?.nombre || '—' },
            { header: 'Estado', value: item => item?.eliminado ? 'Dada de baja' : 'Activa' }
          ],
          formFields: [
            { name: 'calle', label: 'Calle', type: 'text', required: true, width: 'col-md-8' },
            { name: 'numeracion', label: 'Número', type: 'text', required: true, width: 'col-md-4' },
            { name: 'nombrePais', label: 'País', type: 'autocomplete', required: true, width: 'col-md-6', apiEndpoint: '/api/direcciones/paises', displayField: 'nombre' },
            { name: 'nombreProvincia', label: 'Provincia', type: 'autocomplete', required: true, width: 'col-md-6', apiEndpoint: '/api/direcciones/provincias', displayField: 'nombre' },
            { name: 'nombreDepartamento', label: 'Departamento', type: 'autocomplete', required: true, width: 'col-md-6', apiEndpoint: '/api/direcciones/departamentos', displayField: 'nombre' },
            { name: 'nombreLocalidad', label: 'Localidad', type: 'autocomplete', required: true, width: 'col-md-6', apiEndpoint: '/api/direcciones/localidades', displayField: 'nombre' },
            { name: 'codigoPostal', label: 'Código Postal', type: 'text' },
            { name: 'barrio', label: 'Barrio', type: 'text' },
            { name: 'manzanaPiso', label: 'Manzana/Piso', type: 'text' },
            { name: 'casaDepartamento', label: 'Casa/Departamento', type: 'text' },
            { name: 'referencia', label: 'Referencia', type: 'textarea' }
          ],
          apiPath: '/api/direcciones',
          submitHandler: (mode, values) => {
            const payload = {
              calle: values.calle ? values.calle.trim() : null,
              numeracion: values.numeracion ? values.numeracion.trim() : null,
              barrio: values.barrio ? values.barrio.trim() : null,
              manzanaPiso: values.manzanaPiso ? values.manzanaPiso.trim() : null,
              casaDepartamento: values.casaDepartamento ? values.casaDepartamento.trim() : null,
              referencia: values.referencia ? values.referencia.trim() : null,
              nombrePais: values.nombrePais ? values.nombrePais.trim() : null,
              nombreProvincia: values.nombreProvincia ? values.nombreProvincia.trim() : null,
              nombreDepartamento: values.nombreDepartamento ? values.nombreDepartamento.trim() : null,
              nombreLocalidad: values.nombreLocalidad ? values.nombreLocalidad.trim() : null,
              codigoPostal: values.codigoPostal ? values.codigoPostal.trim() : null
            };
            
            if (mode === 'create') {
              return sendJson(buildUrl('/api/direcciones/con-nombres'), 'POST', payload);
            } else {
              return sendJson(buildUrl(`/api/direcciones/${values.id}`), 'PUT', payload);
            }
          }
        },
        localidades: {
          label: 'Localidades',
          singular: 'localidad',
          createLabel: 'Nueva localidad',
          columns: [
            { header: 'Nombre', value: item => item?.nombre || '—' },
            { header: 'Código Postal', value: item => item?.codigoPostal || '—' },
            { header: 'Departamento', value: item => item?.departamento?.nombre || '—' },
            { header: 'Estado', value: item => item?.eliminado ? 'Dada de baja' : 'Activa' }
          ],
          formFields: [
            { name: 'nombre', label: 'Nombre de la localidad', type: 'text', required: true, width: 'col-md-8' },
            { name: 'codigoPostal', label: 'Código Postal', type: 'text' },
            { name: 'idDepartamento', label: 'Departamento', type: 'select', required: true, fullWidth: true, loadOptions: loadDepartamentosOptions }
          ],
          apiPath: '/api/direcciones/localidades'
        },
        departamentos: {
          label: 'Departamentos',
          singular: 'departamento',
          createLabel: 'Nuevo departamento',
          columns: [
            { header: 'Nombre', value: item => item?.nombre || '—' },
            { header: 'Provincia', value: item => item?.provincia?.nombre || '—' },
            { header: 'Estado', value: item => item?.eliminado ? 'Dado de baja' : 'Activo' }
          ],
          formFields: [
            { name: 'nombre', label: 'Nombre del departamento', type: 'text', required: true, width: 'col-md-8' },
            { name: 'idProvincia', label: 'Provincia', type: 'select', required: true, width: 'col-md-4', loadOptions: loadProvinciasOptions }
          ],
          apiPath: '/api/direcciones/departamentos'
        },
        provincias: {
          label: 'Provincias',
          singular: 'provincia',
          createLabel: 'Nueva provincia',
          columns: [
            { header: 'Nombre', value: item => item?.nombre || '—' },
            { header: 'País', value: item => item?.pais?.nombre || '—' },
            { header: 'Estado', value: item => item?.eliminado ? 'Dada de baja' : 'Activa' }
          ],
          formFields: [
            { name: 'nombre', label: 'Nombre de la provincia', type: 'text', required: true, width: 'col-md-8' },
            { name: 'idPais', label: 'País', type: 'select', required: true, width: 'col-md-4', loadOptions: loadPaisesOptions }
          ],
          apiPath: '/api/direcciones/provincias'
        },
        paises: {
          label: 'Países',
          singular: 'país',
          createLabel: 'Nuevo país',
          columns: [
            { header: 'Nombre', value: item => item?.nombre || '—' },
            { header: 'Estado', value: item => item?.eliminado ? 'Dado de baja' : 'Activo' }
          ],
          formFields: [
            { name: 'nombre', label: 'Nombre del país', type: 'text', required: true, fullWidth: true }
          ],
          apiPath: '/api/direcciones/paises'
        }
      },

      async list() {
        const config = this.getSubConfig();
        return requestJson(buildUrl(config.apiPath));
      },
      
      async create(payload) {
        const config = this.getSubConfig();
        return sendJson(buildUrl(config.apiPath), 'POST', payload);
      },
      
      async update(item, payload) {
        const config = this.getSubConfig();
        if (config.apiPath === '/api/direcciones/paises') {
          // Para países usamos query params
          const params = new URLSearchParams();
          params.set('nombre', payload.nombre || '');
          return requestJson(`${buildUrl(config.apiPath + '/' + item.id)}?${params.toString()}`, { method: 'PUT' });
        } else {
          return sendJson(buildUrl(config.apiPath + '/' + item.id), 'PUT', payload);
        }
      },
      
      async remove(item) {
        const config = this.getSubConfig();
        return requestJson(buildUrl(config.apiPath + '/' + item.id), { method: 'DELETE' });
      },
      
      prepareFormValues(item) {
        const subType = this.currentSubType;
        
        switch(subType) {
          case 'direccionesData':
            return {
              calle: item?.calle || '',
              numeracion: item?.numero || '',
              nombrePais: item?.localidad?.departamento?.provincia?.pais?.nombre || '',
              nombreProvincia: item?.localidad?.departamento?.provincia?.nombre || '',
              nombreDepartamento: item?.localidad?.departamento?.nombre || '',
              nombreLocalidad: item?.localidad?.nombre || '',
              codigoPostal: item?.localidad?.codigoPostal || '',
              barrio: item?.barrio || '',
              manzanaPiso: item?.manzanaPiso || '',
              casaDepartamento: item?.casaDepartamento || '',
              referencia: item?.referencia || ''
            };
          case 'localidades':
            return {
              nombre: item?.nombre || '',
              codigoPostal: item?.codigoPostal || '',
              idDepartamento: item?.departamento?.id || ''
            };
          case 'departamentos':
            return {
              nombre: item?.nombre || '',
              idProvincia: item?.provincia?.id || ''
            };
          case 'provincias':
            return {
              nombre: item?.nombre || '',
              idPais: item?.pais?.id || ''
            };
          case 'paises':
            return {
              nombre: item?.nombre || ''
            };
          default:
            return {};
        }
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
    },
    paises: {
      label: 'Países',
      description: 'Gestión de países para organizar direcciones.',
      singular: 'país',
      createLabel: 'Nuevo país',
      searchPlaceholder: 'Buscar por nombre de país',
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
        return requestJson(buildUrl('/api/v1/paises'));
      },
      async create(payload) {
        invalidateOptions('paises');
        invalidateOptions('paisesV1');
        return sendJson(buildUrl('/api/v1/paises'), 'POST', payload);
      },
      async update(item, payload) {
        invalidateOptions('paises');
        invalidateOptions('paisesV1');
        return sendJson(buildUrl('/api/v1/paises/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        invalidateOptions('paises');
        invalidateOptions('paisesV1');
        invalidateOptions('provincias');
        invalidateOptions('provinciasV1');
        return requestJson(buildUrl('/api/v1/paises/' + item.id), { method: 'DELETE' });
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
    },
    provincias: {
      label: 'Provincias',
      description: 'Gestión de provincias organizadas por país.',
      singular: 'provincia',
      createLabel: 'Nueva provincia',
      searchPlaceholder: 'Buscar por nombre de provincia o país',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Nombre', value: item => item?.nombre || '—' },
        { header: 'País', value: item => item?.pais?.nombre || '—' },
        { header: 'Estado', value: item => item?.eliminado ? 'Dada de baja' : 'Activa' }
      ],
      formFields: [
        { name: 'nombre', label: 'Nombre de la provincia', type: 'text', required: true, width: 'col-md-8' },
        { name: 'idPais', label: 'País', type: 'select', required: true, width: 'col-md-4', loadOptions: loadPaisesOptionsV1 }
      ],
      async list() {
        return requestJson(buildUrl('/api/v1/provincias'));
      },
      async create(payload) {
        invalidateOptions('provincias');
        invalidateOptions('provinciasV1');
        return sendJson(buildUrl('/api/v1/provincias'), 'POST', payload);
      },
      async update(item, payload) {
        invalidateOptions('provincias');
        invalidateOptions('provinciasV1');
        return sendJson(buildUrl('/api/v1/provincias/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        invalidateOptions('provincias');
        invalidateOptions('provinciasV1');
        invalidateOptions('departamentos');
        invalidateOptions('departamentosV1');
        return requestJson(buildUrl('/api/v1/provincias/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        return {
          nombre: item?.nombre || '',
          idPais: item?.pais?.id || ''
        };
      },
      toPayload(mode, values) {
        return { 
          nombre: values.nombre.trim(),
          idPais: values.idPais
        };
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [item?.nombre, item?.pais?.nombre].filter(Boolean).join(' ').toLowerCase();
      }
    },
    departamentos: {
      label: 'Departamentos',
      description: 'Gestión de departamentos organizados por provincia.',
      singular: 'departamento',
      createLabel: 'Nuevo departamento',
      searchPlaceholder: 'Buscar por nombre de departamento o provincia',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Nombre', value: item => item?.nombre || '—' },
        { header: 'Provincia', value: item => item?.provincia?.nombre || '—' },
        { header: 'País', value: item => item?.provincia?.pais?.nombre || '—' },
        { header: 'Estado', value: item => item?.eliminado ? 'Dado de baja' : 'Activo' }
      ],
      formFields: [
        { name: 'nombre', label: 'Nombre del departamento', type: 'text', required: true, width: 'col-md-8' },
        { name: 'idProvincia', label: 'Provincia', type: 'select', required: true, width: 'col-md-4', loadOptions: loadProvinciasOptionsV1 }
      ],
      async list() {
        return requestJson(buildUrl('/api/v1/departamentos'));
      },
      async create(payload) {
        invalidateOptions('departamentos');
        invalidateOptions('departamentosV1');
        return sendJson(buildUrl('/api/v1/departamentos'), 'POST', payload);
      },
      async update(item, payload) {
        invalidateOptions('departamentos');
        invalidateOptions('departamentosV1');
        return sendJson(buildUrl('/api/v1/departamentos/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        invalidateOptions('departamentos');
        invalidateOptions('departamentosV1');
        invalidateOptions('localidades');
        invalidateOptions('localidadesV1');
        return requestJson(buildUrl('/api/v1/departamentos/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        return {
          nombre: item?.nombre || '',
          idProvincia: item?.provincia?.id || ''
        };
      },
      toPayload(mode, values) {
        return { 
          nombre: values.nombre.trim(),
          idProvincia: values.idProvincia
        };
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [item?.nombre, item?.provincia?.nombre, item?.provincia?.pais?.nombre].filter(Boolean).join(' ').toLowerCase();
      }
    },
    localidades: {
      label: 'Localidades',
      description: 'Gestión de localidades organizadas por departamento.',
      singular: 'localidad',
      createLabel: 'Nueva localidad',
      searchPlaceholder: 'Buscar por nombre de localidad, código postal o departamento',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Nombre', value: item => item?.nombre || '—' },
        { header: 'Código Postal', value: item => item?.codigoPostal || '—' },
        { header: 'Departamento', value: item => item?.departamento?.nombre || '—' },
        { header: 'Provincia', value: item => item?.departamento?.provincia?.nombre || '—' },
        { header: 'Estado', value: item => item?.eliminado ? 'Dada de baja' : 'Activa' }
      ],
      formFields: [
        { name: 'nombre', label: 'Nombre de la localidad', type: 'text', required: true, width: 'col-md-6' },
        { name: 'codigoPostal', label: 'Código Postal', type: 'text' },
        { name: 'idDepartamento', label: 'Departamento', type: 'select', required: true, fullWidth: true, loadOptions: loadDepartamentosOptionsV1 }
      ],
      async list() {
        return requestJson(buildUrl('/api/v1/localidades'));
      },
      async create(payload) {
        invalidateOptions('localidades');
        invalidateOptions('localidadesV1');
        return sendJson(buildUrl('/api/v1/localidades'), 'POST', payload);
      },
      async update(item, payload) {
        invalidateOptions('localidades');
        invalidateOptions('localidadesV1');
        return sendJson(buildUrl('/api/v1/localidades/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        invalidateOptions('localidades');
        invalidateOptions('localidadesV1');
        return requestJson(buildUrl('/api/v1/localidades/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        return {
          nombre: item?.nombre || '',
          codigoPostal: item?.codigoPostal || '',
          idDepartamento: item?.departamento?.id || ''
        };
      },
      toPayload(mode, values) {
        return { 
          nombre: values.nombre.trim(),
          codigoPostal: values.codigoPostal ? values.codigoPostal.trim() : '',
          idDepartamento: values.idDepartamento
        };
      },
      isInactive(item) {
        return !!item?.eliminado;
      },
      searchText(item) {
        return [item?.nombre, item?.codigoPostal, item?.departamento?.nombre, item?.departamento?.provincia?.nombre].filter(Boolean).join(' ').toLowerCase();
      }
    },
    usuarios: {
      label: 'Usuarios',
      description: 'Gestión de usuarios del sistema.',
      singular: 'usuario',
      createLabel: 'Nuevo usuario',
      searchPlaceholder: 'Buscar por nombre de usuario o correo electrónico',
      allowCreate: true,
      allowUpdate: true,
      allowDelete: true,
      columns: [
        { header: 'Nombre de Usuario', value: item => item?.nombreUsuario || '—' },
        { header: 'Correo', value: item => item?.correoElectronico || '—' },
        { header: 'Estado', value: item => item?.eliminado ? 'Inactivo' : 'Activo' }
      ],
      formFields: [
        { name: 'nombreUsuario', label: 'Nombre de Usuario', type: 'text', required: true },
        { name: 'clave', label: 'Clave', type: 'password', required: true },
        { name: 'correoElectronico', label: 'Correo Electrónico', type: 'email', required: true }
      ],
      async list() {
        return requestJson(buildUrl('/api/usuarios'));
      },
      async create(payload) {
        return sendJson(buildUrl('/api/usuarios'), 'POST', payload);
      },
      async update(item, payload) {
        return sendJson(buildUrl('/api/usuarios/' + item.id), 'PUT', payload);
      },
      async remove(item) {
        return requestJson(buildUrl('/api/usuarios/' + item.id), { method: 'DELETE' });
      },
      prepareFormValues(item) {
        return {
          nombreUsuario: item?.nombreUsuario || '',
          clave: '',
          correoElectronico: item?.correoElectronico || ''
        };
      },
      toPayload(mode, values) {
        return {
          nombreUsuario: values.nombreUsuario.trim(),
          clave: values.clave.trim(),
          correoElectronico: values.correoElectronico.trim()
        };
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
    if (dom.subtype) {
      dom.subtype.addEventListener('change', onSubtypeChange);
    }
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
    
    // Mostrar/ocultar el selector de subtipo solo para direcciones
    if (dom.subtypeContainer) {
      if (entityKey === 'direcciones') {
        dom.subtypeContainer.style.display = 'block';
        // Establecer el valor por defecto si no está seleccionado
        if (dom.subtype && !dom.subtype.value) {
          dom.subtype.value = 'direccionesData';
          ENTITIES.direcciones.currentSubType = 'direccionesData';
        }
      } else {
        dom.subtypeContainer.style.display = 'none';
      }
    }
    
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

  function onSubtypeChange() {
    if (state.entity === 'direcciones' && dom.subtype) {
      const newSubtype = dom.subtype.value;
      console.log('[Panel entidades] Subtype changed to:', newSubtype);
      
      // Actualizar el estado de la entidad direcciones
      ENTITIES.direcciones.currentSubType = newSubtype;
      
      // Recargar los datos con la nueva configuración
      refresh();
    }
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
    
    // Inicializar autocomplete para campos que lo requieran
    initializeAutocompleteFields();
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
    } else if (field.type === 'autocomplete') {
      const currentValue = value == null ? '' : String(value);
      const placeholderText = field.placeholder ? escapeHtml(field.placeholder) : `Buscar ${field.label}...`;
      const apiEndpoint = field.apiEndpoint || '';
      control = `
        <div class="position-relative">
          <input class="form-control autocomplete-input" 
                 type="text" 
                 name="${escapeAttr(field.name)}" 
                 value="${escapeAttr(currentValue)}" 
                 placeholder="${escapeAttr(placeholderText)}"
                 data-api-endpoint="${escapeAttr(apiEndpoint)}"
                 ${requiredAttr} ${disabledAttr}>
          <ul class="autocomplete-suggestions list-group position-absolute w-100" style="z-index: 1050; display: none;"></ul>
        </div>`;
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
    const data = await requestJson(buildUrl('/api/direcciones/paises'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: item.nombre }));
    optionCache.paises = list;
    return list;
  }

  async function loadProvinciasOptions() {
    if (optionCache.provincias) {
      return optionCache.provincias;
    }
    const data = await requestJson(buildUrl('/api/direcciones/provincias'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: item.nombre }));
    optionCache.provincias = list;
    return list;
  }

  async function loadDepartamentosOptions() {
    if (optionCache.departamentos) {
      return optionCache.departamentos;
    }
    const data = await requestJson(buildUrl('/api/direcciones/departamentos'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: item.nombre }));
    optionCache.departamentos = list;
    return list;
  }

  async function loadLocalidadesOptions() {
    if (optionCache.localidades) {
      return optionCache.localidades;
    }
    const data = await requestJson(buildUrl('/api/direcciones/localidades'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: item.nombre }));
    optionCache.localidades = list;
    return list;
  }

  // Funciones para los nuevos endpoints v1
  async function loadPaisesOptionsV1() {
    if (optionCache.paisesV1) {
      return optionCache.paisesV1;
    }
    const data = await requestJson(buildUrl('/api/v1/paises/activos'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: item.nombre }));
    optionCache.paisesV1 = list;
    return list;
  }

  async function loadProvinciasOptionsV1() {
    if (optionCache.provinciasV1) {
      return optionCache.provinciasV1;
    }
    const data = await requestJson(buildUrl('/api/v1/provincias/activos'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: `${item.nombre} (${item.pais?.nombre || 'Sin país'})` }));
    optionCache.provinciasV1 = list;
    return list;
  }

  async function loadDepartamentosOptionsV1() {
    if (optionCache.departamentosV1) {
      return optionCache.departamentosV1;
    }
    const data = await requestJson(buildUrl('/api/v1/departamentos/activos'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: `${item.nombre} (${item.provincia?.nombre || 'Sin provincia'})` }));
    optionCache.departamentosV1 = list;
    return list;
  }

  async function loadLocalidadesOptionsV1() {
    if (optionCache.localidadesV1) {
      return optionCache.localidadesV1;
    }
    const data = await requestJson(buildUrl('/api/v1/localidades/activos'));
    const list = normalizeList(data)
      .filter(item => !item.eliminado)
      .map(item => ({ value: item.id, label: `${item.nombre} ${item.codigoPostal ? '(' + item.codigoPostal + ')' : ''} - ${item.departamento?.nombre || 'Sin depto'}` }));
    optionCache.localidadesV1 = list;
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

  function initializeAutocompleteFields() {
    const autocompleteInputs = dom.formFields.querySelectorAll('.autocomplete-input');
    
    autocompleteInputs.forEach(input => {
      const suggestionsList = input.parentElement.querySelector('.autocomplete-suggestions');
      const apiEndpoint = input.dataset.apiEndpoint;
      
      if (!apiEndpoint) return;
      
      let searchTimeout;
      
      input.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        const query = this.value.trim();
        
        if (query.length >= 2) {
          searchTimeout = setTimeout(() => {
            fetch(`${apiEndpoint}?search=${encodeURIComponent(query)}`)
              .then(response => response.json())
              .then(data => {
                suggestionsList.innerHTML = '';
                
                if (data.length > 0) {
                  suggestionsList.style.display = 'block';
                  
                  data.forEach(item => {
                    const li = document.createElement('li');
                    li.className = 'list-group-item list-group-item-action';
                    li.textContent = item.nombre;
                    li.style.cursor = 'pointer';
                    
                    li.addEventListener('click', () => {
                      input.value = item.nombre;
                      suggestionsList.style.display = 'none';
                    });
                    
                    suggestionsList.appendChild(li);
                  });
                } else {
                  suggestionsList.style.display = 'none';
                }
              })
              .catch(error => {
                console.error('Error fetching autocomplete data:', error);
                suggestionsList.style.display = 'none';
              });
          }, 300);
        } else {
          suggestionsList.style.display = 'none';
        }
      });
      
      // Ocultar sugerencias cuando se hace clic fuera
      document.addEventListener('click', function(e) {
        if (!input.parentElement.contains(e.target)) {
          suggestionsList.style.display = 'none';
        }
      });
    });
  }
})();
