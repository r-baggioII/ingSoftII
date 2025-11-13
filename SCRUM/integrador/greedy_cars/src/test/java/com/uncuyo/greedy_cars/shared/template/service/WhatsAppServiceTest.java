package com.uncuyo.greedy_cars.shared.template.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.uncuyo.greedy_cars.shared.template.entity.Alquiler;
import com.uncuyo.greedy_cars.shared.template.entity.CaracteristicaVehiculo;
import com.uncuyo.greedy_cars.shared.template.entity.Cliente;
import com.uncuyo.greedy_cars.shared.template.entity.ContactoTelefonico;
import com.uncuyo.greedy_cars.shared.template.entity.Vehiculo;
import com.uncuyo.greedy_cars.shared.template.enums.TipoTelefono;
import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class WhatsAppServiceTest {

    private WhatsAppGateway gateway;
    private WhatsAppService service;

    @BeforeEach
    void setUp() {
        gateway = Mockito.mock(WhatsAppGateway.class);
        service = new WhatsAppService(gateway, "whatsapp:+14155238886", "+54 9 261 555 1234");
    }

    @Test
    void enviarRecordatorioDevolucion_ok() throws ErrorServiceException {
        Alquiler alquiler = crearAlquilerConContacto();
        when(gateway.send(any(), any(), any())).thenReturn("sid-test");

        WhatsAppService.WhatsAppSendResult result = service.enviarRecordatorioDevolucion(alquiler, false);

        assertTrue(result.enviado());
        assertEquals("sid-test", result.sid());
        verify(gateway).send(eq("whatsapp:+14155238886"), eq("whatsapp:+5492615551111"), any());
    }

    @Test
    void enviarRecordatorioDevolucion_sinTelefono() {
        Alquiler alquiler = crearAlquilerSinContacto();

        assertThrows(ErrorServiceException.class, () -> service.enviarRecordatorioDevolucion(alquiler, false));
        verifyNoInteractions(gateway);
    }

    private Alquiler crearAlquilerConContacto() {
        Alquiler alquiler = crearAlquilerBase();
        Cliente cliente = alquiler.getCliente();
        ContactoTelefonico contacto = new ContactoTelefonico();
        contacto.setTelefono("+54 9 2615551111");
        contacto.setTipoTelefono(TipoTelefono.CELULAR);
        cliente.getContactos().add(contacto);
        return alquiler;
    }

    private Alquiler crearAlquilerSinContacto() {
        return crearAlquilerBase();
    }

    private Alquiler crearAlquilerBase() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");

        CaracteristicaVehiculo caracteristica = new CaracteristicaVehiculo();
        caracteristica.setMarca("Toyota");
        caracteristica.setModelo("Corolla");

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente("AB123CD");
        vehiculo.setCaracteristicaVehiculo(caracteristica);

        Alquiler alquiler = new Alquiler();
        alquiler.setId("alquiler-1");
        alquiler.setCliente(cliente);
        alquiler.setVehiculo(vehiculo);
        alquiler.setFechaHasta(LocalDate.now().plusDays(1));
        return alquiler;
    }
}
