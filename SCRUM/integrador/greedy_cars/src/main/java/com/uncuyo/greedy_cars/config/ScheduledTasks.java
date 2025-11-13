package com.uncuyo.greedy_cars.config;

import com.uncuyo.greedy_cars.shared.template.exception.ErrorServiceException;
import com.uncuyo.greedy_cars.shared.template.service.AlquilerService;
import com.uncuyo.greedy_cars.shared.template.service.AlquilerService.ReminderDispatchResult;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

    private final AlquilerService alquilerService;

    public ScheduledTasks(AlquilerService alquilerService,
                          @Value("${reminder.schedule.cron:0 0 9 * * ?}") String reminderCron) {
        this.alquilerService = alquilerService;
        LOGGER.info("Reminder scheduler initialized with cron {}", reminderCron);
    }

    @Scheduled(cron = "${reminder.schedule.cron:0 0 9 * * ?}")
    public void enviarRecordatoriosAutomaticos() {
        try {
            ReminderDispatchResult result = alquilerService.enviarRecordatoriosDevolucionProgramados(LocalDate.now().plusDays(1));
            LOGGER.info("Recordatorios automáticos ejecutados: {}", result);
        } catch (ErrorServiceException e) {
            LOGGER.error("Error al enviar recordatorios automáticos: {}", e.getMessage(), e);
        }
    }
}
