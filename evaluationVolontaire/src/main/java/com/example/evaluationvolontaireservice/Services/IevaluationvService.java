package com.example.evaluationvolontaireservice.Services;

import com.example.evaluationvolontaireservice.Entity.evaluationVolontaire;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IevaluationvService {


    evaluationVolontaire saveEvenement(evaluationVolontaire evaluation);

    evaluationVolontaire saveEvenement(evaluationVolontaire evaluation, Long evenementId, Long userId);

    List<evaluationVolontaire> getAllEvenements();

    Optional<evaluationVolontaire> getEvenementById(String id);

    void deleteEvenement(String id);

    void deleteEvenement(String id, Long userId);

    void deleteEvenementByEvenementIdAndUserId(Long evenementId, Long userId);

    evaluationVolontaire updateEvenement(String id, evaluationVolontaire evaluation);

    evaluationVolontaire updateEvenement(String id, evaluationVolontaire evaluation, Long userId);

    evaluationVolontaire updateEvenement(String id, evaluationVolontaire evaluation, Long evenementId, Long userId);

    List<evaluationVolontaire> getEvenementsByNote(int note);

    List<evaluationVolontaire> getEvenementsByUserId(Long userId);

    List<evaluationVolontaire> getEvenementsByEvenementId(Long evenementId);

    List<evaluationVolontaire> getEvenementsByEvenementIdAndUserId(Long evenementId, Long userId);

    List<evaluationVolontaire> getEvenementsOrderedByNoteDesc();

    double getAverageRating();

    double getAverageRatingByEvenementId(Long evenementId);

    Map<Integer, Long> getRatingDistribution();

    Map<Integer, Long> getRatingDistributionByEvenementId(Long evenementId);

    Map<String, Object> getRatingStatistics();

    Map<String, Object> getRatingStatisticsByEvenementId(Long evenementId);
}
