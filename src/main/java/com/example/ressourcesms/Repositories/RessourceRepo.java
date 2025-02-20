package com.example.ressourcesms.Repositories;

import com.example.ressourcesms.Entities.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RessourceRepo extends JpaRepository<Ressource, Integer> {
}
