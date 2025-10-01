package com.example.greedy_gym.servicios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.greedy_gym.entidades.Promocion;
import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.entidades.Usuario;
import com.example.greedy_gym.repositorios.PromocionRepositorio;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PromocionServicioTest {

    @Mock
    private PromocionRepositorio promocionRepositorio;

    @Mock
    private SocioRepositorio socioRepositorio;

    @Mock
    private NotificacionServicio notificacionServicio;

    private PromocionServicio promocionServicio;

    @BeforeEach
    void setUp() {
        promocionServicio = new PromocionServicio(promocionRepositorio, socioRepositorio, notificacionServicio);
    }

    @Test
    void enviaPromocionAhoraAListaDestinatarios() {
        Promocion promocion = new Promocion();
        promocion.setId("promo-1");
        promocion.setTitulo("Promo Septiembre");
        promocion.setTexto("Hola {{nombre}}, vení a entrenar");
        promocion.setFechaEnvioPromocion(LocalDateTime.now().minusMinutes(5));
        promocion.setUsuario(new Usuario());
        promocion.setDestinatarios(new LinkedHashSet<>());

        Socio socio = new Socio();
        socio.setId("s1");
        socio.setNombre("Ana");
        socio.setApellido("Perez");
        socio.setCorreoElectronico("ana@example.com");
        promocion.getDestinatarios().add(socio);

        when(promocionRepositorio.findByIdAndEliminadoFalse("promo-1"))
                .thenReturn(Optional.of(promocion));
        when(promocionRepositorio.save(any(Promocion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        int enviados = promocionServicio.enviarPromocionAhora("promo-1");

        assertEquals(1, enviados);
        assertTrue(promocion.isEnviada());
        verify(notificacionServicio, times(1))
                .enviar("Hola Ana, vení a entrenar", "Promo Septiembre", "ana@example.com");
        ArgumentCaptor<Promocion> captor = ArgumentCaptor.forClass(Promocion.class);
        verify(promocionRepositorio, times(1)).save(captor.capture());
        assertEquals(1L, captor.getValue().getCantidadSociosEnviados());
    }
}
