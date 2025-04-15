package com.example.evaluationevenement.service;


import com.example.evaluationevenement.Entity.EvaluationEvenement;
import com.example.evaluationevenement.Repository.EvaluationEvenementRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.IntSummaryStatistics;

@Service
public class EvaluationEvenementService implements IevaluationService{
    private final EvaluationEvenementRepository evenementRepository;

    public EvaluationEvenementService(EvaluationEvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }

    public EvaluationEvenement saveEvenement(EvaluationEvenement evaluation) {
        evaluation.setUserId(EvaluationEvenement.getUserId());
        return evenementRepository.save(evaluation);
    }

    public List<EvaluationEvenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    public Optional<EvaluationEvenement> getEvenementById(String id) {
        return evenementRepository.findById(id);
    }

    public void deleteEvenement(String id) {
        evenementRepository.deleteById(id);
    }

    public EvaluationEvenement updateEvenement(String id, EvaluationEvenement evaluation) {
        if (evenementRepository.existsById(id)) {
            evaluation.setId(id);
            evaluation.setUserId(EvaluationEvenement.getUserId());
            return evenementRepository.save(evaluation);
        }
        return null;
    }

    public List<EvaluationEvenement> getEvenementsByNote(int note) {
        return evenementRepository.findByNote(note);
    }

    public List<EvaluationEvenement> getEvenementsOrderedByNoteDesc() {
        return evenementRepository.findAllByOrderByNoteDesc();
    }

    public List<EvaluationEvenement> getEvenementsByUserId() {
        return evenementRepository.findByUserId(EvaluationEvenement.getUserId());
    }

    public double getAverageRating() {
        List<EvaluationEvenement> allEvaluations = evenementRepository.findAll();
        if (allEvaluations.isEmpty()) {
            return 0.0;
        }
        return allEvaluations.stream()
                .mapToInt(EvaluationEvenement::getNote)
                .average()
                .orElse(0.0);
    }

    public Map<Integer, Long> getRatingDistribution() {
        List<EvaluationEvenement> allEvaluations = evenementRepository.findAll();
        return allEvaluations.stream()
                .collect(Collectors.groupingBy(
                        EvaluationEvenement::getNote,
                        Collectors.counting()
                ));
    }

    public Map<String, Object> getRatingStatistics() {
        List<EvaluationEvenement> allEvaluations = evenementRepository.findAll();
        if (allEvaluations.isEmpty()) {
            return Map.of(
                    "average", 0.0,
                    "totalEvaluations", 0,
                    "distribution", Map.of()
            );
        }

        IntSummaryStatistics stats = allEvaluations.stream()
                .mapToInt(EvaluationEvenement::getNote)
                .summaryStatistics();

        Map<Integer, Long> distribution = allEvaluations.stream()
                .collect(Collectors.groupingBy(
                        EvaluationEvenement::getNote,
                        Collectors.counting()
                ));

        return Map.of(
                "average", stats.getAverage(),
                "totalEvaluations", stats.getCount(),
                "highestRating", stats.getMax(),
                "lowestRating", stats.getMin(),
                "distribution", distribution
        );
    }
}
