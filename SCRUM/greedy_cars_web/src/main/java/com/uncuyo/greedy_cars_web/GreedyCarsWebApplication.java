package com.uncuyo.greedy_cars_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class GreedyCarsWebApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(GreedyCarsWebApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(GreedyCarsWebApplication.class, args);
	}

}
