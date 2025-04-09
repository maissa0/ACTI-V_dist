package org.example.apigetway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication
public class ApiGetwayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGetwayApplication.class, args);
	}
	//Methode dynamique
	@Bean
	public RouteLocator getwayRoutes(RouteLocatorBuilder builder){
		return builder.routes()
			 /*id adi */	.route("id",r->r.path("/evaluations/**") //fl controllet
						.uri("lb://EvaluationEvenement")) // esm microservice
				.route("id",r->r.path("/evaluationVolontaire/**")
						.uri("lb://evaluationVolontaire"))
				.build();
	}
}
