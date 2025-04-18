package tn.esprit.evennement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.evennement.dto.EventRequest;
import tn.esprit.evennement.entity.Evennement;
import tn.esprit.evennement.services.AuthService;
import tn.esprit.evennement.services.EvennementServiceImpl;

import java.util.List;

@RestController
//@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/events")
public class EvennementController {

    private final EvennementServiceImpl evennementService;
    private final AuthService authService;

    @Autowired
    public EvennementController(EvennementServiceImpl evennementService, AuthService authService) {
        this.evennementService = evennementService;
        this.authService = authService;
    }



    @PostMapping
    public ResponseEntity<Evennement> createEvent(@RequestHeader("Authorization") String token,
            @RequestBody EventRequest event ) {
        Long userId = authService.validateToken(token);
        event.setUserId(userId);
        return new ResponseEntity<>(evennementService.createEvent(event), HttpStatus.CREATED);
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



    @PutMapping("/{id}")
    public ResponseEntity<Evennement> updateEvent(@PathVariable Long id, @RequestBody EventRequest event, @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);

        return ResponseEntity.ok(evennementService.updateEvent(id, event, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        evennementService.deleteEvent(id, userId);
        return ResponseEntity.noContent().build();
    }
} 