package tn.esprit.evennement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.evennement.entity.Evennement;
import tn.esprit.evennement.services.AuthService;
import tn.esprit.evennement.services.EvennementService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EvennementController {

    private final EvennementService evennementService;
    private final AuthService authService;

    @Autowired
    public EvennementController(EvennementService evennementService, AuthService authService) {
        this.evennementService = evennementService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<Evennement>> getAllEvents() {
        return ResponseEntity.ok(evennementService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evennement> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(evennementService.getEventById(id));
    }

    @GetMapping("/my-events")
    public ResponseEntity<List<Evennement>> getMyEvents(@RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        return ResponseEntity.ok(evennementService.getEventsByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Evennement> createEvent(@RequestBody Evennement event, @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        Evennement createdEvent = evennementService.createEvent(event, userId);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evennement> updateEvent(@PathVariable Long id, @RequestBody Evennement event, @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        Evennement updatedEvent = evennementService.updateEvent(id, event, userId);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        evennementService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }
} 