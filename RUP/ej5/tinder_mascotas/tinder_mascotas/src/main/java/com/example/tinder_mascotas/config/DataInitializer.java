package com.example.tinder_mascotas.config;

import com.example.tinder_mascotas.entidades.Zona;
import com.example.tinder_mascotas.repositorios.ZonaRepositorio;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedZonas(ZonaRepositorio zonaRepositorio) {
        return args -> {
            long count = 0;
            try { count = zonaRepositorio.count(); } catch (Exception e) {
                System.out.println("[DataInitializer] Could not count zonas: " + e.getMessage());
            }
            if (count == 0) {
                Zona capital = new Zona();
                capital.setId(UUID.randomUUID().toString());
                capital.setNombre("Capital");
                capital.setDescripcion("Capital");

                Zona godoyCruz = new Zona();
                godoyCruz.setId(UUID.randomUUID().toString());
                godoyCruz.setNombre("Godoy Cruz");
                godoyCruz.setDescripcion("Godoy Cruz");

                zonaRepositorio.save(capital);
                zonaRepositorio.save(godoyCruz);

                System.out.println("[DataInitializer] Seeded zonas: Capital, Godoy Cruz");
            } else {
                System.out.println("[DataInitializer] Zonas already present: " + count);
            }
        };
    }
}
