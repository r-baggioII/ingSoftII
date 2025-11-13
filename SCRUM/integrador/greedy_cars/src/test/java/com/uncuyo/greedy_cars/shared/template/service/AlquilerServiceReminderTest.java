package com.uncuyo.greedy_cars.shared.template.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoTelefonico;
import com.uncuyo.greedy_cars.shared.template.entity.Recordatorio;
import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.enums.TipoRecordatorio;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.mapper.AlquilerMapper;
import com.uncuyo.greedy_cars.shared.template.repository.AlquilerRepository;
import com.uncuyo.greedy_cars.shared.template.repository.ClienteRepository;
import com.uncuyo.greedy_cars.shared.template.repository.DocumentacionRepository;
import com.uncuyo.greedy_cars.shared.template.repository.RecordatorioRepository;
import com.uncuyo.greedy_cars.shared.template.repository.VehiculoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AlquilerServiceReminderTest {

    private AlquilerRepository alquilerRepository;
    private ClienteRepository clienteRepository;
    private VehiculoRepository vehiculoRepository;
    private DocumentacionRepository documentacionRepository;
    private FacturaService facturaService;
    private CostoVehiculoService costoVehiculoService;
    private PromocionService promocionService;
    private AlquilerMapper alquilerMapper;
    private NotificacionCorreoService notificacionCorreoService;
    private WhatsAppService whatsAppService;
    private RecordatorioRepository recordatorioRepository;

    private AlquilerService alquilerService;

    @BeforeEach
    void setUp() {
        alquilerRepository = Mockito.mock(AlquilerRepository.class);
        clienteRepository = Mockito.mock(ClienteRepository.class);
        vehiculoRepository = Mockito.mock(VehiculoRepository.class);
        documentacionRepository = Mockito.mock(DocumentacionRepository.class);
        facturaService = Mockito.mock(FacturaService.class);
        costoVehiculoService = Mockito.mock(CostoVehiculoService.class);
        promocionService = Mockito.mock(PromocionService.class);
        alquilerMapper = Mockito.mock(AlquilerMapper.class);
        notificacionCorreoService = Mockito.mock(NotificacionCorreoService.class);
        whatsAppService = Mockito.mock(WhatsAppService.class);
        recordatorioRepository = Mockito.mock(RecordatorioRepository.class);

        alquilerService = new AlquilerService(
                alquilerRepository,
                clienteRepository,
                vehiculoRepository,
                documentacionRepository,
                facturaService,
                costoVehiculoService,
                promocionService,
                alquilerMapper,
                notificacionCorreoService,
                whatsAppService,
                recordatorioRepository
        );
    }

    @Test
    void enviarRecordatoriosProgramados_disparaCanales() throws ErrorServiceException {
        Alquiler alquiler = crearAlquiler();
        when(alquilerRepository.findAllByFechaHastaAndEliminadoIsFalse(any(LocalDate.class)))
                .thenReturn(List.of(alquiler));
        when(recordatorioRepository.existsByAlquilerIdAndTipoRecordatorioAndEliminadoIsFalse(any(), any()))
                .thenReturn(false);
        when(recordatorioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(whatsAppService.enviarRecordatorioDevolucion(any(), eq(false)))
                .thenReturn(new WhatsAppService.WhatsAppSendResult(true, "sid", null));

        AlquilerService.ReminderDispatchResult result =
                alquilerService.enviarRecordatoriosDevolucionProgramados(LocalDate.now().plusDays(1));

        assertEquals(1, result.totalAlquileres());
        assertEquals(1, result.correosExitosos());
        assertEquals(1, result.whatsappsExitosos());
        verify(notificacionCorreoService, times(1)).enviarRecordatorioDevolucion(alquiler);
        verify(whatsAppService, times(1)).enviarRecordatorioDevolucion(alquiler, false);
        verify(recordatorioRepository, times(2)).save(any(Recordatorio.class));
    }

    @Test
    void enviarRecordatorioManual_previeneDuplicados() throws ErrorServiceException {
        Alquiler alquiler = crearAlquiler();
        when(alquilerRepository.findByIdAndEliminadoIsFalse("A1")).thenReturn(Optional.of(alquiler));
        when(recordatorioRepository.existsByAlquilerIdAndTipoRecordatorioAndEliminadoIsFalse("A1", TipoRecordatorio.WHATSAPP_MANUAL))
                .thenReturn(false)
                .thenReturn(true);
        when(recordatorioRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(whatsAppService.enviarRecordatorioDevolucion(alquiler, true))
                .thenReturn(new WhatsAppService.WhatsAppSendResult(true, "sid", null));

        Recordatorio enviado = alquilerService.enviarRecordatorioManualWhatsapp("A1");
        assertEquals(TipoRecordatorio.WHATSAPP_MANUAL, enviado.getTipoRecordatorio());

        assertThrows(ErrorServiceException.class, () -> alquilerService.enviarRecordatorioManualWhatsapp("A1"));
    }

    private Alquiler crearAlquiler() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Ana");
        cliente.setApellido("Gómez");

        ContactoTelefonico contacto = new ContactoTelefonico();
        contacto.setTelefono("+5492615557777");
        cliente.getContactos().add(contacto);

        CaracteristicaVehiculo caracteristica = new CaracteristicaVehiculo();
        caracteristica.setMarca("Ford");
        caracteristica.setModelo("Fiesta");

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente("AA123BB");
        vehiculo.setCaracteristicaVehiculo(caracteristica);

        Alquiler alquiler = new Alquiler();
        alquiler.setId("A1");
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        alquiler.setFechaHasta(LocalDate.now().plusDays(1));
        return alquiler;
    }
}
