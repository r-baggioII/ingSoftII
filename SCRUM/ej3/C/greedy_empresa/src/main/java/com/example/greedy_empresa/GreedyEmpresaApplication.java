package com.example.greedy_empresa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GreedyEmpresaApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreedyEmpresaApplication.class, args);
    }
}
