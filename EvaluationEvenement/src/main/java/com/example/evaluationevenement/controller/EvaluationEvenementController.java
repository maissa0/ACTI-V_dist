package com.example.evaluationevenement.controller;

import com.example.evaluationevenement.Entity.EvaluationEvenement;
import com.example.evaluationevenement.service.EvaluationEvenementService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/evaluations")
public class EvaluationEvenementController {
    private final EvaluationEvenementService service;

    public EvaluationEvenementController(EvaluationEvenementService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public EvaluationEvenement createEvenement(@RequestBody EvaluationEvenement evaluation) {
        return service.saveEvenement(evaluation);
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
        service.deleteEvenement(id);
    }

    @PutMapping("/update/{id}")
    public EvaluationEvenement updateEvenement(@PathVariable String id, @RequestBody EvaluationEvenement evaluation) {
        return service.updateEvenement(id, evaluation);
    }

    @GetMapping("/search/note/{note}")
    public List<EvaluationEvenement> getEvenementsByNote(@PathVariable int note) {
        return service.getEvenementsByNote(note);
    }

    @GetMapping("/sorted/note-desc")
    public List<EvaluationEvenement> getEvenementsOrderedByNoteDesc() {
        return service.getEvenementsOrderedByNoteDesc();
    }

    @GetMapping("/rating/average")
    public double getAverageRating() {
        return service.getAverageRating();
    }

    @GetMapping("/rating/distribution")
    public Map<Integer, Long> getRatingDistribution() {
        return service.getRatingDistribution();
    }

    @GetMapping("/rating/statistics")
    public Map<String, Object> getRatingStatistics() {
        return service.getRatingStatistics();
    }
}