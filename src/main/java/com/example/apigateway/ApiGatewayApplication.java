package com.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
    @Bean
    public RouteLocator getawayRoutes(RouteLocatorBuilder builder){
        return builder.routes().route("candidat",r->r.path("/candidats").uri("lb://JobSAE8"))
                .route("job", r->r.path("/jobs").uri(("http://localhost:8081")))
                .route("ressource", r->r.path("/ressources").uri("http://localhost:8086"))
                .build();
    }

}
