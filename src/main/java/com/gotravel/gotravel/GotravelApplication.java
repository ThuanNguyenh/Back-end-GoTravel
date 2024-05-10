package com.gotravel.gotravel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableScheduling
@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories
public class GotravelApplication {

	public static void main(String[] args) {
		SpringApplication.run(GotravelApplication.class, args);
	}

}
