package com.example.missions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient

public class MissionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(MissionsApplication.class, args);
	}

}
