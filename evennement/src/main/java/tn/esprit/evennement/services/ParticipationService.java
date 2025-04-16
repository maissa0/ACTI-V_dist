package tn.esprit.evennement.services;

import tn.esprit.evennement.entity.Partcipation;

import java.util.List;

public interface ParticipationService {
    List<Partcipation> getAllParticipations();
    Partcipation getParticipationById(Long id);
    List<Partcipation> getParticipationsByUserId(Long userId);
    List<Partcipation> getParticipationsByEventId(Long eventId);
    Partcipation createParticipation(Long eventId, Long userId);
    void deleteParticipation(Long id, Long userId);
    boolean hasUserParticipated(Long eventId, Long userId);
} 