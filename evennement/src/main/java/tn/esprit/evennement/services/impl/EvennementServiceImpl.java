package tn.esprit.evennement.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.evennement.entity.Evennement;
import tn.esprit.evennement.repository.EvennementRepository;
import tn.esprit.evennement.services.EvennementService;

import java.util.List;

@Service
public class EvennementServiceImpl implements EvennementService {

    private final EvennementRepository evennementRepository;

    @Autowired
    public EvennementServiceImpl(EvennementRepository evennementRepository) {
        this.evennementRepository = evennementRepository;
    }

    @Override
    public List<Evennement> getAllEvents() {
        return evennementRepository.findAll();
    }

    @Override
    public Evennement getEventById(Long id) {
        return evennementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + id));
    }

    @Override
    public List<Evennement> getEventsByUserId(Long userId) {
        return evennementRepository.findByUserId(userId);
    }

    @Override
    public Evennement createEvent(Evennement event, Long userId) {
        event.setUserId(userId);
        return evennementRepository.save(event);
    }

    @Override
    public Evennement updateEvent(Long id, Evennement eventDetails, Long userId) {
        Evennement event = getEventById(id);
        
        // Verify if the user is the owner of the event
        if (!event.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to update this event");
        }
        
        event.setName(eventDetails.getName());
        event.setDescription(eventDetails.getDescription());
        event.setType(eventDetails.getType());
        event.setDateDeb(eventDetails.getDateDeb());
        event.setDateFin(eventDetails.getDateFin());
        
        return evennementRepository.save(event);
    }

    @Override
    public void deleteEvent(Long id, Long userId) {
        Evennement event = getEventById(id);
        
        // Verify if the user is the owner of the event
        if (!event.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to delete this event");
        }
        
        evennementRepository.deleteById(id);
    }
} 