package com.example.gestionabsences.repository;

import com.example.gestionabsences.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AbsenceRepository extends JpaRepository<Absence, Long> {

    List<Absence> findByEtudiantId(Long etudiantId);
    List<Absence> findByDateBetween(LocalDate dateDebut, LocalDate dateFin);
    List<Absence> findByJustifiee(boolean justifiee);
}