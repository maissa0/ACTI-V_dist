package tn.esprit.evennement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.evennement.entity.Evennement;
import tn.esprit.evennement.entity.Partcipation;
import tn.esprit.evennement.repository.EvennementRepository;
import tn.esprit.evennement.repository.ParticipationRepository;

import java.util.List;

@Service
public class ParticipationServiceImpl  {

    private final ParticipationRepository participationRepository;
    private final EvennementRepository evennementRepository;

    @Autowired
    public ParticipationServiceImpl(ParticipationRepository participationRepository, EvennementRepository evennementRepository) {
        this.participationRepository = participationRepository;
        this.evennementRepository = evennementRepository;
    }


    public List<Partcipation> getAllParticipations() {
        return participationRepository.findAll();
    }


    public Partcipation getParticipationById(Long id) {
        return participationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Participation not found with ID: " + id));
    }


    public List<Partcipation> getParticipationsByUserId(Long userId) {
        return participationRepository.findByUserId(userId);
    }


    public List<Partcipation> getParticipationsByEventId(Long eventId) {
        return participationRepository.findByEventId(eventId);
    }


    public Partcipation createParticipation(Long eventId, Long userId) {
        // Check if the event exists
        Evennement event = evennementRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + eventId));
        
        // Check if the user already participates in this event
        if (participationRepository.existsByUserIdAndEventId(userId, eventId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already participates in this event");
        }
        
        // Create new participation
        Partcipation participation = new Partcipation();
        participation.setEvent(event);
        participation.setUserId(userId);
        
        return participationRepository.save(participation);
    }


    public void deleteParticipation(Long id, Long userId) {
        Partcipation participation = getParticipationById(id);
        
        // Check if the user is the owner of the participation
        if (!participation.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this participation");
        }
        
        participationRepository.deleteById(id);
    }


    public boolean hasUserParticipated(Long eventId, Long userId) {
        return participationRepository.existsByUserIdAndEventId(userId, eventId);
    }
} 