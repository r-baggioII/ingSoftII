package com.example.greedy_gym.servicios;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.greedy_gym.entidades.Socio;
import com.example.greedy_gym.repositorios.EmpleadoRepositorio;
import com.example.greedy_gym.repositorios.SocioRepositorio;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class NotificadorCumpleaniosServicioTest {

    private static final String ZONA = "America/Argentina/Buenos_Aires";
    private static final String CORREO_REMITENTE = "test@greedy-gym.com";

    @Mock
    private SocioRepositorio socioRepositorio;

    @Mock
    private EmpleadoRepositorio empleadoRepositorio;

    @Mock
    private JavaMailSender javaMailSender;

    private NotificadorCumpleaniosServicio notificador;

    @BeforeEach
    void setUp() {
        notificador = new NotificadorCumpleaniosServicio(
                socioRepositorio,
                empleadoRepositorio,
                javaMailSender,
                CORREO_REMITENTE,
                ZONA);
    }

    @Test
    void enviaFelicitacionesProgramadas() {
        LocalDate hoy = LocalDate.now(ZoneId.of(ZONA));

        Socio socio = new Socio();
        socio.setNombre("Juan");
        socio.setApellido("Perez");
        socio.setCorreoElectronico("juan@example.com");
        socio.setFechaNacimiento(hoy);

        when(socioRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc())
                .thenReturn(List.of(socio));
        when(empleadoRepositorio.findByEliminadoFalseOrderByApellidoAscNombreAsc())
                .thenReturn(Collections.emptyList());

        notificador.enviarFelicitacionesProgramadas();

        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
