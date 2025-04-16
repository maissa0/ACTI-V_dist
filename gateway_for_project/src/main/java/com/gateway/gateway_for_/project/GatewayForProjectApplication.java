package com.gateway.gateway_for_.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayForProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayForProjectApplication.class, args);
	}

}
