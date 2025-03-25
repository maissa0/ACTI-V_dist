package com.example.gestionabsences.repository;

import com.example.gestionabsences.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {

    List<Etudiant> findByNomAndPrenom(String nom, String prenom);
    List<Etudiant> findByClasseId(Long classeId);
}
