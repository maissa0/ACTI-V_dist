package com.example.evaluationvolontaireservice.Services;
import com.example.evaluationvolontaireservice.Entity.evaluationVolontaire;
import com.example.evaluationvolontaireservice.Repository.evaluationvRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class evaluationvService {

    @Autowired
    private final evaluationvRepository volentRepository;

    public evaluationvService(evaluationvRepository volentRepository) {
        this.volentRepository = volentRepository;
    }

    public evaluationVolontaire saveEvenement(evaluationVolontaire evaluation) {
        return volentRepository.save(evaluation);
    }

    private void safeSetUserId(evaluationVolontaire evaluation, Long userId) {
        if (userId == null) return;

        try {
            evaluation.setUserId(userId);
        } catch (Exception e) {
            try {
                // Try using reflection as a fallback
                java.lang.reflect.Field field = evaluationVolontaire.class.getDeclaredField("userId");
                field.setAccessible(true);
                field.set(evaluation, userId);
            } catch (Exception ex) {
                System.err.println("Failed to set userId: " + ex.getMessage());
            }
        }
    }

    private void safeSetEvenementId(evaluationVolontaire evaluation, Long evenementId) {
        if (evenementId == null) return;

        try {
            evaluation.setEvenementId(evenementId);
        } catch (Exception e) {
            try {
                // Try using reflection as a fallback
                java.lang.reflect.Field field = evaluationVolontaire.class.getDeclaredField("evenementId");
                field.setAccessible(true);
                field.set(evaluation, evenementId);
            } catch (Exception ex) {
                System.err.println("Failed to set evenementId: " + ex.getMessage());
            }
        }
    }

    public evaluationVolontaire saveEvenement(evaluationVolontaire evaluation, Long evenementId, Long userId) {
        safeSetEvenementId(evaluation, evenementId);
        safeSetUserId(evaluation, userId);
        return volentRepository.save(evaluation);
    }

    public List<evaluationVolontaire> getAllEvenements() {
        return volentRepository.findAll();
    }

    public Optional<evaluationVolontaire> getEvenementById(String id) {
        return volentRepository.findById(id);
    }

    public void deleteEvenement(String id) {
        volentRepository.deleteById(id);
    }

    public void deleteEvenement(String id, Long userId) {
        Optional<evaluationVolontaire> evaluation = volentRepository.findById(id);
        if (evaluation.isPresent() && evaluation.get().getUserId() != null &&
                evaluation.get().getUserId().equals(userId)) {
            volentRepository.deleteById(id);
        }
    }

    public void deleteEvenementByEvenementIdAndUserId(Long evenementId, Long userId) {
        List<evaluationVolontaire> evaluations = volentRepository.findByEvenementIdAndUserId(evenementId, userId);
        if (!evaluations.isEmpty()) {
            evaluations.forEach(ev -> volentRepository.deleteById(ev.getId()));
        }
    }

    public evaluationVolontaire updateEvenement(String id, evaluationVolontaire evaluation) {
        if (volentRepository.existsById(id)) {
            evaluation.setId(id);
            volentRepository.save(evaluation);        }
        return null;
    }



    public evaluationVolontaire updateEvenement(String id, evaluationVolontaire evaluation, Long userId) {
        Optional<evaluationVolontaire> existingEvaluation = volentRepository.findById(id);
        if (existingEvaluation.isPresent() && existingEvaluation.get().getUserId() != null &&
                existingEvaluation.get().getUserId().equals(userId)) {
            evaluation.setId(id);
            return volentRepository.save(evaluation);
        }
        return null;
    }

    public evaluationVolontaire updateEvenement(String id, evaluationVolontaire evaluation, Long evenementId, Long userId) {
        Optional<evaluationVolontaire> existingEvaluation = volentRepository.findById(id);
        if (existingEvaluation.isPresent() && existingEvaluation.get().getUserId() != null &&
                existingEvaluation.get().getUserId().equals(userId)) {
            evaluation.setId(id);
            safeSetEvenementId(evaluation, evenementId);
            safeSetUserId(evaluation, userId);
            return volentRepository.save(evaluation);
        }
        return null;
    }

    public List<evaluationVolontaire> getEvenementsByNote(int note) {
        return volentRepository.findByNote(note);
    }

    public List<evaluationVolontaire> getEvenementsByUserId(Long userId) {
        return volentRepository.findByUserId(userId);
    }

    public List<evaluationVolontaire> getEvenementsByEvenementId(Long evenementId) {
        return volentRepository.findByEvenementId(evenementId);
    }

    public List<evaluationVolontaire> getEvenementsByEvenementIdAndUserId(Long evenementId, Long userId) {
        return volentRepository.findByEvenementIdAndUserId(evenementId, userId);
    }

    public List<evaluationVolontaire> getEvenementsOrderedByNoteDesc() {
        return volentRepository.findAllByOrderByNoteDesc();
    }

    public double getAverageRating() {
        List<evaluationVolontaire> allEvaluations = volentRepository.findAll();
        if (allEvaluations.isEmpty()) {
            return 0.0;
        }
        return allEvaluations.stream()
                .mapToInt(evaluationVolontaire::getNote)
                .average()
                .orElse(0.0);
    }

    public double getAverageRatingByEvenementId(Long evenementId) {
        List<evaluationVolontaire> evaluations = volentRepository.findByEvenementId(evenementId);
        if (evaluations.isEmpty()) {
            return 0.0;
        }
        return evaluations.stream()
                .mapToInt(evaluationVolontaire::getNote)
                .average()
                .orElse(0.0);
    }

    public Map<Integer, Long> getRatingDistribution() {
        List<evaluationVolontaire> allEvaluations = volentRepository.findAll();
        return allEvaluations.stream()
                .collect(Collectors.groupingBy(
                        evaluationVolontaire::getNote,
                        Collectors.counting()
                ));
    }

    public Map<Integer, Long> getRatingDistributionByEvenementId(Long evenementId) {
        List<evaluationVolontaire> evaluations = volentRepository.findByEvenementId(evenementId);
        return evaluations.stream()
                .collect(Collectors.groupingBy(
                        evaluationVolontaire::getNote,
                        Collectors.counting()
                ));
    }

    public Map<String, Object> getRatingStatistics() {
        List<evaluationVolontaire> allEvaluations = volentRepository.findAll();
        if (allEvaluations.isEmpty()) {
            return Map.of(
                    "average", 0.0,
                    "totalEvaluations", 0,
                    "distribution", Map.of()
            );
        }

        IntSummaryStatistics stats = allEvaluations.stream()
                .mapToInt(evaluationVolontaire::getNote)
                .summaryStatistics();

        Map<Integer, Long> distribution = allEvaluations.stream()
                .collect(Collectors.groupingBy(
                        evaluationVolontaire::getNote,
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
        List<evaluationVolontaire> evaluations = volentRepository.findByEvenementId(evenementId);
        if (evaluations.isEmpty()) {
            return Map.of(
                    "average", 0.0,
                    "totalEvaluations", 0,
                    "distribution", Map.of()
            );
        }

        IntSummaryStatistics stats = evaluations.stream()
                .mapToInt(evaluationVolontaire::getNote)
                .summaryStatistics();

        Map<Integer, Long> distribution = evaluations.stream()
                .collect(Collectors.groupingBy(
                        evaluationVolontaire::getNote,
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