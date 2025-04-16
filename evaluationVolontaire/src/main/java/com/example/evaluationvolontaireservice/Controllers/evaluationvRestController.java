package com.example.evaluationvolontaireservice.Controllers;

import com.example.evaluationvolontaireservice.Entity.evaluationVolontaire;
import com.example.evaluationvolontaireservice.Services.evaluationvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/evaluationVolontaire")
public class evaluationvRestController {

    private final evaluationvService service;

    public evaluationvRestController(evaluationvService service) {
        this.service = service;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return (Long) authentication.getPrincipal();
        }
        return null;
    }

@PostMapping("/create")
public evaluationVolontaire createEvenement(@RequestBody evaluationVolontaire evaluation) {
    Long userId = getCurrentUserId();
    if (userId != null) {
        try {
            evaluation.setUserId(userId);
        } catch (Exception e) {
            try {
                java.lang.reflect.Field field = evaluationVolontaire.class.getDeclaredField("userId");
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
public evaluationVolontaire createEvenementWithIdFromPath(
        @PathVariable Long evenementId,
        @RequestBody evaluationVolontaire evaluation) {
    Long userId = getCurrentUserId();
    if (userId != null) {
        return service.saveEvenement(evaluation, evenementId, userId);
    }
    try {
        evaluation.setEvenementId(evenementId);
    } catch (Exception e) {
        try {
            java.lang.reflect.Field field = evaluationVolontaire.class.getDeclaredField("evenementId");
            field.setAccessible(true);
            field.set(evaluation, evenementId);
        } catch (Exception ex) {
            System.err.println("Error setting evenementId: " + ex.getMessage());
        }
    }
    return service.saveEvenement(evaluation);
}

@GetMapping("/all")
public List<evaluationVolontaire> getAllEvenements() {
    return service.getAllEvenements();
}

@GetMapping("/{id}")
public Optional<evaluationVolontaire> getEvenementById(@PathVariable String id) {
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
public evaluationVolontaire updateEvenement(@PathVariable String id, @RequestBody evaluationVolontaire evaluation) {
    Long userId = getCurrentUserId();
    if (userId != null) {
        evaluation.setUserId(userId);
    }
    return service.updateEvenement(id, evaluation, userId);
}

@PutMapping("/evenement/{evenementId}/update/{id}")
public evaluationVolontaire updateEvenementWithIdFromPath(
        @PathVariable String id,
        @PathVariable Long evenementId,
        @RequestBody evaluationVolontaire evaluation) {
    Long userId = getCurrentUserId();
    if (userId != null) {
        return service.updateEvenement(id, evaluation, evenementId, userId);
    }
    return null;
}

@GetMapping("/search/note/{note}")
public List<evaluationVolontaire> getEvenementsByNote(@PathVariable int note) {
    return service.getEvenementsByNote(note);
}

@GetMapping("/evenement/{evenementId}")
public List<evaluationVolontaire> getEvenementsByEvenementId(@PathVariable Long evenementId) {
    return service.getEvenementsByEvenementId(evenementId);
}

@GetMapping("/evenement/{evenementId}/user")
public List<evaluationVolontaire> getEvenementsByEvenementIdAndCurrentUser(@PathVariable Long evenementId) {
    Long userId = getCurrentUserId();
    if (userId != null) {
        return service.getEvenementsByEvenementIdAndUserId(evenementId, userId);
    }
    return List.of();
}

@GetMapping("/sorted/note-desc")
public List<evaluationVolontaire> getEvenementsOrderedByNoteDesc() {
    return service.getEvenementsOrderedByNoteDesc();
}

@GetMapping("/my-evaluations")
public List<evaluationVolontaire> getCurrentUserEvaluations() {
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
