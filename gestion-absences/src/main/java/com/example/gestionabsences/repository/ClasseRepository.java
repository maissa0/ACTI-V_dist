package com.example.gestionabsences.repository;

import com.example.gestionabsences.entity.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {

    List<Classe> findByNiveau(Integer niveau);
    List<Classe> findByNomContaining(String nomPartiel);
}