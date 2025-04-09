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
			 /*id adi */	.route("feedevent",r->r.path("/feedevent/**") //fl controllet
						.uri("lb://EVALUATIONEVENEMENT")) // esm microservice
				.route("feedvolontaire",r->r.path("/feedvolont/**")
						.uri("lb://EVALUATION-VOLONTAIRE-SERVICE"))
				.build();
	}
}
