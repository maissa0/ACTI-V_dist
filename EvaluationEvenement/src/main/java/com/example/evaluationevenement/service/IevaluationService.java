package com.example.evaluationevenement.service;

import com.example.evaluationevenement.Entity.EvaluationEvenement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IevaluationService {
    EvaluationEvenement saveEvenement(EvaluationEvenement evaluation);
    
    EvaluationEvenement saveEvenement(EvaluationEvenement evaluation, Long evenementId, Long userId);
    
    List<EvaluationEvenement> getAllEvenements();
    
    Optional<EvaluationEvenement> getEvenementById(String id);
    
    void deleteEvenement(String id);
    
    void deleteEvenement(String id, Long userId);
    
    void deleteEvenementByEvenementIdAndUserId(Long evenementId, Long userId);
    
    EvaluationEvenement updateEvenement(String id, EvaluationEvenement evaluation);
    
    EvaluationEvenement updateEvenement(String id, EvaluationEvenement evaluation, Long userId);
    
    EvaluationEvenement updateEvenement(String id, EvaluationEvenement evaluation, Long evenementId, Long userId);
    
    List<EvaluationEvenement> getEvenementsByNote(int note);
    
    List<EvaluationEvenement> getEvenementsByUserId(Long userId);
    
    List<EvaluationEvenement> getEvenementsByEvenementId(Long evenementId);
    
    List<EvaluationEvenement> getEvenementsByEvenementIdAndUserId(Long evenementId, Long userId);
    
    List<EvaluationEvenement> getEvenementsOrderedByNoteDesc();
    
    double getAverageRating();
    
    double getAverageRatingByEvenementId(Long evenementId);
    
    Map<Integer, Long> getRatingDistribution();
    
    Map<Integer, Long> getRatingDistributionByEvenementId(Long evenementId);
    
    Map<String, Object> getRatingStatistics();
    
    Map<String, Object> getRatingStatisticsByEvenementId(Long evenementId);
}
