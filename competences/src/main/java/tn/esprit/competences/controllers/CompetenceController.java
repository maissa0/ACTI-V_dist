package tn.esprit.competences.controllers;

import tn.esprit.competences.dto.CompetenceRequest;
import tn.esprit.competences.entities.Competence;
import tn.esprit.competences.services.AuthService;
import tn.esprit.competences.services.CompetenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/competences")
public class CompetenceController {
    private final CompetenceService competenceService;
    private final AuthService authService;

    public CompetenceController(CompetenceService competenceService, AuthService authService) {
        this.competenceService = competenceService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<Competence> addCompetence(
            @RequestHeader("Authorization") String token,
            @RequestBody CompetenceRequest request) {
        Long userId = authService.validateToken(token);
        request.setUserId(userId);
        return ResponseEntity.ok(competenceService.addCompetence(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Competence>> getUserCompetences(@PathVariable Long userId) {
        return ResponseEntity.ok(competenceService.getUserCompetences(userId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Competence>> getMyCompetences(
            @RequestHeader("Authorization") String token) {
        Long userId = authService.validateToken(token);
        return ResponseEntity.ok(competenceService.getUserCompetences(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompetence(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Long userId = authService.validateToken(token);
        competenceService.deleteCompetence(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Competence> updateCompetence(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody CompetenceRequest request) {
        Long userId = authService.validateToken(token);
        return ResponseEntity.ok(competenceService.updateCompetence(id, request, userId));
    }
} 