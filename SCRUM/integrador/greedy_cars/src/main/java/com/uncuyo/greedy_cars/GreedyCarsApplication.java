package com.uncuyo.greedy_cars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class GreedyCarsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GreedyCarsApplication.class, args);
	}

}
