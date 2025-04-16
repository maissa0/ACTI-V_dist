package tn.esprit.evennement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.evennement.entity.Evennement;

import java.util.List;

@Repository
public interface EvennementRepository extends JpaRepository<Evennement, Long> {
    List<Evennement> findByUserId(Long userId);
} 