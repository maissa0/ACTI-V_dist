package tn.esprit.evennement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.evennement.entity.Partcipation;
import tn.esprit.evennement.services.AuthService;
import tn.esprit.evennement.services.ParticipationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/participations")
public class ParticipationController {

    private final ParticipationService participationService;
    private final AuthService authService;

    @Autowired
    public ParticipationController(ParticipationService participationService, AuthService authService) {
        this.participationService = participationService;
        this.authService = authService;
    }

    @GetMapping
    public ResponseEntity<List<Partcipation>> getAllParticipations() {
        return ResponseEntity.ok(participationService.getAllParticipations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Partcipation> getParticipationById(@PathVariable Long id) {
        return ResponseEntity.ok(participationService.getParticipationById(id));
    }

    @GetMapping("/my-participations")
    public ResponseEntity<List<Partcipation>> getMyParticipations(@RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        return ResponseEntity.ok(participationService.getParticipationsByUserId(userId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Partcipation>> getParticipationsByEventId(@PathVariable Long eventId) {
        return ResponseEntity.ok(participationService.getParticipationsByEventId(eventId));
    }

    @PostMapping
    public ResponseEntity<Partcipation> createParticipation(@RequestBody Map<String, Long> payload, @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        Long eventId = payload.get("eventId");
        if (eventId == null) {
            return ResponseEntity.badRequest().build();
        }
        Partcipation createdParticipation = participationService.createParticipation(eventId, userId);
        return new ResponseEntity<>(createdParticipation, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParticipation(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        participationService.deleteParticipation(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check/{eventId}")
    public ResponseEntity<Map<String, Boolean>> checkParticipation(@PathVariable Long eventId, @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        boolean hasParticipated = participationService.hasUserParticipated(eventId, userId);
        return ResponseEntity.ok(Map.of("hasParticipated", hasParticipated));
    }
} 