package com.example.book_services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class BookServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookServicesApplication.class, args);
	}

}
