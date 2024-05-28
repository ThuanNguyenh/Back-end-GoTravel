package com.gotravel.gotravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories
@ComponentScan(basePackages = "com.gotravel.gotravel")
public class GotravelApplication {

	public static void main(String[] args) {
		SpringApplication.run(GotravelApplication.class, args);
	}

}
