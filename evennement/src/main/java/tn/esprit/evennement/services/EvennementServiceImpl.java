package tn.esprit.evennement.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.evennement.dto.EventRequest;
import tn.esprit.evennement.entity.Evennement;
import tn.esprit.evennement.repository.EvennementRepository;

import java.util.List;

@Service
public class EvennementServiceImpl  {

    private final EvennementRepository evennementRepository;

    @Autowired
    public EvennementServiceImpl(EvennementRepository evennementRepository) {
        this.evennementRepository = evennementRepository;
    }


    public Evennement createEvent(EventRequest request) {
        Evennement ev = new Evennement();
        ev.setName(request.getName());
        ev.setDescription(request.getDescription());
        ev.setType(request.getType());
        ev.setUserId(request.getUserId());
        ev.setDateDeb(request.getDateDeb());
        ev.setDateFin(request.getDateFin());
        return evennementRepository.save(ev);
    }

    public List<Evennement> getEventsByUserId(Long userId) {
        return evennementRepository.findByUserId(userId);
    }



    public void deleteEvent(Long id, Long userId) {
        Evennement ev = evennementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("event not found"));

        if (!ev.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to delete this event");
        }

        evennementRepository.deleteById(id);
    }


    public Evennement updateEvent(Long id, EventRequest request, Long userId) {
        Evennement ev = evennementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competence not found"));

        if (!ev.getUserId().equals(userId)) {
            throw new RuntimeException("You are not authorized to update this competence");
        }

        ev.setName(request.getName());
        ev.setDescription(request.getDescription());
        ev.setType(request.getType());
        ev.setUserId(request.getUserId());
        ev.setDateDeb(request.getDateDeb());
        ev.setDateFin(request.getDateFin());

        return evennementRepository.save(ev);
    }

    public List<Evennement> getAllEvents() {
        return evennementRepository.findAll();
    }


    public Evennement getEventById(Long id) {
        return evennementRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found with ID: " + id));
    }


























} 