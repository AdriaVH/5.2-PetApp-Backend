package com.virtualpets.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VirtualPetsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(VirtualPetsBackendApplication.class, args);
	}

}
