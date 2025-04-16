package com.example.ressourcesms.Repositories;

import com.example.ressourcesms.Entities.Ressource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RessourceRepo extends JpaRepository<Ressource, Integer> {
    List<Ressource> findByEventId(Long eventId);
}
