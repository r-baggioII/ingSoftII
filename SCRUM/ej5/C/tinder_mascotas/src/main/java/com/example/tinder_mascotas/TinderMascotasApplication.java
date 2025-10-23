package com.example.tinder_mascotas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TinderMascotasApplication {

	public static void main(String[] args) {
		SpringApplication.run(TinderMascotasApplication.class, args);
	}

}
