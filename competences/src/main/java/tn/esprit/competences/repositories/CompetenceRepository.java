package tn.esprit.competences.repositories;

import tn.esprit.competences.entities.Competence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetenceRepository extends JpaRepository<Competence, Long> {
    List<Competence> findByUserId(Long userId);
} 