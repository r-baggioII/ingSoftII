(function () {
  'use strict';

  function debugCostosPage() {
    console.log('=== DEBUG COSTOS PAGE ===');

    // Check if costos table exists
    const costosTable = document.querySelector('table tbody');
    console.log('Costos table found:', !!costosTable);

    if (costosTable) {
      const rows = costosTable.querySelectorAll('tr');
      console.log('Number of table rows:', rows.length);

      rows.forEach((row, index) => {
        console.log(`Row ${index}:`, row.textContent.trim());
      });
    }

    // Check if costos are in the model data
    const costosCells = document.querySelectorAll('td:first-child');
    console.log('Costo ID cells found:', costosCells.length);

    costosCells.forEach((cell, index) => {
      console.log(`Costo ID ${index}:`, cell.textContent.trim());
    });

    // Check for error messages
    const errorAlerts = document.querySelectorAll('.alert-danger');
    console.log('Error alerts found:', errorAlerts.length);
    errorAlerts.forEach((alert, index) => {
      console.log(`Error ${index}:`, alert.textContent.trim());
    });

    // Check for success messages
    const successAlerts = document.querySelectorAll('.alert-success');
    console.log('Success alerts found:', successAlerts.length);
    successAlerts.forEach((alert, index) => {
      console.log(`Success ${index}:`, alert.textContent.trim());
    });

    // Check caracteristicas dropdown
    const caracteristicaSelect = document.getElementById('costo-caracteristica');
    if (caracteristicaSelect) {
      console.log('Caracteristicas select found with', caracteristicaSelect.options.length, 'options');
      for (let i = 0; i < caracteristicaSelect.options.length; i++) {
        const option = caracteristicaSelect.options[i];
        console.log(`  Option ${i}: "${option.text}" (value: ${option.value})`);
      }
    } else {
      console.log('Caracteristicas select NOT found');
    }

    console.log('=== END DEBUG COSTOS PAGE ===');
  }

  function initCaracteristicaResumen() {
    console.log('=== INIT CARACTERISTICA RESUMEN ===');
    const select = document.getElementById('costo-caracteristica');
    const resumen = document.getElementById('resumen-caracteristica');
    console.log('Select element:', select);
    console.log('Resumen element:', resumen);

    if (!select || !resumen) {
      console.log('Missing elements - select:', !!select, 'resumen:', !!resumen);
      return;
    }

    const defaultMessage = resumen.textContent.trim() || 'Seleccione una característica para ver sus detalles.';

    const buildText = (option) => {
      console.log('buildText called with option:', option);
      if (!option || !option.value) {
        console.log('No option or no value, returning default message');
        return defaultMessage;
      }
      const fragments = [];
      const marca = option.dataset.marca || '';
      const modelo = option.dataset.modelo || '';
      console.log('Option data - marca:', marca, 'modelo:', modelo);
      const encabezado = [marca, modelo].filter(Boolean).join(' ').trim();
      if (encabezado) {
        fragments.push(encabezado);
      }
      if (option.dataset.anio) {
        fragments.push('Año ' + option.dataset.anio);
      }
      if (option.dataset.puertas) {
        fragments.push(option.dataset.puertas + ' puertas');
      }
      if (option.dataset.asientos) {
        fragments.push(option.dataset.asientos + ' asientos');
      }
      fragments.push('ID: ' + option.value);
      const result = fragments.join(' · ');
      console.log('buildText result:', result);
      return result;
    };

    const updateResumen = () => {
      console.log('updateResumen called - select disabled:', select.disabled, 'select value:', select.value);
      if (select.disabled || !select.value) {
        const message = select.disabled
          ? 'No hay características disponibles. Crea una desde Gestión de Vehículos.'
          : defaultMessage;
        console.log('Setting resumen to message:', message);
        resumen.textContent = message;
        return;
      }
      const option = select.options[select.selectedIndex];
      console.log('Selected option:', option);
      resumen.textContent = buildText(option);
    };

    select.addEventListener('change', function() {
      console.log('Select change event triggered');
      updateResumen();
    });
    updateResumen();
    console.log('=== FIN CARACTERISTICA RESUMEN ===');
  }

  function initDateGuards() {
    console.log('=== INIT DATE GUARDS ===');
    const fechaDesde = document.querySelector('input[name="fechaDesde"]');
    const fechaHasta = document.querySelector('input[name="fechaHasta"]');
    console.log('fechaDesde input:', fechaDesde);
    console.log('fechaHasta input:', fechaHasta);

    if (!fechaDesde || !fechaHasta) {
      console.log('Missing date inputs - desde:', !!fechaDesde, 'hasta:', !!fechaHasta);
      return;
    }

    const sync = () => {
      console.log('Date sync called - desde value:', fechaDesde.value, 'hasta value:', fechaHasta.value);
      if (fechaDesde.value) {
        fechaHasta.min = fechaDesde.value;
        if (fechaHasta.value && fechaHasta.value < fechaDesde.value) {
          console.log('Adjusting hasta value from', fechaHasta.value, 'to', fechaDesde.value);
          fechaHasta.value = fechaDesde.value;
        }
      } else {
        fechaHasta.min = '';
      }
    };

    fechaDesde.addEventListener('change', function() {
      console.log('Fecha desde changed');
      sync();
    });
    sync();
    console.log('=== FIN DATE GUARDS ===');
  }

  document.addEventListener('DOMContentLoaded', function () {
    console.log('=== DOM CONTENT LOADED ===');
    console.log('Costos page fully loaded, starting initialization...');

    // Run debug function first
    setTimeout(function() {
      debugCostosPage();
    }, 100);

    initCaracteristicaResumen();
    initDateGuards();

    console.log('=== INITIALIZATION COMPLETE ===');
  });
})();
