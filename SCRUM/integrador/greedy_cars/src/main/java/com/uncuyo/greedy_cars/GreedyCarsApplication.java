package com.uncuyo.greedy_cars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GreedyCarsApplication {

	public static void main(String[] args) {
		// TODO: habilitar @EnableAsync y @EnableScheduling cuando se activen los jobs de correo.
		SpringApplication.run(GreedyCarsApplication.class, args);
	}

}
