package tn.esprit.evennement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.evennement.entity.Partcipation;

import java.util.List;

@Repository
public interface ParticipationRepository extends JpaRepository<Partcipation, Long> {
    List<Partcipation> findByUserId(Long userId);
    List<Partcipation> findByEventId(Long eventId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
} 