package com.example.yandexBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class YandexBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(YandexBackendApplication.class, args);
	}

}
