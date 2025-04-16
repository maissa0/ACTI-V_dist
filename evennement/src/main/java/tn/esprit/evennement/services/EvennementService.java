package tn.esprit.evennement.services;

import tn.esprit.evennement.entity.Evennement;

import java.util.List;

public interface EvennementService {
    List<Evennement> getAllEvents();
    Evennement getEventById(Long id);
    List<Evennement> getEventsByUserId(Long userId);
    Evennement createEvent(Evennement event, Long userId);
    Evennement updateEvent(Long id, Evennement event, Long userId);
    void deleteEvent(Long id, Long userId);
} 