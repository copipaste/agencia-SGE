package com.agencia.agencia_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgenciaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgenciaBackendApplication.class, args);
	}

}
