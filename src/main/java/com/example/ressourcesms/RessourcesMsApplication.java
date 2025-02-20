package com.example.ressourcesms;

import com.example.ressourcesms.Entities.Ressource;
import com.example.ressourcesms.Entities.TypeRessources;
import com.example.ressourcesms.Repositories.RessourceRepo;
import com.example.ressourcesms.Repositories.TyepRessourceRepo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class RessourcesMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RessourcesMsApplication.class, args);
    }


}
