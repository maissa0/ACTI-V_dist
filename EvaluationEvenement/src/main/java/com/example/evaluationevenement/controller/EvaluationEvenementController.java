package com.example.evaluationevenement.controller;

import com.example.evaluationevenement.Entity.EvaluationEvenement;
import com.example.evaluationevenement.clients.UserAuthClient;
import com.example.evaluationevenement.service.EvaluationEvenementService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/evaluations")
public class EvaluationEvenementController {
    private final EvaluationEvenementService service;

    private final UserAuthClient userAuthClient;
    public EvaluationEvenementController(EvaluationEvenementService service, UserAuthClient userAuthClient) {
        this.service = service;
        this.userAuthClient = userAuthClient;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

    @PostMapping("/create")
    public EvaluationEvenement createEvenement(@RequestBody EvaluationEvenement evaluation) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            try {
                evaluation.setUserId(userId);
            } catch (Exception e) {
                try {
                    java.lang.reflect.Field field = EvaluationEvenement.class.getDeclaredField("userId");
                    field.setAccessible(true);
                    field.set(evaluation, userId);
                } catch (Exception ex) {
                    System.err.println("Error setting userId: " + ex.getMessage());
                }
            }
        }
        return service.saveEvenement(evaluation);
    }

    @PostMapping("/evenement/{evenementId}/create")
    public EvaluationEvenement createEvenementWithIdFromPath(
            @PathVariable Long evenementId,
            @RequestBody EvaluationEvenement evaluation,
            @RequestHeader("Authorization") String token) {

        Long userId = evaluation.getUserId();

        if (userId == null) {
            // Utiliser le token pour récupérer l'userId via le client Feign
            userId = userAuthClient.getUserIdByUsername2(token);
            evaluation.setUserId(userId);
        }

        evaluation.setEvenementId(evenementId);
        return service.saveEvenement(evaluation, evenementId, userId);
    }
    @GetMapping("/all")
    public List<EvaluationEvenement> getAllEvenements() {
        return service.getAllEvenements();
    }

    @GetMapping("/{id}")
    public Optional<EvaluationEvenement> getEvenementById(@PathVariable String id) {
        return service.getEvenementById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteEvenement(@PathVariable String id) {
        Long userId = getCurrentUserId();
        service.deleteEvenement(id, userId);
    }

    @DeleteMapping("/evenement/{evenementId}/delete")
    public void deleteEvenementByEvenementId(@PathVariable Long evenementId) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            service.deleteEvenementByEvenementIdAndUserId(evenementId, userId);
        }
    }

    @PutMapping("/update/{id}")
    public EvaluationEvenement updateEvenement(@PathVariable String id, @RequestBody EvaluationEvenement evaluation) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            evaluation.setUserId(userId);
        }
        return service.updateEvenement(id, evaluation, userId);
    }

    @PutMapping("/evenement/{evenementId}/update/{id}")
    public EvaluationEvenement updateEvenementWithIdFromPath(
            @PathVariable String id, 
            @PathVariable Long evenementId,
            @RequestBody EvaluationEvenement evaluation) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return service.updateEvenement(id, evaluation, evenementId, userId);
        }
        return null;
    }

    @GetMapping("/search/note/{note}")
    public List<EvaluationEvenement> getEvenementsByNote(@PathVariable int note) {
        return service.getEvenementsByNote(note);
    }

    @GetMapping("/evenement/{evenementId}")
    public List<EvaluationEvenement> getEvenementsByEvenementId(@PathVariable Long evenementId) {
        return service.getEvenementsByEvenementId(evenementId);
    }

    @GetMapping("/evenement/{evenementId}/user")
    public List<EvaluationEvenement> getEvenementsByEvenementIdAndCurrentUser(@PathVariable Long evenementId) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return service.getEvenementsByEvenementIdAndUserId(evenementId, userId);
        }
        return List.of();
    }

    @GetMapping("/sorted/note-desc")
    public List<EvaluationEvenement> getEvenementsOrderedByNoteDesc() {
        return service.getEvenementsOrderedByNoteDesc();
    }

    @GetMapping("/my-evaluations")
    public List<EvaluationEvenement> getCurrentUserEvaluations() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return service.getEvenementsByUserId(userId);
        }
        return List.of();
    }

    @GetMapping("/rating/average")
    public double getAverageRating() {
        return service.getAverageRating();
    }

    @GetMapping("/evenement/{evenementId}/rating/average")
    public double getAverageRatingByEvenementId(@PathVariable Long evenementId) {
        return service.getAverageRatingByEvenementId(evenementId);
    }

    @GetMapping("/rating/distribution")
    public Map<Integer, Long> getRatingDistribution() {
        return service.getRatingDistribution();
    }

    @GetMapping("/evenement/{evenementId}/rating/distribution")
    public Map<Integer, Long> getRatingDistributionByEvenementId(@PathVariable Long evenementId) {
        return service.getRatingDistributionByEvenementId(evenementId);
    }

    @GetMapping("/rating/statistics")
    public Map<String, Object> getRatingStatistics() {
        return service.getRatingStatistics();
    }

    @GetMapping("/evenement/{evenementId}/rating/statistics")
    public Map<String, Object> getRatingStatisticsByEvenementId(@PathVariable Long evenementId) {
        return service.getRatingStatisticsByEvenementId(evenementId);
    }
}