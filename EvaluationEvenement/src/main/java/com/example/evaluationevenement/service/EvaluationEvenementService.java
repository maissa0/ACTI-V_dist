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
        return evenementRepository.save(evaluation);
    }
    
    private void safeSetUserId(EvaluationEvenement evaluation, Long userId) {
        if (userId == null) return;
        
        try {
            evaluation.setUserId(userId);
        } catch (Exception e) {
            try {
                // Try using reflection as a fallback
                java.lang.reflect.Field field = EvaluationEvenement.class.getDeclaredField("userId");
                field.setAccessible(true);
                field.set(evaluation, userId);
            } catch (Exception ex) {
                System.err.println("Failed to set userId: " + ex.getMessage());
            }
        }
    }
    
    private void safeSetEvenementId(EvaluationEvenement evaluation, Long evenementId) {
        if (evenementId == null) return;
        
        try {
            evaluation.setEvenementId(evenementId);
        } catch (Exception e) {
            try {
                // Try using reflection as a fallback
                java.lang.reflect.Field field = EvaluationEvenement.class.getDeclaredField("evenementId");
                field.setAccessible(true);
                field.set(evaluation, evenementId);
            } catch (Exception ex) {
                System.err.println("Failed to set evenementId: " + ex.getMessage());
            }
        }
    }
    
    public EvaluationEvenement saveEvenement(EvaluationEvenement evaluation, Long evenementId, Long userId) {
        safeSetEvenementId(evaluation, evenementId);
        safeSetUserId(evaluation, userId);
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
    
    public void deleteEvenement(String id, Long userId) {
        Optional<EvaluationEvenement> evaluation = evenementRepository.findById(id);
        if (evaluation.isPresent() && evaluation.get().getUserId() != null && 
                evaluation.get().getUserId().equals(userId)) {
            evenementRepository.deleteById(id);
        }
    }
    
    public void deleteEvenementByEvenementIdAndUserId(Long evenementId, Long userId) {
        List<EvaluationEvenement> evaluations = evenementRepository.findByEvenementIdAndUserId(evenementId, userId);
        if (!evaluations.isEmpty()) {
            evaluations.forEach(ev -> evenementRepository.deleteById(ev.getId()));
        }
    }

    public EvaluationEvenement updateEvenement(String id, EvaluationEvenement evaluation) {
        if (evenementRepository.existsById(id)) {
            evaluation.setId(id);
            return evenementRepository.save(evaluation);
        }
        return null;
    }
    
    public EvaluationEvenement updateEvenement(String id, EvaluationEvenement evaluation, Long userId) {
        Optional<EvaluationEvenement> existingEvaluation = evenementRepository.findById(id);
        if (existingEvaluation.isPresent() && existingEvaluation.get().getUserId() != null && 
                existingEvaluation.get().getUserId().equals(userId)) {
            evaluation.setId(id);
            return evenementRepository.save(evaluation);
        }
        return null;
    }
    
    public EvaluationEvenement updateEvenement(String id, EvaluationEvenement evaluation, Long evenementId, Long userId) {
        Optional<EvaluationEvenement> existingEvaluation = evenementRepository.findById(id);
        if (existingEvaluation.isPresent() && existingEvaluation.get().getUserId() != null && 
                existingEvaluation.get().getUserId().equals(userId)) {
            evaluation.setId(id);
            safeSetEvenementId(evaluation, evenementId);
            safeSetUserId(evaluation, userId);
            return evenementRepository.save(evaluation);
        }
        return null;
    }

    public List<EvaluationEvenement> getEvenementsByNote(int note) {
        return evenementRepository.findByNote(note);
    }
    
    public List<EvaluationEvenement> getEvenementsByUserId(Long userId) {
        return evenementRepository.findByUserId(userId);
    }
    
    public List<EvaluationEvenement> getEvenementsByEvenementId(Long evenementId) {
        return evenementRepository.findByEvenementId(evenementId);
    }
    
    public List<EvaluationEvenement> getEvenementsByEvenementIdAndUserId(Long evenementId, Long userId) {
        return evenementRepository.findByEvenementIdAndUserId(evenementId, userId);
    }

    public List<EvaluationEvenement> getEvenementsOrderedByNoteDesc() {
        return evenementRepository.findAllByOrderByNoteDesc();
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
    
    public double getAverageRatingByEvenementId(Long evenementId) {
        List<EvaluationEvenement> evaluations = evenementRepository.findByEvenementId(evenementId);
        if (evaluations.isEmpty()) {
            return 0.0;
        }
        return evaluations.stream()
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
    
    public Map<Integer, Long> getRatingDistributionByEvenementId(Long evenementId) {
        List<EvaluationEvenement> evaluations = evenementRepository.findByEvenementId(evenementId);
        return evaluations.stream()
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
    
    public Map<String, Object> getRatingStatisticsByEvenementId(Long evenementId) {
        List<EvaluationEvenement> evaluations = evenementRepository.findByEvenementId(evenementId);
        if (evaluations.isEmpty()) {
            return Map.of(
                    "average", 0.0,
                    "totalEvaluations", 0,
                    "distribution", Map.of()
            );
        }

        IntSummaryStatistics stats = evaluations.stream()
                .mapToInt(EvaluationEvenement::getNote)
                .summaryStatistics();

        Map<Integer, Long> distribution = evaluations.stream()
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
