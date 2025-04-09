package com.example.evaluationvolontaireservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class EvaluationVolontaireServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EvaluationVolontaireServiceApplication.class, args);
	}

}
